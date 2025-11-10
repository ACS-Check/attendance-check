package attendance.attendance.controller;

import attendance.attendance.domain.AttendanceRecord;
import attendance.attendance.service.AttendanceRecordService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet("/attendance-records")
public class AttendanceRecordController extends HttpServlet {
    private final AttendanceRecordService attendanceRecordService = new AttendanceRecordService(null); // 나중에 DI

    // 출석 등록 (POST)
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userIdParam = req.getParameter("userId");
        String codeIdParam = req.getParameter("codeId");

        if (userIdParam == null || codeIdParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            int userId = Integer.parseInt(userIdParam);
            int codeId = Integer.parseInt(codeIdParam);

            AttendanceRecord record = new AttendanceRecord();
            record.setUserId(userId);
            record.setCodeId(codeId);
            record.setAttendedAt(LocalDateTime.now());

            attendanceRecordService.save(record);
            resp.sendRedirect(req.getContextPath() + "/attendance-records?userId=" + userId);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    // 출석부 조회 (GET)
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String lectureIdParam = req.getParameter("lectureId");
        String userIdParam = req.getParameter("userId");

        try {
            if (lectureIdParam != null) {
                int lectureId = Integer.parseInt(lectureIdParam);
                List<AttendanceRecord> records = attendanceRecordService.findAllByLectureId(lectureId);
                req.setAttribute("records", records);
                req.getRequestDispatcher("/WEB-INF/views/attendance/lecture-records.jsp").forward(req, resp);
            } else if (userIdParam != null) {
                int userId = Integer.parseInt(userIdParam);
                List<AttendanceRecord> records = attendanceRecordService.findAllByUserId(userId);
                req.setAttribute("records", records);
                req.getRequestDispatcher("/WEB-INF/views/attendance/user-records.jsp").forward(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}