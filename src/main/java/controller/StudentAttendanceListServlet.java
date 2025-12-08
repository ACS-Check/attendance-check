package controller;

import java.io.IOException;
import java.time.YearMonth;
import java.util.List;

import dao.AttendanceDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.AttendanceRecord;

/**
 * 학생 본인 출석 기록 페이지 진입 서블릿
 * GET /student/attendanceList -> /student/attendance-list.jsp 로 포워드
 */
@WebServlet(name = "StudentAttendanceListServlet", urlPatterns = {"/student/attendanceList"})
public class StudentAttendanceListServlet extends HttpServlet {
    private final AttendanceDAO attDAO = new AttendanceDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // AuthFilter에서 설정한 user_id, role attribute 가져오기
        String userIdStr = (String) req.getAttribute("user_id");
        String role = (String) req.getAttribute("role");
        
        if (userIdStr == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        // 관리자는 교사용 페이지로 이동 (선택적 처리)
        if (role != null && "admin".equalsIgnoreCase(role)) {
            resp.sendRedirect(req.getContextPath() + "/teacher/attendanceList");
            return;
        }

        // 초기 렌더용 서버 사이드 데이터 제공 (오늘 월)
        try {
            int userId = Integer.parseInt(userIdStr);
            YearMonth ym = YearMonth.now();
            List<AttendanceRecord> initialList = attDAO.findByUserAndMonth(userId, ym);
            req.setAttribute("initialList", initialList);
            req.setAttribute("initialMonth", ym.toString()); // e.g. 2025-11
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        RequestDispatcher rd = req.getRequestDispatcher("/student/attendance-list.jsp");
        rd.forward(req, resp);
    }
}
