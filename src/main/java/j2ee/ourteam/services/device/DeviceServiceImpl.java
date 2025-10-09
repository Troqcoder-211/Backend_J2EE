package j2ee.ourteam.services.device;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import j2ee.ourteam.entities.Device;
import j2ee.ourteam.repositories.DeviceRepository;

@Service
public class DeviceServiceImpl implements IDeviceService {

  private final DeviceRepository deviceRepository;

  public DeviceServiceImpl(DeviceRepository deviceRepository) {
    this.deviceRepository = deviceRepository;
  }

  @Override
  public List<Device> findAll() {
    return deviceRepository.findAll();
  }

  @Override
  public Optional<Device> findById(UUID id) {
    return deviceRepository.findById(id);
  }

  @Override
  public Device save(Device entity) {
    return deviceRepository.save(entity);
  }

  @Override
  public void deleteById(UUID id) {
    deviceRepository.deleteById(id);
  }

}
