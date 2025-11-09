package controller;

import dao.UserDAO;
import model.User;
import util.PasswordUtil;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;

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
            session.setAttribute("user_id", user.getUserId());  // AuthFilter에서 확인
            session.setAttribute("role", user.getRole());        // RoleFilter에서 확인

            // ✅ 역할에 따라 리다이렉트 분기
            if ("admin".equalsIgnoreCase(user.getRole())) {
                // 교사(admin) → 출석 코드 생성 페이지로
                response.sendRedirect(request.getContextPath() + "/teacher.jsp");
            } else {
                // 학생(student) → 출석 입력 페이지로
                response.sendRedirect(request.getContextPath() + "/student.jsp");
            }

        } else {
            // 로그인 실패 시 에러 메시지와 함께 로그인 폼으로
            request.setAttribute("error", "아이디 또는 비밀번호가 잘못되었습니다.");
            RequestDispatcher rd = request.getRequestDispatcher("login.jsp");
            rd.forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("login.jsp");
    }
}
