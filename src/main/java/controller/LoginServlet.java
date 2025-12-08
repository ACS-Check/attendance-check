package controller;

import java.io.IOException;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import util.JwtUtil;
import util.PasswordUtil;

/**
 * 로그인 요청 처리 서블릿
 * - POST /login
 * - JWT 토큰 생성 및 쿠키 설정, 권한에 따른 리다이렉트
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        UserDAO dao = new UserDAO();
        User user = dao.findByUsername(username);

        if (user != null && PasswordUtil.checkPassword(password, user.getPassword())) {

            // ✅ JWT 토큰 생성
            String token = JwtUtil.generateToken(String.valueOf(user.getUserId()), user.getRole());
            
            // ✅ 쿠키에 토큰 저장 (HttpOnly, Secure 옵션 설정)
            Cookie tokenCookie = new Cookie(JwtUtil.getTokenCookieName(), token);
            tokenCookie.setHttpOnly(true);  // JavaScript 접근 방지 (XSS 방어)
            tokenCookie.setPath(request.getContextPath() + "/");
            tokenCookie.setMaxAge(24 * 60 * 60);  // 24시간
            // tokenCookie.setSecure(true);  // HTTPS 환경에서만 활성화
            response.addCookie(tokenCookie);

            // ✅ 역할에 따라 리다이렉트 분기
            if ("admin".equalsIgnoreCase(user.getRole())) {
                // 교사(admin) → 출석 코드 생성 페이지로
                response.sendRedirect(request.getContextPath() + "/teacher/attendanceList");
            } else {
                // 학생(student) → 출석 입력 페이지로
                response.sendRedirect(request.getContextPath() + "/student/attendanceList");
            }

        } else {
            // 로그인 실패: alert 후 로그인 페이지로 리다이렉트
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html; charset=UTF-8");
            String ctx = request.getContextPath();
            String html = "<!DOCTYPE html>" +
                    "<html><head><meta charset=\"UTF-8\"/></head><body>" +
                    "<script>" +
                    "alert('아이디 또는 비밀번호가 잘못되었습니다.');" +
                    "window.location.href = '" + ctx + "/login';" +
                    "</script>" +
                    "</body></html>";
            response.getWriter().write(html);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }
}
