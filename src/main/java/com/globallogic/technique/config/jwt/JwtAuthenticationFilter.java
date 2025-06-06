package com.globallogic.technique.config.jwt;

import com.globallogic.technique.config.exception.JwtAuthenticationEntryPoint;
import com.globallogic.technique.exception.token.InvalidTokenException;
import com.globallogic.technique.service.TokenValidationService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenValidationService tokenValidationService;
    private final JwtAuthenticationEntryPoint entryPoint;

    public JwtAuthenticationFilter(TokenValidationService tokenValidationService, JwtAuthenticationEntryPoint entryPoint) {
        this.tokenValidationService = tokenValidationService;
        this.entryPoint = entryPoint;
    }


    @Override
    protected void doFilterInternal(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, javax.servlet.FilterChain filterChain) throws javax.servlet.ServletException, IOException {
        try {
            String token = getTokenFromRequest(request);

            boolean isValid = tokenValidationService.validateJwtToken(token);

            if (!isValid) {
                throw new InvalidTokenException("Invalid or expired token");
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(null, null, Collections.emptyList());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (RuntimeException ex) {
            entryPoint.commence(request, response, new AuthenticationException(ex.getMessage()) {});
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !path.startsWith("/users/login");
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}