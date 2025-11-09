package controller;

import dao.AttendanceCodeDAO;
import dao.AttendanceDAO;
import model.AttendanceCode;
import model.AttendanceRecord;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Optional;

@WebServlet(name="AttendServlet", urlPatterns={"/attend/mark"})
public class AttendServlet extends HttpServlet {
    private final AttendanceCodeDAO codeDAO = new AttendanceCodeDAO();
    private final AttendanceDAO attDAO = new AttendanceDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            resp.sendError(401, "UNAUTHORIZED");
            return;
        }
        int userId = (Integer) session.getAttribute("user_id");

        String codeValue = req.getParameter("code");
        if (codeValue == null || codeValue.isBlank()) {
            resp.sendError(400, "CODE_REQUIRED");
            return;
        }

        Optional<AttendanceCode> oc = codeDAO.findByValue(codeValue.trim().toUpperCase());
        if (oc.isEmpty()) {
            resp.sendError(400, "INVALID_CODE");
            return;
        }

        AttendanceCode code = oc.get();
        String status = codeDAO.isExpired(code) ? "지각" : "출석";

        Optional<AttendanceRecord> saved = attDAO.markAttendance(userId, code.getCodeId(), status);
        if (saved.isEmpty()) {
            resp.sendError(500, "SAVE_FAILED");
            return;
        }

        AttendanceRecord r = saved.get();
        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().printf("{\"attendId\":%d,\"userId\":%d,\"date\":\"%s\",\"time\":\"%s\",\"status\":\"%s\",\"codeId\":%d}",
                r.getAttendId(), r.getUserId(), r.getAttendDate(), r.getAttendTime(), r.getStatus(), r.getCodeId());
    }
}
