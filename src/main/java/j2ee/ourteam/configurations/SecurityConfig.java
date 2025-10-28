package j2ee.ourteam.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .anyRequest().permitAll())
        .formLogin(form -> form.disable())
        .httpBasic(basic -> basic.disable())
        .logout(logout -> logout.disable())
        .securityContext(context -> context.disable())
        .sessionManagement(session -> session.disable());

    return http.build();
  }
}
