package de.thnuernberg.sep.gruppe5.be.control.mapper;

import de.thnuernberg.sep.gruppe5.be.control.models.User;
import de.thnuernberg.sep.gruppe5.be.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
  User toUser(UserEntity user);

  UserEntity toUserEntity(User user);
}
