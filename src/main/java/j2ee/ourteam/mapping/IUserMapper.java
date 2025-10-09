package j2ee.ourteam.mapping;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import j2ee.ourteam.entities.User;
import j2ee.ourteam.models.user.UserDTO;

@Mapper(componentModel = "spring")
public interface IUserMapper {

  @Mapping(source = "id", target = "Id", qualifiedByName = "uuidToString")
  @Mapping(source = "displayName", target = "fulName")
  UserDTO toDto(User user);

  @Mapping(source = "Id", target = "id", qualifiedByName = "stringToUuid")
  @Mapping(source = "fulName", target = "displayName")
  User toEntity(UserDTO userDTO);

  @Named("uuidToString")
  default String uuidToString(UUID id) {
    return id == null ? null : id.toString();
  }

  @Named("stringToUuid")
  default UUID stringToUuid(String id) {
    return (id == null || id.isEmpty()) ? null : UUID.fromString(id);
  }

}
