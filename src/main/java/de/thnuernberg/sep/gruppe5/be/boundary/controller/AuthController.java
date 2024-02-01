package de.thnuernberg.sep.gruppe5.be.boundary.controller;

import de.thnuernberg.sep.gruppe5.be.boundary.dtos.*;
import de.thnuernberg.sep.gruppe5.be.boundary.mapper.CredentialsDTOMapper;
import de.thnuernberg.sep.gruppe5.be.boundary.mapper.PasswordsDTOMapper;
import de.thnuernberg.sep.gruppe5.be.boundary.mapper.UserDTOMapper;
import de.thnuernberg.sep.gruppe5.be.control.services.AuthenticationService;
import de.thnuernberg.sep.gruppe5.be.control.services.VerificationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class AuthController {
  private final AuthenticationService authenticationService;
  private final VerificationService verificationService;
  private final CredentialsDTOMapper credMapper;
  private final PasswordsDTOMapper passwordsMapper;
  private final UserDTOMapper userMapper;

  @PostMapping("/login")
  public ResponseEntity<UserResponseDTO> login(@Valid @RequestBody CredentialsRequestDTO credentialsRequestDTO) {
    UserResponseDTO user = userMapper.toUserDTO(authenticationService.loginUser(credMapper.toCredentials(credentialsRequestDTO)));
    return ResponseEntity.ok(user);
  }

  @PostMapping("/register")
  public ResponseEntity<MessageResponseDTO> register(@Valid @RequestBody CredentialsRequestDTO credentialsRequestDTO) {
    authenticationService.registerUser(credMapper.toCredentials(credentialsRequestDTO));
    return ResponseEntity.created(URI.create("/confirmRegistration")).body(new MessageResponseDTO("Du hast dich erfolgreich registriert. Wir haben dir eine Email geschickt, mit einem Bestätigungs-Code, den du hier eingeben musst, um deinen Account freizuschalten."));
  }

  @PostMapping("/confirmRegistration")
  public ResponseEntity<MessageResponseDTO> confirmRegistration(@Valid @RequestBody VerificationTokenRequestDTO token) {
    authenticationService.confirmRegistration(token.token());
    return ResponseEntity.ok(new MessageResponseDTO("Dein Account wurde erfolgreich eingerichtet."));
  }

  @PostMapping("/resendRegistrationCode")
  public ResponseEntity<MessageResponseDTO> resendRegistrationCode(@Valid @RequestBody CredentialsRequestDTO credentialsRequestDTO) {
    verificationService.resendVerificationToken(credMapper.toCredentials(credentialsRequestDTO));
    return ResponseEntity.ok(new MessageResponseDTO("Wir haben dir erneut eine Email geschickt, mit einem Bestätigungs-Code, den du hier eingeben musst, um deinen Account freizuschalten."));
  }

  @PutMapping("/changePassword")
  public ResponseEntity<MessageResponseDTO> changePassword(@Valid @RequestBody PasswordsRequestDTO passwords) {
    this.authenticationService.changePassword(passwordsMapper.toPasswords(passwords));
    return ResponseEntity.ok(new MessageResponseDTO("Dein Passwort wurde geändert."));
  }

  @PostMapping("/resetPassword")
  public ResponseEntity<MessageResponseDTO> resetPassword(@Valid @Pattern(regexp = "[A-Za-z0-9.]+@th-nuernberg\\.de", message = "TH-Mail") @RequestParam String email) {
    this.authenticationService.resetPassword(email);
    return ResponseEntity.ok(new MessageResponseDTO("Wir haben dir eine Email für die Zurücksetzung deines Passworts geschickt."));
  }

  @PutMapping("/renewPassword")
  public ResponseEntity<MessageResponseDTO> renewPassword(@Valid @RequestBody PasswordsRequestDTO passwords) {
    this.authenticationService.renewPassword(passwordsMapper.toPasswords(passwords));
    return ResponseEntity.ok(new MessageResponseDTO("Dein Passwort wurde geändert."));
  }
}
