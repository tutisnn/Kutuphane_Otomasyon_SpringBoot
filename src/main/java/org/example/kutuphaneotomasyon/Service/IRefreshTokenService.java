package org.example.kutuphaneotomasyon.Service;

import org.example.kutuphaneotomasyon.Dto.AuthResponse;
import org.example.kutuphaneotomasyon.Dto.RefreshTokenRequest;

public interface IRefreshTokenService {

    AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
}
