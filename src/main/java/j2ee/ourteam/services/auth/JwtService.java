package j2ee.ourteam.services.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import j2ee.ourteam.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.crypto.SecretKey;

@Service
public class JwtService {

    private final SecretKey key;
    private final long ACCESS_TOKEN_EXPIRATION;
    private final long REFRESH_TOKEN_EXPIRATION;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration.access}") long accessTokenExpiration,
            @Value("${jwt.expiration.refresh}") long refreshTokenExpiration
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.ACCESS_TOKEN_EXPIRATION = accessTokenExpiration;
        this.REFRESH_TOKEN_EXPIRATION = refreshTokenExpiration;
    }

    @SuppressWarnings("deprecation")
    public String generateAccessToken(User user, UUID deviceId) {
        return Jwts.builder()
                .setSubject(user.getUserName())
                .addClaims(Map.of("deviceId", deviceId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @SuppressWarnings("deprecation")
    public String generateRefreshToken(User user, UUID deviceId) {
        return Jwts.builder()
                .setSubject(user.getUserName())
                .addClaims(Map.of("deviceId", deviceId))
                .addClaims(Map.of("userId", user.getId()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @SuppressWarnings("deprecation")
    public String extractUsername(String token) {
        return Jwts.parser().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    @SuppressWarnings("deprecation")
    public UUID extractDeviceId(String token) {
        return ((UUID) Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("deviceId"));
    }
    public UUID extractUserId(String token) {
        return (UUID) Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("userId");
    }
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }


    public Date extractExpiration(String token) {
        Claims claims = Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return claims.getExpiration();
    }
    
    @SuppressWarnings("deprecation")
    public boolean isTokenValid(String token, User user) {
        try {
            Claims claims = Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();
            String username = claims.getSubject();
            return username.equals(user.getUserName()) && claims.getExpiration().after(new Date());
        } catch (JwtException e) {
            return false;
        }
    }
}
