package org.example.kutuphaneotomasyon.Controller;



import org.example.kutuphaneotomasyon.Dto.*;
import org.example.kutuphaneotomasyon.Entity.User;
import org.example.kutuphaneotomasyon.ResponseMessage.LoginResponse;
import org.example.kutuphaneotomasyon.Service.AuthenticationService;
import org.example.kutuphaneotomasyon.jwt.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        LoginResponse loginResponse = authenticationService.authenticate(loginUserDto);
        return ResponseEntity.ok(loginResponse);
    }


    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDto verifyUserDto) {
        try {
            authenticationService.verifyUser(verifyUserDto);
            return ResponseEntity.ok("Account verified successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email) {
        try {
            authenticationService.resendVerificationCode(email);
            return ResponseEntity.ok("Verification code sent");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> sendResetCode(@RequestBody ForgotPasswordDto dto) {
        authenticationService.sendForgotPasswordCode(dto.getEmail());
        return ResponseEntity.ok("Verification code sent");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDto dto) {
        authenticationService.resetPassword(dto);
        return ResponseEntity.ok("Password updated successfully");
    }
}
