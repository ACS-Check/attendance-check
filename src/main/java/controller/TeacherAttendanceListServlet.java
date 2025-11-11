package controller;

import java.io.IOException;
import java.util.List;

import dao.AttendanceDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.AttendanceSummary;

@WebServlet(name="TeacherAttendanceListServlet", urlPatterns={"/teacher/attendanceList"})
public class TeacherAttendanceListServlet extends HttpServlet {
    private final AttendanceDAO attDAO = new AttendanceDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user_id") == null || !"admin".equals(session.getAttribute("role"))) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
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
