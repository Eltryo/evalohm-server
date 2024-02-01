package de.thnuernberg.sep.gruppe5.be.repositories;

import de.thnuernberg.sep.gruppe5.be.entity.UserEntity;
import de.thnuernberg.sep.gruppe5.be.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
  Optional<VerificationToken> findByToken(String token);

  Optional<VerificationToken> findByUser(UserEntity user);

  Stream<VerificationToken> findAllByExpiryDateLessThan(Date now);
}
