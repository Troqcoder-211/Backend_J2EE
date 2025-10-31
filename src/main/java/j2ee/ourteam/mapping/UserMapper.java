package j2ee.ourteam.mapping;

import j2ee.ourteam.models.auth.RegisterRequestDTO;
import org.mapstruct.Mapper;

import j2ee.ourteam.entities.User;
// import j2ee.ourteam.models.auth.RegisterRequestDTO;
import j2ee.ourteam.models.user.UserProfileResponseDTO;
import j2ee.ourteam.models.user.UserResponseDTO;

@Mapper(componentModel = "spring")
public interface UserMapper {

  User toCreateEntity(RegisterRequestDTO dto);

  UserProfileResponseDTO toUserProfileResponseDTO(User entity);

  UserResponseDTO toUserResponseDTO(User entity);

}
