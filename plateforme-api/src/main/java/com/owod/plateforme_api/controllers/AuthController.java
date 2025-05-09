package com.owod.plateforme_api.controllers;

import com.owod.plateforme_api.models.entities.Role;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Map;
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
        String token = jwtUtils.generateToken(user);
        Cookie cookie = jwtUtils.createCookie("jwt", token, 24 * 60 * 60, true);

        response.addCookie(cookie);

        return ResponseEntity.ok("Login successful !");
    }

    /**
     * Endpoint to logout user
     * @param response
     * @return
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = jwtUtils.createCookie("jwt", "", 0, true);
        response.addCookie(cookie);
        return ResponseEntity.ok("Logout successful");
    }

    /**
     * Endpoint to register a new user
     * @param registerRequest that contains infos about user and a boolean admin
     * @return 404 if email is already taken, 200 if ok
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        if (userService.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cet email est déjà utilisé.");
        }

        User newUser = new User();
        newUser.setEmail(registerRequest.getEmail());
        newUser.setFirstname(registerRequest.getFirstname());
        newUser.setLastname(registerRequest.getLastname());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword())); // Encodage sécurisé

        Set<Role> roles = new HashSet<>(); // Utiliser HashSet pour initialiser
        roles.add(registerRequest.getAdmin() ? Role.ADMIN : Role.USER);
        newUser.setRoles(roles);

        userService.save(newUser);

        return ResponseEntity.ok("User registered successfully");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails ud) {
        if (ud == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(Map.of(
                "userId", ud.getUsername(),
                "roles", ud.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
        ));

    }
}
