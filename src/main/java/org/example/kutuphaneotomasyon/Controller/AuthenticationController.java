package org.example.kutuphaneotomasyon.Controller;

import jakarta.validation.Valid;
import org.example.kutuphaneotomasyon.Dto.AuthRequest;
import org.example.kutuphaneotomasyon.Dto.AuthResponse;
import org.example.kutuphaneotomasyon.Dto.RefreshTokenRequest;
import org.example.kutuphaneotomasyon.Dto.UserDto;
import org.example.kutuphaneotomasyon.Service.AuthenticationService;
import org.example.kutuphaneotomasyon.Service.IRefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private IRefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public UserDto register(@Valid @RequestBody AuthRequest authRequest) {
        return authenticationService.register(authRequest);
    }

    @PostMapping("/authenticate")
    public AuthResponse authenticate(@Valid @RequestBody AuthRequest authRequest) {
        return authenticationService.authenticate(authRequest);
    }

    @PostMapping("/refreshToken")
    public AuthResponse refreshToken(@RequestBody RefreshTokenRequest request) {
        return refreshTokenService.refreshToken(request);
    }
}
