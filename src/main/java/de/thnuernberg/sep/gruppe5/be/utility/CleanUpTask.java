package de.thnuernberg.sep.gruppe5.be.utility;

import de.thnuernberg.sep.gruppe5.be.repositories.PasswordResetTokenRepository;
import de.thnuernberg.sep.gruppe5.be.repositories.UserRepository;
import de.thnuernberg.sep.gruppe5.be.repositories.VerificationTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
@Transactional
@RequiredArgsConstructor
public class CleanUpTask {
  private final VerificationTokenRepository tokenRepository;
  private final PasswordResetTokenRepository passwordResetTokenRepository;
  private final UserRepository userRepository;

  @Scheduled(cron = "${purge.cron.expression}", zone = "Europe/Berlin")
  public void purgeExpired() {
    Date now = Date.from(Instant.now());

    tokenRepository.findAllByExpiryDateLessThan(now).forEach(
      expiredToken -> {
        tokenRepository.delete(expiredToken);
        userRepository.delete(expiredToken.getUser());
      }
    );
    passwordResetTokenRepository.deleteAllExpiredSince(now);
  }
}
