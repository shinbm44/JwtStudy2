package org.example.jwt2.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.jwt2.Entity.UserEntity;
import org.example.jwt2.dto.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil JwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //access키에 담긴 토큰 꺼내기
        String accessToken = request.getHeader("access");

        if (accessToken == null ) {
            filterChain.doFilter(request, response);
            return;
        }


        // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        try {
            JwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {

            PrintWriter writer = response.getWriter();
            writer.print("액세스 토큰 만료");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            return;
        }


        // 토큰이 access인지 확인 (발급시 페이로드에 명시)
        String tokencategory = JwtUtil.getCategory(accessToken);

        if(!tokencategory.equals("access")) {

            PrintWriter writer = response.getWriter();
            writer.print("유효하지 않은 토큰");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String username = JwtUtil.getUsername(accessToken);
        String role = JwtUtil.getRole(accessToken);

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setRole(role);
        CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);

    }
}
