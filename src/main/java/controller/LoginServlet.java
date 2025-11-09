package controller;

import dao.UserDAO;
import model.User;
import util.PasswordUtil;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * 로그인 요청 처리 서블릿
 * - POST /login
 * - 세션 생성, 권한에 따른 리다이렉트
 */
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
            HttpSession session = request.getSession();
            session.setAttribute("user", user);

            // 역할에 따라 리다이렉트
            if ("teacher".equalsIgnoreCase(user.getRole())) {
                response.sendRedirect("teacher.jsp");
            } else {
                response.sendRedirect("student.jsp");
            }
        } else {
            // 실패 시 로그인 페이지로 포워딩
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
