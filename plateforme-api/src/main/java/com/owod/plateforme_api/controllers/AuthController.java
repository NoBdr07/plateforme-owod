package com.owod.plateforme_api.controllers;

import com.owod.plateforme_api.models.entities.AccountType;
import com.owod.plateforme_api.models.entities.Role;
import com.owod.plateforme_api.models.entities.User;
import com.owod.plateforme_api.models.payload.LoginRequest;
import com.owod.plateforme_api.models.payload.RegisterRequest;
import com.owod.plateforme_api.models.payload.SessionInfo;
import com.owod.plateforme_api.services.DesignerService;
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
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * REST controller for authentication operations:
 * <ul>
 *   <li>Login: issues a JWT in an HttpOnly cookie</li>
 *   <li>Logout: clears the JWT cookie</li>
 *   <li>Register: creates a new user with ROLE_USER</li>
 *   <li>Get current user info from the JWT</li>
 * </ul>
 */
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
     * Authenticates the user with given credentials.
     * On success, sets a secure, HttpOnly 'jwt' cookie containing the JWT.
     *
     * @param loginRequest contains the user's email and password
     * @param response     the HTTP response, used to add the JWT cookie
     * @return 200 OK with success message if authentication succeeds;
     *         401 Unauthorized with error message otherwise
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest,
                                   HttpServletResponse response) {
        Optional<User> optionalUser = userService.findByEmail(loginRequest.getEmail());
        if (optionalUser.isEmpty() ||
                !passwordEncoder.matches(loginRequest.getPassword(), optionalUser.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Incorrect email or password");
        }

        User user = optionalUser.get();
        String token = jwtUtils.generateToken(user);
        Cookie cookie = jwtUtils.createCookie("jwt", token, 24 * 60 * 60, true);
        response.addCookie(cookie);

        return ResponseEntity.ok("Login successful !");
    }

    /**
     * Logs the user out by clearing the 'jwt' cookie.
     *
     * @param response the HTTP response, used to clear the JWT cookie
     * @return 200 OK with success message
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = jwtUtils.createCookie("jwt", "", 0, true);
        response.addCookie(cookie);
        return ResponseEntity.ok("Logout successful");
    }

    /**
     * Registers a new user with the role ROLE_USER.
     * Duplicate emails are rejected.
     *
     * @param registerRequest contains email, password, firstname and lastname
     * @return 200 OK with success message if registration succeeds;
     *         400 Bad Request if email is already in use
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        if (userService.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Cet email est déjà utilisé.");
        }

        User newUser = new User();
        newUser.setEmail(registerRequest.getEmail());
        newUser.setFirstname(registerRequest.getFirstname());
        newUser.setLastname(registerRequest.getLastname());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        newUser.setRoles(roles);

        userService.save(newUser);
        return ResponseEntity.ok("User registered successfully");
    }

    /**
     * Retrieves the authenticated user's ID and roles from the JWT cookie.
     * Must be called with a valid 'jwt' cookie.
     *
     * @param ud the Spring Security UserDetails, injected from the SecurityContext
     * @return 200 OK with JSON containing "userId" and "roles" if authenticated;
     *         401 Unauthorized otherwise
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails ud) {
        if (ud == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String userId = ud.getUsername();
        User u = userService.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        AccountType accountType =
                u.getCompanyId() != null ? AccountType.COMPANY :
                        u.getDesignerId() != null ? AccountType.DESIGNER : AccountType.NONE;

        var body = new SessionInfo(
                u.getUserId(),
                u.getFirstname(),
                u.getLastname(),
                ud.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList(),
                accountType,
                u.getDesignerId(),
                u.getCompanyId()
        );
        return ResponseEntity.ok(body);
    }
}
