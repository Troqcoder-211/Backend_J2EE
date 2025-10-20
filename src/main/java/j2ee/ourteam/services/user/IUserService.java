package j2ee.ourteam.services.user;

import java.util.Optional;
import java.util.UUID;

import j2ee.ourteam.entities.User;
import j2ee.ourteam.interfaces.GenericCrudService;
import j2ee.ourteam.models.user.GetUserListRequestDTO;

public interface IUserService extends GenericCrudService<User, Object, Object, UUID> {
    Optional<Object> findByName(GetUserListRequestDTO request);
}
