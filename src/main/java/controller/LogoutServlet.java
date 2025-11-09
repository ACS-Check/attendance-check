package controller;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * 로그아웃 처리 서블릿
 * - GET /logout
 */
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();

        response.sendRedirect("login.jsp");
    }
}
