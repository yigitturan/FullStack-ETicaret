package com.yigit.ecommerce.security;

import com.yigit.ecommerce.user.entity.User;
import com.yigit.ecommerce.user.repository.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
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

        // hangi endpoint'e istek geldigini logluyoruz
        log.info("JWT filter calisti. path={}", requestPath);

        // auth endpointlerinde token kontrolu yapmiyoruz
        if (requestPath.startsWith("/api/auth")) {
            log.info("Auth endpoint. Token kontrolu atlandi. path={}", requestPath);
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        // token hic yoksa veya Bearer formatinda degilse devam ediyoruz
        // burada direkt hata firlatmiyoruz, security config zaten yetkiyi kontrol edecek
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Token yok veya Bearer formatinda degil. path={}", requestPath);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Bearer kismindan sonraki tokeni aliyoruz
            String token = authHeader.substring(7);

            // token icinden email bilgisini cekiyoruz
            String email = jwtService.extractEmail(token);

            log.info("Token okundu. email={}", email);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // token icindeki email ile user db'de var mi bakiyoruz
                User user = userRepository.findByEmail(email).orElse(null);

                if (user == null) {
                    log.warn("Token email bulundu ama user db'de yok. email={}", email);
                }

                if (user != null && jwtService.isTokenValid(token, user)) {

                    // role bilgisini ROLE_USER / ROLE_ADMIN formatinda veriyoruz
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    user,
                                    null,
                                    List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
                            );

                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    log.info("SecurityContext set edildi. email={}, role={}",
                            user.getEmail(), user.getRole().name());
                } else {
                    log.warn("Token gecersiz veya user null. email={}", email);
                }
            }

        } catch (Exception e) {
            // token bozuksa veya parse edilemezse context temizlenir
            log.error("JWT filter hata aldi. message={}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}