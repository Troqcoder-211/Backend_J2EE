package j2ee.ourteam.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import j2ee.ourteam.entities.Device;
import j2ee.ourteam.entities.RefreshToken;
import j2ee.ourteam.entities.User;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);
    @Transactional
    void deleteByToken(String token);
    @Transactional
    void deleteByUser(User user);
    @Transactional
    void deleteByUserAndDevice(User user, Device device);
    @Transactional
    void deleteAllByUserId(UUID userId);
}
