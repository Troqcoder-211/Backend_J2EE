package j2ee.ourteam.mapping;

import org.mapstruct.Mapper;

import j2ee.ourteam.entities.Device;

@Mapper(componentModel = "spring")
public interface DeviceMapper {
  Device toDto(Device d);

  Device toEntity(Device d);
}
