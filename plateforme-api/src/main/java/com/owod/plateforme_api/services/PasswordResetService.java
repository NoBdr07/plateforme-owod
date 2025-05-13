package com.owod.plateforme_api.services;

import com.owod.plateforme_api.models.entities.User;
import com.owod.plateforme_api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service responsible for handling user password reset workflows.
 * <p>
 * Provides methods to initiate a password reset by generating and emailing a token,
 * and to complete the reset by validating the token and updating the password.
 */
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

    @Value("${app.frontend.url}")
    private String frontUrl;

    /**
     * Initiates the password reset process for the user with the given email.
     * <p>
     * Generates a unique token, sets its expiry, persists it to the user record,
     * and sends a reset email containing the reset link.
     *
     * @param email the email address of the user requesting password reset
     * @throws UsernameNotFoundException if no user is found with the provided email
     */
    public void initiatePasswordReset(String email) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(1);

        user.setResetToken(token);
        user.setResetTokenExpiry(expiryDate);
        userRepository.save(user);

        String resetLink = frontUrl + "/reset-password?token=" + token;
        emailService.sendPasswordResetEmail(email, resetLink);
    }

    /**
     * Resets the user's password using the provided token and new password.
     * <p>
     * Validates the token's existence and expiry, encodes and updates the password,
     * and clears the reset token and expiry fields.
     *
     * @param token       the password reset token received by the user
     * @param newPassword the new plaintext password to set
     * @throws UsernameNotFoundException if the token is invalid or expired
     */
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
                            throw new UsernameNotFoundException("Invalid or expired token: " + token);
                        }
                );
    }

}
