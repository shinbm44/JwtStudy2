package org.example.jwt2.service;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.jwt2.jwt.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReissueService{
    private final JwtUtil jwtUtil;

    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
      // Refresh 토큰을 쿠키에서 가져오기
      String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
        if (cookie.getName().equals("refresh")) {
            refresh = cookie.getValue();
            break;  // Refresh 토큰을 찾으면 루프를 종료
        }
    }

    // Refresh 토큰이 없을 경우
    if (refresh == null) {
        return new ResponseEntity<>("refresh 토큰이 없습니다.", HttpStatus.BAD_REQUEST);
    }

    // Refresh 토큰이 만료되었는지 확인
    try {
        jwtUtil.isExpired(refresh);
    } catch (ExpiredJwtException e) {
        return new ResponseEntity<>("refresh 토큰이 만료되었습니다.", HttpStatus.BAD_REQUEST);
    }

    // 토큰의 카테고리가 "refresh"인지 확인
    String category = jwtUtil.getCategory(refresh);
    if (!category.equals("refresh")) {
        return new ResponseEntity<>("refresh 토큰이 아닙니다.", HttpStatus.BAD_REQUEST);
    }

    // 토큰에서 사용자 이름과 역할을 추출
    String username = jwtUtil.getUsername(refresh);
    String role = jwtUtil.getRole(refresh);

    // 새로운 Access 토큰 생성
    String newAccess = jwtUtil.createJwt("access", username, role, 600000L);

    // Response에 새로운 Access 토큰 추가
    response.setHeader("access", newAccess);

    return new ResponseEntity<>(HttpStatus.OK);
}
}