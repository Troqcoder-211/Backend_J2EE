package j2ee.ourteam.services.auth;

import j2ee.ourteam.BaseTest;
import j2ee.ourteam.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class JwtServiceTest extends BaseTest {

    private JwtService jwtService;
    private User testUser;
    private UUID deviceId;

    private final String secret = "12345678901234567890123456789012"; // 32 bytes key
    private final long accessExpiration = 1000L * 60 * 60; // 1 hour
    private final long refreshExpiration = 1000L * 60 * 60 * 24 * 7; // 7 days

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(secret, accessExpiration, refreshExpiration);

        testUser = User.builder()
                .id(UUID.randomUUID())
                .userName("testuser")
                .build();

        deviceId = UUID.randomUUID();
    }

    @Test
    void generateAccessToken_andValidate() {
        String token = jwtService.generateAccessToken(testUser, deviceId);

        assertThat(token).isNotBlank();
        assertThat(jwtService.validateToken(token)).isTrue();
        assertThat(jwtService.isTokenValid(token, testUser)).isTrue();
        assertThat(jwtService.extractUsername(token)).isEqualTo("testuser");
        assertThat(jwtService.extractDeviceId(token)).isEqualTo(deviceId);
        assertThat(jwtService.extractUserId(token)).isEqualTo(testUser.getId());
        assertThat(jwtService.extractExpiration(token)).isAfter(new Date());
    }

    @Test
    void generateRefreshToken_andValidate() {
        String token = jwtService.generateRefreshToken(testUser, deviceId);

        assertThat(token).isNotBlank();
        assertThat(jwtService.validateToken(token)).isTrue();
        assertThat(jwtService.isTokenValid(token, testUser)).isTrue();
        assertThat(jwtService.extractUsername(token)).isEqualTo("testuser");
        assertThat(jwtService.extractDeviceId(token)).isEqualTo(deviceId);
        assertThat(jwtService.extractUserId(token)).isEqualTo(testUser.getId());
        assertThat(jwtService.extractExpiration(token)).isAfter(new Date());
    }

    @Test
    void validateToken_invalidToken_shouldReturnFalse() {
        String invalidToken = "abc.def.ghi";

        assertThat(jwtService.validateToken(invalidToken)).isFalse();
    }

    @Test
    void isTokenValid_invalidToken_shouldReturnFalse() {
        String invalidToken = "abc.def.ghi";

        assertThat(jwtService.isTokenValid(invalidToken, testUser)).isFalse();
    }

    @Test
    void isTokenValid_wrongUser_shouldReturnFalse() {
        String token = jwtService.generateAccessToken(testUser, deviceId);

        User otherUser = User.builder().id(UUID.randomUUID()).userName("otheruser").build();

        assertThat(jwtService.isTokenValid(token, otherUser)).isFalse();
    }
}
