package controller;

import java.io.IOException;
import java.util.List;

import dao.AttendanceDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.AttendanceSummary;

@WebServlet(name="TeacherAttendanceListServlet", urlPatterns={"/teacher/attendanceList"})
public class TeacherAttendanceListServlet extends HttpServlet {
    private final AttendanceDAO attDAO = new AttendanceDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // RoleFilter에서 admin 확인된다고 가정 (JWT 토큰 기반)
        String role = (String) req.getAttribute("role");
        if (role == null || !"admin".equals(role)) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

    // 전체 출석 요약 데이터 가져오기
    List<AttendanceSummary> summary = attDAO.getAttendanceSummary();
        req.setAttribute("attendanceSummary", summary);
        try {
            req.getRequestDispatcher("/teacher/attendance-list.jsp").forward(req, resp);
        } catch (ServletException e) {
            throw new RuntimeException("ServletException occurred while forwarding to JSP", e);
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred while forwarding to JSP", e);
        }
    }
}
