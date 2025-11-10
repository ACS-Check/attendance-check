package attendance.attendance.controller;

import attendance.attendance.domain.AttendanceCode;
import attendance.attendance.service.AttendanceCodeService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/attendance-codes")
public class AttendanceCodeController extends HttpServlet {
    private final AttendanceCodeService attendanceCodeService = new AttendanceCodeService(null); // 나중에 DI

    // 코드 생성 (POST)
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String lectureIdParam = req.getParameter("lectureId");
        String teacherIdParam = req.getParameter("teacherId");

        if (lectureIdParam == null || teacherIdParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            int lectureId = Integer.parseInt(lectureIdParam);
            int teacherId = Integer.parseInt(teacherIdParam);
            AttendanceCode newCode = attendanceCodeService.createCode(lectureId, teacherId);
            req.setAttribute("attendanceCode", newCode);
            req.getRequestDispatcher("/WEB-INF/views/attendance/code.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    // 코드 조회 (GET)
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String codeParam = req.getParameter("code");
        if (codeParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            AttendanceCode code = attendanceCodeService.getByCode(codeParam);
            req.setAttribute("attendanceCode", code);
            req.getRequestDispatcher("/WEB-INF/views/attendance/code-detail.jsp").forward(req, resp);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    // 코드 삭제 (DELETE)
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String codeIdParam = req.getParameter("codeId");
        if (codeIdParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            int codeId = Integer.parseInt(codeIdParam);
            AttendanceCode code = attendanceCodeService.getByCodeId(codeId);
            attendanceCodeService.deleteCode(code);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}