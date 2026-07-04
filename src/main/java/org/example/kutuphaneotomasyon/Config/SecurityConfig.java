package org.example.kutuphaneotomasyon.Config;

import org.example.kutuphaneotomasyon.jwt.AuthEntryPoint;
import org.example.kutuphaneotomasyon.jwt.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    public static final String AUTHENTICATE = "/authenticate";
    public static final String REGISTER = "/register";
    public static final String REFRESH_TOKEN = "/refreshToken";

    public static final String[] SWAGGER_PATHS = {
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-ui.html"
    };

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private AuthEntryPoint authEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(requests ->
                        requests.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .requestMatchers(AUTHENTICATE, REGISTER, REFRESH_TOKEN).permitAll()
                                .requestMatchers(SWAGGER_PATHS).permitAll()
                                .requestMatchers("/users/**").hasRole("ADMIN")
                                .requestMatchers("/rest/api/Book/system/status").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/rest/api/Book/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/rest/api/Author/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/rest/api/Category/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/rest/api/Publisher/**").permitAll()
                                .anyRequest()
                                .authenticated())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(authEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
