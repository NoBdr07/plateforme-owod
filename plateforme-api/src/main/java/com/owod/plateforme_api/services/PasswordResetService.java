package com.owod.plateforme_api.services;

import com.owod.plateforme_api.models.entities.User;
import com.owod.plateforme_api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void initiatePasswordReset(String email) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(1);

        user.setResetToken(token);
        user.setResetTokenExpiry(expiryDate);
        userRepository.save(user);

        String resetLink = "http://localhost:4200/reset-password?token="+ token; // URL A CHANGER EN PROD
        emailService.sendPasswordResetEmail(email, resetLink);
    }

    public void resetPassword(String token, String newPassword) {
        userService.findByResetToken(token)
                .filter(user -> LocalDateTime.now().isBefore(user.getResetTokenExpiry()))
                .ifPresentOrElse(
                        user -> {
                            user.setPassword(passwordEncoder.encode(newPassword));
                            user.setResetToken(null);
                            user.setResetTokenExpiry(null);
                            userRepository.save(user);
                        },
                        () -> {
                            throw new UsernameNotFoundException("Invalid or expired token");
                        }
                );
    }

}












