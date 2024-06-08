package org.example.jwt2.service;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.jwt2.Entity.RefreshEntity;
import org.example.jwt2.jwt.JwtUtil;
import org.example.jwt2.repository.RefreshRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ReissueService{
    private final JwtUtil JwtUtil;
    private final RefreshRepository refreshRepository;

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
        JwtUtil.isExpired(refresh);
    } catch (ExpiredJwtException e) {
        return new ResponseEntity<>("refresh 토큰이 만료되었습니다.", HttpStatus.BAD_REQUEST);
    }

    // 토큰의 카테고리가 "refresh"인지 확인
    String category = JwtUtil.getCategory(refresh);
    if (!category.equals("refresh")) {
        return new ResponseEntity<>("유효하지 않은 refresh 토큰", HttpStatus.BAD_REQUEST);
    }

    Boolean isExist = refreshRepository.existsByRefresh(refresh);

    if (!isExist) {
        return new ResponseEntity<>("유효하지 않은 refresh 토큰", HttpStatus.BAD_REQUEST);
    }

    // 토큰에서 사용자 이름과 역할을 추출
    String username = JwtUtil.getUsername(refresh);
    String role = JwtUtil.getRole(refresh);

    // 새로운 Access 토큰 생성
    String newAccess = JwtUtil.createJwt("access", username, role, 600000L);
    String newrefresh = JwtUtil.createJwt("refresh", username, role, 86400000L);

    // Response에 새로운 Access 토큰 추가
    response.setHeader("access", newAccess);
    response.addCookie(createCookie("refresh", newrefresh));

    // DB에 기존 refresh를 지우고 다시 발급
    refreshRepository.deleteByRefresh(refresh);
    addRefreshEntity(username,refresh,86400000L);


    return new ResponseEntity<>(HttpStatus.OK);
}

    private void addRefreshEntity(String username, String refresh, long expiredtime) {
        Date date = new Date(System.currentTimeMillis() + expiredtime);
        RefreshEntity refreshEntity = new RefreshEntity();

        refreshEntity.setUsername(username);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }


    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        cookie.setHttpOnly(true);

        return cookie;
    }
}