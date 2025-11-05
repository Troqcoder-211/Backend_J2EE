package j2ee.ourteam.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import j2ee.ourteam.entities.User;
import j2ee.ourteam.repositories.UserRepository;
import j2ee.ourteam.services.auth.JwtService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // 1️⃣ Lấy JWT từ cookie
        String accessToken = extractJwtFromCookies(request);
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2️⃣ Giải mã token, lấy username + deviceId
        String username;
        UUID deviceId;
        try {
            username = jwtService.extractUsername(accessToken);
            deviceId = jwtService.extractDeviceId(accessToken);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or malformed token");
            return;
        }

        // 3️⃣ Nếu chưa có Authentication trong context, xác thực user
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Optional<User> userOpt = userRepository.findByUserName(username);

            if (userOpt.isPresent()) {
                User user = userOpt.get();

                if (jwtService.isTokenValid(accessToken, user)) {
                    // 4️⃣ Tạo Authentication
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())));
                    // Gắn deviceId làm thông tin phụ
                    authToken.setDetails(deviceId);

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractJwtFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null)
            return null;
        for (Cookie c : request.getCookies()) {
            if ("access_token".equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }
}
