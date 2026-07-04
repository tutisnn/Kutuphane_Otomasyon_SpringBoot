package org.example.kutuphaneotomasyon.Service;


import jakarta.mail.MessagingException;
import org.example.kutuphaneotomasyon.Dto.LoginUserDto;
import org.example.kutuphaneotomasyon.Dto.RegisterUserDto;
import org.example.kutuphaneotomasyon.Dto.ResetPasswordDto;
import org.example.kutuphaneotomasyon.Dto.VerifyUserDto;
import org.example.kutuphaneotomasyon.Entity.User;
import org.example.kutuphaneotomasyon.Repository.UserRepository;
import org.example.kutuphaneotomasyon.Service.Impl.EmailService;
import org.example.kutuphaneotomasyon.jwt.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.example.kutuphaneotomasyon.ResponseMessage.LoginResponse;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final JwtService jwtService; // ✅ EKLENDİ


    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            EmailService emailService,
             JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.jwtService = jwtService;


    }

    public User signup(RegisterUserDto input) {
        User user = new User(input.getUsername(), input.getEmail(), passwordEncoder.encode(input.getPassword()));
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        user.setRole(User.Role.STUDENT);
        user.setEnabled(false);
        sendVerificationEmail(user);
        return userRepository.save(user);
    }



    public LoginResponse authenticate(LoginUserDto input) {
        User user = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isEnabled()) {
            throw new RuntimeException("Account not verified. Please verify your account.");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        // 🔥 Token'a role bilgisi ekleniyor
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", user.getRole().name()); // Örn: ADMIN, STUDENT

        String token = jwtService.generateToken(extraClaims, user);

        // ✨ LoginResponse objesi döndürülüyor
        return new LoginResponse(token, jwtService.getExpirationTime());
    }


    public void verifyUser(VerifyUserDto input) {
        Optional<User> optionalUser = userRepository.findByEmail(input.getEmail());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Verification code has expired");
            }
            if (user.getVerificationCode().equals(input.getVerificationCode())) {
                user.setEnabled(true);
                user.setVerificationCode(null);
                user.setVerificationCodeExpiresAt(null);
                userRepository.save(user);
            } else {
                throw new RuntimeException("Invalid verification code");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public void resendVerificationCode(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.isEnabled()) {
                throw new RuntimeException("Account is already verified");
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));
            sendVerificationEmail(user);
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    private void sendVerificationEmail(User user) { //TODO: Update with company logo
        String subject = "Account Verification";
        String verificationCode = "VERIFICATION CODE " + user.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            // Handle email sending exception
            e.printStackTrace();
        }
    }
    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
   // @Override
   public void sendForgotPasswordCode(String email) {
       User user = userRepository.findByEmail(email)
               .orElseThrow(() -> new RuntimeException("Email not found"));

       String code = generateVerificationCode();
       user.setVerificationCode(code);
       user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(10));
       userRepository.save(user);

       String subject = "Şifre Sıfırlama Kodu";
       String htmlMessage = "<html>"
               + "<body style=\"font-family: Arial, sans-serif;\">"
               + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
               + "<h2 style=\"color: #333;\">Şifre Sıfırlama İsteği</h2>"
               + "<p style=\"font-size: 16px;\">Aşağıdaki kod ile şifrenizi yenileyebilirsiniz:</p>"
               + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; "
               + "box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
               + "<h3 style=\"color: #333;\">Kod:</h3>"
               + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + code + "</p>"
               + "</div>"
               + "</div>"
               + "</body>"
               + "</html>";

       try {
           emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
       } catch (MessagingException e) {
           e.printStackTrace();
           throw new RuntimeException("Mail gönderilemedi!");
       }
   }
    //@Override
    public void resetPassword(ResetPasswordDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!dto.getVerificationCode().equals(user.getVerificationCode()) ||
                user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Invalid or expired code");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setVerificationCode(null); // sıfırla
        user.setVerificationCodeExpiresAt(null);
        userRepository.save(user);
    }
}
