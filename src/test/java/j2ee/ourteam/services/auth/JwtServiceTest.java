package j2ee.ourteam.services.auth;

import j2ee.ourteam.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private JwtService jwtService;
    private User testUser;
    private UUID deviceId;

    private final String SECRET = "0123456789ABCDEF0123456789ABCDEF"; // 32 bytes
    private final long ACCESS_EXP = 1000 * 60;  // 1 minute
    private final long REFRESH_EXP = 1000 * 60 * 60; // 1 hour

    @BeforeEach
    void setup() {
        jwtService = new JwtService(SECRET, ACCESS_EXP, REFRESH_EXP);

        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setUserName("testuser");

        deviceId = UUID.randomUUID();
    }

    @Test
    void testGenerateAccessToken() {
        String token = jwtService.generateAccessToken(testUser, deviceId);
        assertNotNull(token);

        String username = jwtService.extractUsername(token);
        assertEquals("testuser", username);

        assertEquals(testUser.getId(), jwtService.extractUserId(token));
        assertEquals(deviceId, jwtService.extractDeviceId(token));
    }

    @Test
    void testGenerateRefreshToken() {
        String token = jwtService.generateRefreshToken(testUser, deviceId);

        assertNotNull(token);
        assertEquals("testuser", jwtService.extractUsername(token));
    }

    @Test
    void testExtractUsername() {
        String token = jwtService.generateAccessToken(testUser, deviceId);
        String username = jwtService.extractUsername(token);
        assertEquals("testuser", username);
    }

    @Test
    void testExtractUserId() {
        String token = jwtService.generateAccessToken(testUser, deviceId);
        UUID userId = jwtService.extractUserId(token);
        assertEquals(testUser.getId(), userId);
    }

    @Test
    void testExtractDeviceId() {
        String token = jwtService.generateAccessToken(testUser, deviceId);
        UUID extracted = jwtService.extractDeviceId(token);
        assertEquals(deviceId, extracted);
    }

    @Test
    void testExtractExpiration() {
        String token = jwtService.generateAccessToken(testUser, deviceId);
        Date exp = jwtService.extractExpiration(token);
        assertTrue(exp.after(new Date()));
    }

    @Test
    void testValidateToken_valid() {
        String token = jwtService.generateAccessToken(testUser, deviceId);
        assertTrue(jwtService.validateToken(token));
    }

    @Test
    void testValidateToken_invalid() {
        assertFalse(jwtService.validateToken("invalid.token.example"));
    }

    @Test
    void testIsTokenValid_true() {
        String token = jwtService.generateAccessToken(testUser, deviceId);
        assertTrue(jwtService.isTokenValid(token, testUser));
    }

    @Test
    void testIsTokenValid_wrongUser() {
        String token = jwtService.generateAccessToken(testUser, deviceId);

        User otherUser = new User();
        otherUser.setUserName("another");

        assertFalse(jwtService.isTokenValid(token, otherUser));
    }

    @Test
    void testIsTokenValid_expired() throws InterruptedException {
        // Expiration = 10 ms
        JwtService shortJwtService = new JwtService(SECRET, 10, REFRESH_EXP);

        String token = shortJwtService.generateAccessToken(testUser, deviceId);

        Thread.sleep(20); // cho token hết hạn

        assertFalse(shortJwtService.isTokenValid(token, testUser));
    }
}
