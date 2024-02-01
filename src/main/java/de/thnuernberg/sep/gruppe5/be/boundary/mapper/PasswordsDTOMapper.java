package de.thnuernberg.sep.gruppe5.be.boundary.mapper;

import de.thnuernberg.sep.gruppe5.be.boundary.dtos.PasswordsRequestDTO;
import de.thnuernberg.sep.gruppe5.be.control.models.Passwords;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PasswordsDTOMapper {
  Passwords toPasswords(PasswordsRequestDTO passwords);
}
