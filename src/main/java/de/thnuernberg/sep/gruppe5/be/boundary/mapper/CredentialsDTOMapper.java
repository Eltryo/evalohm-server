package de.thnuernberg.sep.gruppe5.be.boundary.mapper;

import de.thnuernberg.sep.gruppe5.be.boundary.dtos.CredentialsRequestDTO;
import de.thnuernberg.sep.gruppe5.be.control.models.Credentials;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CredentialsDTOMapper {
  Credentials toCredentials(CredentialsRequestDTO credentials);
}
