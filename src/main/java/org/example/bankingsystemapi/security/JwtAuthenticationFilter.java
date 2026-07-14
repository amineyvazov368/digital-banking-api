package org.example.bankingsystemapi.security; // Öz paket adına diqqət et

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
        final String jwt;
        final String userEmail;

        // 1. "Authorization" başlığını (header) yoxlayırıq. Boşdursa və ya "Bearer " ilə başlamırsa, növbəti filtrə keçirik.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. "Bearer " sözünü kəsib yalnız token hissəsini alırıq
        jwt = authHeader.substring(7);

        try {
            // 3. Tokendən email-i çıxarırıq
            userEmail = jwtService.extractEmail(jwt);

            // 4. Əgər email tapılıbsa və istifadəçi artıq sistemə daxil edilməyibsə (SecurityContext boşdursa)
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Bazadan (və ya yaddaşdan) istifadəçi detallarını yükləyirik
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // Tokenin etibarlı (valid) olub-olmadığını yoxlayırıq
                if (jwtService.isValid(jwt)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Spring Security-yə deyirik ki, "Bu istifadəçi artıq təsdiqləndi!"
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Token xətalı olduqda (məsələn vaxtı keçdikdə) filter zənciri qırılmasın deyə loglaya və ya xəta verə bilərsən
            logger.error("JWT doğrulama xətası: " + e.getMessage());
        }

        // 5. Sorğunu növbəti filtrə ötürürük
        filterChain.doFilter(request, response);
    }
}