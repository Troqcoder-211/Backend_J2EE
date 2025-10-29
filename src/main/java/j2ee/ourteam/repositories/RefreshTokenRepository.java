package j2ee.ourteam.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import j2ee.ourteam.entities.Device;
import j2ee.ourteam.entities.RefreshToken;
import j2ee.ourteam.entities.User;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByToken(String token);
    void deleteByUser(User user);
    void deleteByUserAndDevice(User user, Device device);
    void deleteAllByUserId(UUID userId);
}
