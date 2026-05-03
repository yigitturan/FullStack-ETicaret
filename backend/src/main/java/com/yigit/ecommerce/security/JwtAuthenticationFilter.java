package com.yigit.ecommerce.security;

import com.yigit.ecommerce.user.entity.User;
import com.yigit.ecommerce.user.repository.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String requestPath = request.getServletPath();

        System.out.println("JWT FILTER CALISTI -> path: " + requestPath);

        if (requestPath.startsWith("/api/auth")) {
            System.out.println("AUTH ENDPOINT -> token kontrolu yok");
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        System.out.println("AUTH HEADER -> " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("TOKEN YOK VEYA BEARER DEGIL");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(7);
            String email = jwtService.extractEmail(token);

            System.out.println("TOKEN EMAIL -> " + email);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                User user = userRepository.findByEmail(email).orElse(null);

                System.out.println("DB USER VAR MI -> " + (user != null));

                if (user != null && jwtService.isTokenValid(token, user)) {

                    System.out.println("TOKEN GECERLI -> SECURITY CONTEXT SET EDILIYOR");

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    user,
                                    null,
                                    List.of(new SimpleGrantedAuthority(user.getRole().name()))
                            );

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    System.out.println("TOKEN GECERSIZ VEYA USER NULL");
                }
            }

        } catch (Exception e) {
            System.out.println("JWT FILTER HATA -> " + e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}