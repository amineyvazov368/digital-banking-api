package org.example.bankingsystemapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // 1. "Authorization" yoxlaması
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Tokeni təhlükəsiz şəkildə götürürük (boşluqları təmizləyirik - trim())
        final String jwt = authHeader.substring(7).trim();

        try {
            // 3. Tokendən email-i çıxarırıq
            final String userEmail = jwtService.extractEmail(jwt);

            // 4. Əgər email tapılıbsa və hələ giriş edilməyibsə
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                if (jwtService.isValid(jwt)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Əgər token xətalıdırsa (vaxtı keçibsə və ya JWE xətası verirsə)
            // Bu loq bizə tam olaraq hansı tokenin xəta verdiyini göstərəcək
            logger.error("JWT validation failed for token: [" + jwt + "] | Error: " + e.getMessage());

            // Xəta halında istifadəçini anonim saxlayırıq və istəsən 401 qaytara bilərsən.
            // Hələlik filter zəncirini davam etdirək, Spring özü 403/401 verəcək.
        }

        filterChain.doFilter(request, response);
    }
}