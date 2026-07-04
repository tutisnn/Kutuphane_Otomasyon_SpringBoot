package org.example.kutuphaneotomasyon.Service;

import org.example.kutuphaneotomasyon.Dto.AuthRequest;
import org.example.kutuphaneotomasyon.Dto.AuthResponse;
import org.example.kutuphaneotomasyon.Dto.UserDto;

public interface AuthenticationService {

    UserDto register(AuthRequest authRequest);

    AuthResponse authenticate(AuthRequest authRequest);

}
