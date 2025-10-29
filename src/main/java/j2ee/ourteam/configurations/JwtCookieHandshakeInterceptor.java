package j2ee.ourteam.configurations;

import j2ee.ourteam.services.auth.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;


import java.util.Arrays;
import java.util.Map;
import java.util.UUID;


@Component
@RequiredArgsConstructor
public class JwtCookieHandshakeInterceptor implements HandshakeInterceptor {

    private JwtService jwtService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest servletReq) {
            HttpServletRequest http = servletReq.getServletRequest();
            Cookie[] cookies = http.getCookies();
            if (cookies == null) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return false;
            }
            String token = Arrays.stream(cookies)
                    .filter(c -> "access_token".equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
            if (token == null || !jwtService.validateToken(token)) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return false;
            }
            String userId = jwtService.extractUserId(token).toString();
            attributes.put("userId", userId);
            return true;
        }
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return false;
    }


    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {}
}