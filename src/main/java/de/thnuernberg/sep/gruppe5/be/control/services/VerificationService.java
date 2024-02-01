package de.thnuernberg.sep.gruppe5.be.control.services;

import de.thnuernberg.sep.gruppe5.be.control.mapper.UserMapper;
import de.thnuernberg.sep.gruppe5.be.control.models.Credentials;
import de.thnuernberg.sep.gruppe5.be.control.models.User;
import de.thnuernberg.sep.gruppe5.be.entity.UserEntity;
import de.thnuernberg.sep.gruppe5.be.entity.VerificationToken;
import de.thnuernberg.sep.gruppe5.be.repositories.UserRepository;
import de.thnuernberg.sep.gruppe5.be.repositories.VerificationTokenRepository;
import de.thnuernberg.sep.gruppe5.be.utility.exceptions.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

@Service
@Transactional
@RequiredArgsConstructor
public class VerificationService {
  private final UserRepository userRepository;
  private final VerificationTokenRepository verificationTokenRepository;
  private final UserMapper mapper;
  private final MailService mailService;

  public void createVerificationTokenForUser(final User user, final String token) {
    final Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(new Date().getTime());
    cal.add(Calendar.MINUTE, 60 * 24);

    final VerificationToken myToken = new VerificationToken();
    myToken.setToken(token);
    myToken.setUser(mapper.toUserEntity(user));
    myToken.setExpiryDate(cal.getTime());
    verificationTokenRepository.save(myToken);
  }

  public VerificationToken validateVerificationToken(String token) {
    VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
      .orElseThrow(() -> new AppException("Dieser Code ist leider ungültig!", HttpStatus.BAD_REQUEST));

    verificationTokenRepository.delete(verificationToken);

    return verificationToken;
  }

  public void resendVerificationToken(Credentials credentials) {
    UserEntity user = userRepository.findByUsername(credentials.getUsername())
      .orElseThrow(() -> new AppException("User existiert nicht!", HttpStatus.NOT_FOUND));

    VerificationToken token = verificationTokenRepository.findByUser(user)
      .orElseThrow(() -> new AppException("Für diesen User liegt kein Bestätigungscode vor!", HttpStatus.NOT_FOUND));

    mailService.sendVerificationMail(user.getUsername(), token.getToken());
  }
}
