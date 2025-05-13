package com.owod.plateforme_api.utils;

import com.owod.plateforme_api.models.entities.Role;
import com.owod.plateforme_api.models.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.Cookie;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility component for generating, validating, and parsing JSON Web Tokens (JWT)
 * and for creating secure HTTP cookies to store them.
 */
@Component
public class JwtUtils {

    @Value("${owod.plateforme-api.jwtSecret}")
    private String jwtSecret;

    @Value("${owod.plateforme-api.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Value("${cookie.secure}")
    private boolean cookieSecure;

    @Value("${cookie.httpOnly}")
    private boolean cookieHttpOnly;

    @Value("${cookie.sameSite}")
    private boolean sameSite;

    /**
     * Generates a JWT containing the user's ID as subject and their roles as a claim.
     * Token is signed using HS512 algorithm and expires after configured milliseconds.
     *
     * @param user the User entity for which to generate the token
     * @return the generated JWT string
     */
    public String generateToken(User user) {
        List<String> roles = user.getRoles().stream()
                .map(Role::authority)
                .toList();

        return Jwts.builder()
                .setSubject(user.getUserId())
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    /**
     * Validates the provided JWT string by parsing it with the signing key.
     *
     * @param token the JWT to validate
     * @return true if the token is valid and not expired; false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.err.println("Token expired");
        } catch (SignatureException e) {
            System.err.println("Invalid JWT signature");
        } catch (Exception e) {
            System.err.println("Invalid JWT token");
        }
        return false;
    }

    /**
     * Extracts the subject (user ID) from the JWT.
     *
     * @param token the JWT string
     * @return the user ID contained in the token subject
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Parses all claims stored in the JWT.
     *
     * @param token the JWT string
     * @return the Claims object containing all token claims
     */
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Retrieves the roles claim from the JWT as a list of strings.
     *
     * @param token the JWT string
     * @return list of role authority strings, or empty list if none present
     */
    public List<String> getRolesFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        Object rawRoles = claims.get("roles");
        if (rawRoles instanceof List<?>) {
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) rawRoles;
            return list.stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * Creates a secure HTTP cookie for storing the JWT.
     * Sets HttpOnly, secure, SameSite attributes, path, and max age.
     *
     * @param name   the cookie name
     * @param value  the cookie value (e.g., JWT)
     * @param maxAge the maximum age of the cookie in seconds
     * @param secure flag indicating if the cookie should be marked Secure
     * @return the configured Cookie object
     */
    public Cookie createCookie(String name, String value, int maxAge, boolean secure) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(cookieHttpOnly);
        cookie.setSecure(cookieSecure);
        if (sameSite) {
            cookie.setAttribute("SameSite", "None");
        }
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        return cookie;
    }
}
