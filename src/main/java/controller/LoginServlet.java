package controller;

import java.io.IOException;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import util.PasswordUtil;

/**
 * 로그인 요청 처리 서블릿
 * - POST /login
 * - 세션 생성 및 권한에 따른 리다이렉트
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

            // ✅ 세션 생성 및 공통 키 저장 (필터/출석 시스템과 일치)
            HttpSession session = request.getSession();
            session.setAttribute("user", user); // Add user object for AuthFilter compatibility
            session.setAttribute("user_id", user.getUserId());  // AuthFilter에서 확인
            session.setAttribute("role", user.getRole());        // RoleFilter에서 확인

            // ✅ 역할에 따라 리다이렉트 분기
            if ("admin".equalsIgnoreCase(user.getRole())) {
                // 교사(admin) → 출석 코드 생성 페이지로
                response.sendRedirect(request.getContextPath() + "/teacher/attendanceList");
            } else {
                // 학생(student) → 출석 입력 페이지로
                response.sendRedirect(request.getContextPath() + "/student/attendanceList");
            }

        } else {
            // 로그인 실패: 서블릿에서 직접 alert 후 로그인 페이지로 리다이렉트
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html; charset=UTF-8");
            String ctx = request.getContextPath();
            String html = "<!DOCTYPE html>" +
                    "<html lang=\"ko\"><head><meta charset=\"UTF-8\"/>" +
                    // JS 비활성화 환경 대비
                    "<noscript><meta http-equiv=\"refresh\" content=\"0; url=" + ctx + "/login.jsp\"/></noscript>" +
                    "<title>로그인 실패</title></head><body>" +
                    "<script>" +
                    "alert('아이디 또는 비밀번호가 잘못되었습니다.');" +
                    "window.location.replace('" + ctx + "/login.jsp');" +
                    "</script>" +
                    "</body></html>";
            response.getWriter().write(html);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("login.jsp");
    }
}
