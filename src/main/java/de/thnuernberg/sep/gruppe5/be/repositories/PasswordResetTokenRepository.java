package de.thnuernberg.sep.gruppe5.be.repositories;

import de.thnuernberg.sep.gruppe5.be.entity.PasswordResetToken;
import de.thnuernberg.sep.gruppe5.be.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
  Optional<PasswordResetToken> findByToken(String token);

  Optional<PasswordResetToken> findByUser(UserEntity user);

  @Modifying
  @Query("delete from PasswordResetToken t where t.expiryDate <= ?1")
  void deleteAllExpiredSince(Date now);
}
