package de.thnuernberg.sep.gruppe5.be.repositories;

import de.thnuernberg.sep.gruppe5.be.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {
  Optional<RoleEntity> findByAuthority(String authority);
}
