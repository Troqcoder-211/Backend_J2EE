package j2ee.ourteam.services.user;

import java.io.IOException;
import java.util.UUID;

import j2ee.ourteam.entities.User;
import j2ee.ourteam.interfaces.GenericCrudService;
import j2ee.ourteam.models.page.PageResponse;
import j2ee.ourteam.models.user.GetUserListRequestDTO;
import j2ee.ourteam.models.user.UpdateUserProfileDTO;
import j2ee.ourteam.models.user.UserProfileResponseDTO;
import j2ee.ourteam.models.user.UserResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface IUserService extends GenericCrudService<User, Object, Object, UUID> {

    PageResponse<UserResponseDTO> getUserList(GetUserListRequestDTO request);

    UserProfileResponseDTO getUserProfile(UUID id);

    UserProfileResponseDTO updateMyProfile(UUID currentUserId, UpdateUserProfileDTO updateDTO);

    String updateAvatar(UUID id, MultipartFile avatarFile) throws IOException;

    void disableUser(UUID currentUserId);

    void enableUser(UUID currentUserId);

    void deleteUser(UUID currentUserId);
}
