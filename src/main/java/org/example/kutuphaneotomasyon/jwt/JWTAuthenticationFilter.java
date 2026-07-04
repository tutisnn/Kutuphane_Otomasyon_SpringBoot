package org.example.kutuphaneotomasyon.jwt;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JWTAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        log.info("JWT filter hit: method={}, uri={}, authHeaderPresent={}",
                request.getMethod(),
                request.getRequestURI(),
                request.getHeader("Authorization") != null);

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            if (header != null) {
                log.info("JWT filter skipped parsing because Authorization header is not Bearer: {}", header);
            }
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        log.info("JWT filter bearer token detected, tokenLength={}", token.length());

        try {
            String username = jwtService.extractUsername(token);
            log.info("JWT token parsed username={}", username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    log.info("JWT authentication set for username={}", username);
                }
            }
        } catch (UsernameNotFoundException e) {
            SecurityContextHolder.clearContext();
            log.warn("JWT filter user not found on uri={}, username={}", request.getRequestURI(), e.getMessage());
        } catch (JwtException | IllegalArgumentException e) {
            SecurityContextHolder.clearContext();
            log.warn("JWT filter jwt exception on uri={}: {}", request.getRequestURI(), e.getMessage());
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            log.warn("JWT filter general exception on uri={}: {}", request.getRequestURI(), e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
