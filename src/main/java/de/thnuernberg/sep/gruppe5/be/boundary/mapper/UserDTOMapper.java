package de.thnuernberg.sep.gruppe5.be.boundary.mapper;

import de.thnuernberg.sep.gruppe5.be.boundary.dtos.UserResponseDTO;
import de.thnuernberg.sep.gruppe5.be.control.models.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserDTOMapper {
  UserResponseDTO toUserDTO(User user);

  User toUser(UserResponseDTO user);
}
