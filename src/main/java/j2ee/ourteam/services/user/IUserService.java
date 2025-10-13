package j2ee.ourteam.services.user;

import java.util.UUID;

import j2ee.ourteam.entities.User;
import j2ee.ourteam.interfaces.GenericCrudService;

public interface IUserService extends GenericCrudService<User, Object, Object, UUID> {
    public boolean existsByUsername(String username);
}
