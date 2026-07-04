package org.example.kutuphaneotomasyon.Service.Impl;

import org.example.kutuphaneotomasyon.Dto.AuthResponse;
import org.example.kutuphaneotomasyon.Dto.RefreshTokenRequest;
import org.example.kutuphaneotomasyon.Entity.RefreshToken;
import org.example.kutuphaneotomasyon.Repository.RefreshTokenRepository;
import org.example.kutuphaneotomasyon.Service.IRefreshTokenService;
import org.example.kutuphaneotomasyon.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements IRefreshTokenService {

    private final JwtService jwtService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenServiceImpl(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public boolean isRefreshTokenExpired(Date expiredDate) {
        return new Date().before(expiredDate);
    }

    private RefreshToken createRefreshToken(org.example.kutuphaneotomasyon.Entity.User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setRefreshToken(UUID.randomUUID().toString());
        refreshToken.setExpireDate(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 4));
        refreshToken.setUser(user);
        return refreshToken;
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        Optional<RefreshToken> optional = refreshTokenRepository.findByRefreshToken(refreshTokenRequest.getRefreshToken());
        if (optional.isEmpty()) {
            throw new RuntimeException("Refresh token not found" + refreshTokenRequest.getRefreshToken());
        }

        RefreshToken refreshToken = optional.get();
        if (!isRefreshTokenExpired(refreshToken.getExpireDate())) {
            throw new RuntimeException("Refresh token expired");
        }

        String accessToken = jwtService.generateToken(refreshToken.getUser());
        RefreshToken savedRefreshToken = refreshTokenRepository.save(createRefreshToken(refreshToken.getUser()));
        return new AuthResponse(accessToken, savedRefreshToken.getRefreshToken());
    }
}
