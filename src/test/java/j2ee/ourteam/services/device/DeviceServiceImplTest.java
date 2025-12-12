package j2ee.ourteam.services.device;

import j2ee.ourteam.BaseTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeviceServiceImplTest extends BaseTest {

    private final DeviceServiceImpl service = new DeviceServiceImpl();

    @Test
    void findAll_shouldThrowException() {
        assertThatThrownBy(service::findAll)
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("findAll");
    }

    @Test
    void findById_shouldThrowException() {
        assertThatThrownBy(() -> service.findById(UUID.randomUUID()))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("findById");
    }

    @Test
    void create_shouldThrowException() {
        assertThatThrownBy(() -> service.create(new Object()))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("create");
    }

    @Test
    void update_shouldThrowException() {
        assertThatThrownBy(() -> service.update(UUID.randomUUID(), new Object()))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("update");
    }

    @Test
    void deleteById_shouldThrowException() {
        assertThatThrownBy(() -> service.deleteById(UUID.randomUUID()))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("deleteById");
    }
}
