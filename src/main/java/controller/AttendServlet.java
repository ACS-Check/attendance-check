package controller;

import java.io.IOException;
import java.util.Optional;

import dao.AttendanceCodeDAO;
import dao.AttendanceDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.AttendanceCode;
import model.AttendanceRecord;

@WebServlet(name="AttendServlet", urlPatterns={"/attend/mark"})
public class AttendServlet extends HttpServlet {
    private final AttendanceCodeDAO codeDAO = new AttendanceCodeDAO();
    private final AttendanceDAO attDAO = new AttendanceDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // AuthFilter에서 설정한 user_id attribute 가져오기
        String userIdStr = (String) req.getAttribute("user_id");
        if (userIdStr == null) {
            resp.sendError(401, "UNAUTHORIZED");
            return;
        }
        int userId = Integer.parseInt(userIdStr);

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
