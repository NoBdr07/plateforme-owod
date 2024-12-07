package com.owod.plateforme_api.controllers;

import com.owod.plateforme_api.models.entities.User;
import com.owod.plateforme_api.models.payload.LoginRequest;
import com.owod.plateforme_api.models.payload.RegisterRequest;
import com.owod.plateforme_api.services.UserService;
import com.owod.plateforme_api.utils.JwtUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userService;

    /**
     * Endpoint for login
     * @param loginRequest that contains email and password
     * @param response
     * @return a ResponseEntity 200 if ok, 401 if wrong credentials
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        Optional<User> optionalUser = userService.findByEmail(loginRequest.getEmail());
        if (optionalUser.isEmpty() ||
                !passwordEncoder.matches(loginRequest.getPassword(), optionalUser.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect email or password");
        }

        User user = optionalUser.get();
        String token = jwtUtils.generateToken(user.getUsername());
        Cookie cookie = jwtUtils.createCookie("jwt", token, 24 * 60 * 60, true);

        response.addCookie(cookie);

        return ResponseEntity.ok("Login successful !");
    }

    /**
     * Endpoint to register a new user
     * @param registerRequest that contains infos about user and a boolean admin
     * @return 404 if email is already taken, 200 if ok
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        if (userService.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already taken");
        }

        User newUser = new User();
        newUser.setEmail(registerRequest.getEmail());
        newUser.setUsername(registerRequest.getUsername());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword())); // Encodage sécurisé

        Set<String> roles = new HashSet<>(); // Utiliser HashSet pour initialiser
        if (registerRequest.getAdmin()) {
            roles.add("admin");
        } else {
            roles.add("user"); // Ajouter un rôle par défaut
        }
        newUser.setRoles(roles);

        userService.save(newUser);

        return ResponseEntity.ok("User registered successfully");
    }

}
