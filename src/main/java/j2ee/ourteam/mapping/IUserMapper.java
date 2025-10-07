package j2ee.ourteam.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import j2ee.ourteam.entities.User;
import j2ee.ourteam.models.user.UserDTO;

@Mapper
public interface IUserMapper {
  IUserMapper INSTANCE = Mappers.getMapper(IUserMapper.class);

  UserDTO userEntitytoUserDto(User user);

  User userDtotoUserEntity(UserDTO userDto);
}
