package de.thnuernberg.sep.gruppe5.be.repositories;

import de.thnuernberg.sep.gruppe5.be.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
  Optional<UserEntity> findByUsername(String username);

  @Query("select username from UserEntity where id = ?1")
  Optional<String> getUsernameById(Integer id);
}
