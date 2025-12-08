package util;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

/**
 * JWT 토큰 생성, 검증, 파싱 유틸리티
 * - 토큰 생성 시 userId, role을 claim에 포함
 * - 쿠키에서 토큰 추출 및 검증
 */
public class JwtUtil {

    // ⚠️ 운영 환경에서는 환경 변수나 안전한 저장소에서 관리해야 함
    private static final String SECRET_KEY = System.getenv("JWT_SECRET_KEY") != null 
            ? System.getenv("JWT_SECRET_KEY") 
            : "acs-check-attendance-system-secret-key-2025";
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000; // 24시간
    private static final String TOKEN_COOKIE_NAME = "auth_token";

    /**
     * JWT 토큰 생성
     * @param userId 사용자 ID
     * @param role 사용자 역할 (admin, student)
     * @return JWT 토큰 문자열
     */
    public static String generateToken(String userId, String role) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.builder()
                .subject(userId)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    /**
     * 토큰에서 Claims 추출 (검증 포함)
     * @param token JWT 토큰
     * @return Claims 객체
     * @throws Exception 토큰이 유효하지 않거나 만료된 경우
     */
    public static Claims parseToken(String token) throws Exception {
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 토큰에서 userId 추출
     * @param token JWT 토큰
     * @return userId
     */
    public static String getUserIdFromToken(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 토큰에서 role 추출
     * @param token JWT 토큰
     * @return role
     */
    public static String getRoleFromToken(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.get("role", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 토큰 유효성 검증
     * @param token JWT 토큰
     * @return 유효하면 true, 아니면 false
     */
    public static boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * HTTP 요청에서 쿠키로부터 토큰 추출
     * @param request HttpServletRequest
     * @return JWT 토큰 또는 null
     */
    public static String getTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 토큰 쿠키 이름 반환
     * @return 쿠키 이름
     */
    public static String getTokenCookieName() {
        return TOKEN_COOKIE_NAME;
    }
}
