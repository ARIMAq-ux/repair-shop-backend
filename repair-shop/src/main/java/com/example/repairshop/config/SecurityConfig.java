package com.example.repairshop.config;

import com.example.repairshop.security.JwtRequestFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.time.LocalDateTime;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(403);
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            response.getWriter().write(
                                    "{\"timestamp\":\"" + LocalDateTime.now() + "\"," +
                                            "\"status\":403," +
                                            "\"error\":\"Forbidden\"," +
                                            "\"message\":\"Доступ запрещён. Недостаточно прав\"," +
                                            "\"path\":\"" + request.getRequestURI() + "\"}");
                        })
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(401);
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            response.getWriter().write(
                                    "{\"timestamp\":\"" + LocalDateTime.now() + "\"," +
                                            "\"status\":401," +
                                            "\"error\":\"Unauthorized\"," +
                                            "\"message\":\"Требуется авторизация\"," +
                                            "\"path\":\"" + request.getRequestURI() + "\"}");
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        // Swagger
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/api-docs/**").permitAll()
                        // Auth
                        .requestMatchers("/api/auth/**").permitAll()
                        // GET: все авторизованные
                        .requestMatchers(HttpMethod.GET, "/api/**").authenticated()
                        // POST и PUT: requests и devices — все, clients и technicians — только ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/requests/**", "/api/devices/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/requests/**", "/api/devices/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/clients/**", "/api/technicians/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/clients/**", "/api/technicians/**").hasRole("ADMIN")
                        // DELETE: только ADMIN
                        .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
    @Bean public AuthenticationManager authenticationManager(AuthenticationConfiguration c) throws Exception { return c.getAuthenticationManager(); }
}