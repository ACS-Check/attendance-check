package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 출석 코드 생성 페이지 서블릿
 * GET /teacher/attendanceCode -> /teacher/attendance-code.jsp 로 포워드
 */
@WebServlet(name = "AttendanceCodePageServlet", urlPatterns = {"/teacher/attendanceCode"})
public class AttendanceCodePageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // RoleFilter에서 admin 확인된다고 가정 (JWT 토큰 기반)
        String role = (String) req.getAttribute("role");
        if (role == null || !"admin".equals(role)) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        req.getRequestDispatcher("/teacher/attendance-code.jsp").forward(req, resp);
    }
}
