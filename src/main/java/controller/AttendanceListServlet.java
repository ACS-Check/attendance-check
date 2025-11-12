package controller;

import dao.AttendanceDAO;
import model.AttendanceRecord;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@WebServlet(name="AttendanceListServlet", urlPatterns={"/attend/list","/teacher/daily"})
public class AttendanceListServlet extends HttpServlet {
    private final AttendanceDAO attDAO = new AttendanceDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getServletPath();
        resp.setContentType("application/json; charset=UTF-8");

        if ("/attend/list".equals(path)) {
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("user_id") == null) { resp.sendError(401); return; }
            int userId = (Integer) session.getAttribute("user_id");

            String month = req.getParameter("month"); // 예: 2025-11
            YearMonth ym;
            try {
                if (month == null || month.isBlank()) {
                    ym = YearMonth.now(); // 기본 현재 월
                } else {
                    ym = YearMonth.parse(month.trim());
                }
            } catch (Exception e) {
                resp.sendError(400, "INVALID_MONTH_FORMAT");
                return;
            }

            try {
                List<AttendanceRecord> list = attDAO.findByUserAndMonth(userId, ym);
                writeJson(list, resp);
            } catch (RuntimeException ex) {
                resp.setStatus(500);
                resp.setContentType("text/plain; charset=UTF-8");
                String msg = ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
                resp.getWriter().print("SERVER_ERROR: " + msg);
            }
            return;
        }

        if ("/teacher/daily".equals(path)) {
            // RoleFilter로 admin 보장된다고 가정
            String date = req.getParameter("date"); // 예: 2025-11-09
            if (date == null || date.isBlank()) { resp.sendError(400, "DATE_REQUIRED"); return; }

            LocalDate d = LocalDate.parse(date);
            List<AttendanceRecord> list = attDAO.findByDate(d);
            writeJson(list, resp);
        }
    }

    private void writeJson(List<AttendanceRecord> list, HttpServletResponse resp) throws IOException {
        StringBuilder sb = new StringBuilder("[");
        for (int i=0;i<list.size();i++) {
            AttendanceRecord r = list.get(i);
            sb.append(String.format("{\"attendId\":%d,\"userId\":%d,\"date\":\"%s\",\"time\":\"%s\",\"status\":\"%s\",\"codeId\":%d}",
                    r.getAttendId(), r.getUserId(), r.getAttendDate(), r.getAttendTime(), r.getStatus(), r.getCodeId()));
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        resp.getWriter().print(sb.toString());
    }
}
