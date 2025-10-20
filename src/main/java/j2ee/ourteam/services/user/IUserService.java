package j2ee.ourteam.services.user;

import java.util.ArrayList;
import java.util.UUID;

import j2ee.ourteam.entities.User;
import j2ee.ourteam.interfaces.GenericCrudService;
import j2ee.ourteam.models.page.PageResponse;
import j2ee.ourteam.models.user.GetUserListRequestDTO;
import j2ee.ourteam.models.user.UpdateUserProfileDTO;
import j2ee.ourteam.models.user.UserProfileResponseDTO;
import j2ee.ourteam.models.user.UserResponseDTO;

public interface IUserService extends GenericCrudService<User, Object, Object, UUID> {
        /**
     * Lấy danh sách người dùng theo bộ lọc (phân trang, tìm kiếm, vai trò, trạng thái, v.v.)
     */
    PageResponse<UserResponseDTO> getUserList(GetUserListRequestDTO request);

    /**
     * Lấy thông tin chi tiết người dùng theo ID.
     */
    UserProfileResponseDTO getUserProfile(UUID id);

    /**
     * Cập nhật thông tin cá nhân của người dùng hiện tại.
     */
    UserProfileResponseDTO updateMyProfile(UUID currentUserId, UpdateUserProfileDTO updateDTO);

    /**
     * Vô hiệu hóa tài khoản người dùng hiện tại.
     */
    void disableUser(UUID currentUserId);

    /**
     * Kích hoạt lại tài khoản người dùng hiện tại.
     */
    void enableUser(UUID currentUserId);

    /**
     * Xóa tài khoản người dùng hiện tại.
     */
    void deleteUser(UUID currentUserId);
}
