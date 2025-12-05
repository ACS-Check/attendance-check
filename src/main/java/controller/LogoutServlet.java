package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.JwtUtil;

/**
 * 로그아웃 처리 서블릿
 * - GET /logout
 * - JWT 토큰 쿠키 삭제
 */
@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout"})
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ✅ JWT 토큰 쿠키 삭제 (MaxAge를 0으로 설정)
        Cookie tokenCookie = new Cookie(JwtUtil.getTokenCookieName(), "");
        tokenCookie.setPath(request.getContextPath() + "/");
        tokenCookie.setMaxAge(0);  // 즉시 삭제
        tokenCookie.setHttpOnly(true);
        response.addCookie(tokenCookie);

        response.sendRedirect("login.jsp");
    }
}
