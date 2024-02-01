package de.thnuernberg.sep.gruppe5.be.control.services;

import de.thnuernberg.sep.gruppe5.be.control.events.OnRegistrationCompleteEvent;
import de.thnuernberg.sep.gruppe5.be.control.mapper.UserMapper;
import de.thnuernberg.sep.gruppe5.be.control.models.Credentials;
import de.thnuernberg.sep.gruppe5.be.control.models.Passwords;
import de.thnuernberg.sep.gruppe5.be.control.models.User;
import de.thnuernberg.sep.gruppe5.be.entity.PasswordResetToken;
import de.thnuernberg.sep.gruppe5.be.entity.UserEntity;
import de.thnuernberg.sep.gruppe5.be.entity.VerificationToken;
import de.thnuernberg.sep.gruppe5.be.repositories.PasswordResetTokenRepository;
import de.thnuernberg.sep.gruppe5.be.repositories.RoleRepository;
import de.thnuernberg.sep.gruppe5.be.repositories.UserRepository;
import de.thnuernberg.sep.gruppe5.be.utility.exceptions.AppException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.CharBuffer;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationService {
  private final AuthenticationManager authenticationManager;
  private final PasswordEncoder passwordEncoder;
  private final LoginAttemptService loginAttemptService;
  private final JwtTokenService jwtTokenService;
  private final VerificationService verificationService;
  private final MailService mailService;
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordResetTokenRepository passwordResetTokenRepository;
  private final UserMapper entityMapper;
  private final ApplicationEventPublisher eventPublisher;

  private static String getIP() {
    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

    String ip;

    final String xfHeader = request.getHeader("X-Forwarded-For");
    if (xfHeader == null || xfHeader.isEmpty() || !xfHeader.contains(request.getRemoteAddr())) {
      ip = request.getRemoteAddr();
    } else {
      ip = xfHeader.split(",")[0];
    }
    return ip;
  }

  public User registerUser(@Valid final Credentials credentials) {
    Optional<UserEntity> oUser = userRepository.findByUsername(credentials.getUsername());

    if (oUser.isPresent()) {
      throw new AppException("User existiert bereits!", HttpStatus.BAD_REQUEST);
    }

    UserEntity user = new UserEntity();
    user.setUsername(credentials.getUsername());
    user.setPassword(passwordEncoder.encode(CharBuffer.wrap(credentials.getPassword())));
    user.setAuthorities(Set.of(roleRepository.findByAuthority("STUDENT").get()));
    user.setEnabled(false);

    UserEntity savedUser = userRepository.save(user);

    eventPublisher.publishEvent(new OnRegistrationCompleteEvent(entityMapper.toUser(savedUser)));

    return entityMapper.toUser(savedUser);
  }

  public User loginUser(@Valid final Credentials credentials) {
    UserEntity user = userRepository.findByUsername(credentials.getUsername()).orElseThrow(() -> new AppException("Dieser Nutzer existiert nicht!", HttpStatus.NOT_FOUND));

    if (!user.isEnabled()) {
      throw new AppException("Du musst deinen Account noch freischalten!", HttpStatus.LOCKED);
    }

    String ip = getIP();

    Authentication auth = new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword());

    try {
      auth = authenticationManager.authenticate(auth);
    } catch (AuthenticationException e) {
      int remainingAttempts = loginAttemptService.loginFailed(ip);

      if (remainingAttempts == 0) {
        throw new AppException("Sie haben leider zu oft ungültige Anmeldedaten ausprobiert, weswegen wir leider weitere Anmeldeversuche von Ihnen sperren müssen!", HttpStatus.BAD_REQUEST);
      } else {
        throw new AppException("Ungültige Anmeldedaten! Verbleibende Versuche: " + remainingAttempts, HttpStatus.BAD_REQUEST);
      }
    }

    loginAttemptService.loginSuccessful(ip);

    User loggedInUser = entityMapper.toUser(userRepository.findByUsername(credentials.getUsername()).get());
    loggedInUser.setToken(jwtTokenService.generateJwt(auth));
    return loggedInUser;
  }

  public void confirmRegistration(@Valid @NotBlank final String token) {
    VerificationToken verificationToken = verificationService.validateVerificationToken(token);

    final UserEntity user = verificationToken.getUser();
    final Calendar cal = Calendar.getInstance();

    if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
      userRepository.delete(user);
      throw new AppException("Dieser Code ist leider abgelaufen! Sie müssen die Registrierung erneut durchführen.", HttpStatus.BAD_REQUEST);
    }

    user.setEnabled(true);
    userRepository.save(user);
  }

  public void changePassword(@Valid final Passwords passwords) {
    UserEntity user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
      .orElseThrow(() -> new AppException("Unbekannter Nutzer!", HttpStatus.NOT_FOUND));

    if (!passwordEncoder.matches(CharBuffer.wrap(passwords.getOldPassword()), user.getPassword())) {
      throw new AppException("Falsches Passwort!", HttpStatus.BAD_REQUEST);
    }

    user.setPassword(passwordEncoder.encode(passwords.getNewPassword()));
    userRepository.save(user);
  }

  public void renewPassword(@Valid final Passwords passwords) {
    PasswordResetToken token = passwordResetTokenRepository.findByToken(passwords.getOldPassword()).orElseThrow(() -> new AppException("Falscher Reset-Code!", HttpStatus.BAD_REQUEST));

    UserEntity user = token.getUser();
    user.setPassword(passwordEncoder.encode(passwords.getNewPassword()));
    userRepository.save(user);
  }

  public void resetPassword(@Valid @Pattern(regexp = "[A-Za-z0-9.]+@th-nuernberg\\.de") final String email) {
    UserEntity user = userRepository.findByUsername(email).orElseThrow(() -> new AppException("User existiert nicht!", HttpStatus.NOT_FOUND));

    final String token = UUID.randomUUID().toString();

    createTokenForPasswordReset(entityMapper.toUser(user), token);

    mailService.sendPasswordResetMail(user.getUsername(), token);
  }

  private void createTokenForPasswordReset(@Valid final User user, @Valid @NotBlank final String token) {
    final Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(new Date().getTime());
    cal.add(Calendar.MINUTE, 60 * 24);

    final PasswordResetToken myToken = passwordResetTokenRepository.findByUser(entityMapper.toUserEntity(user)).orElse(new PasswordResetToken());
    myToken.setToken(token);
    myToken.setUser(entityMapper.toUserEntity(user));
    myToken.setExpiryDate(cal.getTime());
    passwordResetTokenRepository.save(myToken);
  }
}
