package filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.JwtUtil;

/**
 * AuthFilter
 * - JWT 토큰 기반 인증 확인
 * - 토큰이 없거나 유효하지 않으면 /login.jsp 로 리다이렉트
 * - 유효한 토큰이 있으면 request에 userId와 role을 attribute로 설정
 */
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String uri = req.getRequestURI();

        // 로그인 / 회원가입 / API / 정적 파일은 필터 제외
        boolean allowed = uri.endsWith("/login") ||
                          uri.endsWith("/register") ||
                          uri.contains("/api/") ||
                          uri.contains("/static/");

        if (allowed) {
            chain.doFilter(request, response);
            return;
        }

        // ✅ JWT 토큰 검증
        String token = JwtUtil.getTokenFromRequest(req);
        boolean loggedIn = false;

        if (token != null && JwtUtil.validateToken(token)) {
            // 토큰이 유효하면 사용자 정보를 request attribute에 설정
            String userId = JwtUtil.getUserIdFromToken(token);
            String role = JwtUtil.getRoleFromToken(token);
            
            if (userId != null && role != null) {
                req.setAttribute("user_id", userId);
                req.setAttribute("role", role);
                loggedIn = true;
            }
        }

        if (loggedIn) {
            chain.doFilter(request, response);
        } else {
            res.sendRedirect(req.getContextPath() + "/login");
        }
    }
}
