package com.owod.plateforme_api.security;

import com.owod.plateforme_api.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filter that intercepts incoming HTTP requests to authenticate users based on a JWT cookie.
 * <p>
 * Extracts the "jwt" cookie, validates the token, retrieves user information and roles,
 * and sets the authentication in the SecurityContext.
 */
@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * Filters each request to check for a valid JWT in cookies. If present and valid,
     * sets the authenticated user details and authorities in the security context.
     *
     * @param request     the incoming HTTP request
     * @param response    the HTTP response
     * @param filterChain the filter chain to delegate to
     * @throws ServletException in case of servlet errors
     * @throws IOException      in case of I/O errors
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Extract token from cookies
        String token = extractTokenFromCookies(request);

        if (token != null && jwtUtils.validateToken(token)) {
            // Retrieve username and roles from token
            String username = jwtUtils.getUsernameFromToken(token);
            List<String> roles = jwtUtils.getRolesFromToken(token);

            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            // Build UserDetails with authorities (password not used)
            UserDetails userDetails = org.springframework.security.core.userdetails.User
                    .withUsername(username)
                    .password("")
                    .authorities(authorities)
                    .build();

            // Create authentication token and set details
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the JWT token value from the "jwt" cookie in the request.
     *
     * @param request the HTTP request containing cookies
     * @return the JWT token string if present, or null if not found
     */
    private String extractTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
