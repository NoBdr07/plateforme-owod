package com.owod.plateforme_api.utils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.Cookie;

import java.util.Date;

@Component
public class JwtUtils {

    @Value("${owod.plateforme-api.jwtSecret}")
    private String jwtSecret;

    @Value("${owod.plateforme-api.jwtExpirationMs}")
    private int jwtExpirationMs;

    /**
     * Method to generate a token containing the username
     * @param userId
     * @return a string container the generated token
     */
    public String generateToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
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
     * Method that create a secure cookie
     * @param name
     * @param value
     * @param maxAge
     * @param secure
     * @return
     */
    public Cookie createCookie(String name, String value, int maxAge, boolean secure) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true); // Empêche l'accès via JavaScript
        cookie.setSecure(false); // REMETTRE A SECURE EN PRODUCTION
        cookie.setPath("/");
        cookie.setMaxAge(maxAge); // Durée de vie du cookie
        return cookie;
    }
}
