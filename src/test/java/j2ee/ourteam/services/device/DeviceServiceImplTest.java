package j2ee.ourteam.services.device;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DeviceServiceImplTest {

    private DeviceServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new DeviceServiceImpl();
    }

    @Test
    void testFindAll_ThrowsException() {
        UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class,
                () -> service.findAll());
        assertEquals("Unimplemented method 'findAll'", ex.getMessage());
    }

    @Test
    void testFindById_ThrowsException() {
        UUID id = UUID.randomUUID();
        UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class,
                () -> service.findById(id));
        assertEquals("Unimplemented method 'findById'", ex.getMessage());
    }

    @Test
    void testCreate_ThrowsException() {
        Object dto = new Object();
        UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class,
                () -> service.create(dto));
        assertEquals("Unimplemented method 'create'", ex.getMessage());
    }

    @Test
    void testUpdate_ThrowsException() {
        UUID id = UUID.randomUUID();
        Object dto = new Object();
        UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class,
                () -> service.update(id, dto));
        assertEquals("Unimplemented method 'update'", ex.getMessage());
    }

    @Test
    void testDeleteById_ThrowsException() {
        UUID id = UUID.randomUUID();
        UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class,
                () -> service.deleteById(id));
        assertEquals("Unimplemented method 'deleteById'", ex.getMessage());
    }
}
