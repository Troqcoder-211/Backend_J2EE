package j2ee.ourteam.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import j2ee.ourteam.entities.User;
import j2ee.ourteam.models.auth.RegisterRequestDTO;
import j2ee.ourteam.models.user.UserResponseDTO;

@Mapper(componentModel = "spring")
public interface UserMapper {

  User toEntity(RegisterRequestDTO dto);

  @Mapping(target = "password", ignore = true)
  UserResponseDTO toDto(User entity);
  
}
