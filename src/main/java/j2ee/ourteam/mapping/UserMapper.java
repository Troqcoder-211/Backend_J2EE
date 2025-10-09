package j2ee.ourteam.mapping;

import org.mapstruct.Mapper;

import j2ee.ourteam.entities.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
  User toDto(User u);

  User toEntity(User u);
}
