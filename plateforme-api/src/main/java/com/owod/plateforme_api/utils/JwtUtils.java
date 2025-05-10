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
import java.util.Set;
import java.util.stream.Collectors;

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
     * Method to generate a token containing the email and roles
     * @param user
     * @return a string container the generated token
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
     * Method that checks if token is valid
     * @param token
     * @return
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.err.println("Token expiré");
        } catch (SignatureException e) {
            System.err.println("Signature invalide");
        } catch (Exception e) {
            System.err.println("Token invalide");
        }
        return false;
    }


    /**
     * Method to retrive username from a token
     * @param token
     * @return
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Récupération des éléments du token
     * @param token
     * @return
     */
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Récuperation des rôles à partir du token
     * @param token
     * @return
     */
    public List<String> getRolesFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        Object rawRoles = claims.get("roles");
        if (rawRoles instanceof List<?>) {
            // on convertit chaque élément en String
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) rawRoles;
            return list.stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }


    /**
     * Method that create a secure cookie
     * @param name
     * @param value
     * @param maxAge
     * @param secure
     * @return
     */
    public Cookie createCookie(String name, String value, int maxAge, boolean secure) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(cookieHttpOnly);
        cookie.setSecure(cookieSecure);
        if(sameSite) {
            cookie.setAttribute("SameSite", "None");
        }
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        return cookie;
    }
}
