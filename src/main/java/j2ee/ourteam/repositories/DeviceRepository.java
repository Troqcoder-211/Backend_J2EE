package j2ee.ourteam.repositories;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import j2ee.ourteam.entities.Device;

public interface DeviceRepository extends JpaRepository<Device, UUID> {
  List<Device> findByUserId(UUID userId);
}
