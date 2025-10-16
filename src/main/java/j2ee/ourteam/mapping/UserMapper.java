package j2ee.ourteam.mapping;

import org.mapstruct.Mapper;

import j2ee.ourteam.entities.User;
import j2ee.ourteam.models.auth.RegisterRequestDTO;

@Mapper(componentModel = "spring")
public interface UserMapper {

  User toEntity(RegisterRequestDTO dto);

  UserResponseDTO toDto(User entity);
  
}
