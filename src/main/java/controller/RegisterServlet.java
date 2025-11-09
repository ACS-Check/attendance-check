package controller;

import dao.UserDAO;
import model.User;
import util.PasswordUtil;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * 회원가입 요청 처리 서블릿
 * - POST /register
 */
public class RegisterServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role"); // "student" or "teacher"

        UserDAO dao = new UserDAO();

        // 중복 확인
        if (dao.findByUsername(username) != null) {
            request.setAttribute("error", "이미 존재하는 사용자명입니다.");
            RequestDispatcher rd = request.getRequestDispatcher("register.jsp");
            rd.forward(request, response);
            return;
        }

        // 비밀번호 해싱 후 DB 삽입
        String hashed = PasswordUtil.hashPassword(password);
        User user = new User();
        user.setUsername(username);
        user.setPassword(hashed);
        user.setRole(role);

        boolean success = dao.insertUser(user);
        if (success) {
            response.sendRedirect("login.jsp");
        } else {
            request.setAttribute("error", "회원가입에 실패했습니다.");
            RequestDispatcher rd = request.getRequestDispatcher("register.jsp");
            rd.forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("register.jsp");
    }
}
