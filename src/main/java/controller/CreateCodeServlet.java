package controller;

import dao.AttendanceCodeDAO;
import model.AttendanceCode;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(name="CreateCodeServlet", urlPatterns={"/teacher/code"})
public class CreateCodeServlet extends HttpServlet {
    private final AttendanceCodeDAO codeDAO = new AttendanceCodeDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // RoleFilter에서 admin 확인한다고 가정. 없으면 아래 주석 해제해서 직접 체크
        /*
        HttpSession session = req.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("role"))) {
            resp.sendError(403, "FORBIDDEN");
            return;
        }
        */

        String minutesParam = req.getParameter("validMinutes");
        int minutes = 10; // 기본 10분
        try { if (minutesParam != null) minutes = Integer.parseInt(minutesParam); } catch (NumberFormatException ignored) {}

        AttendanceCode code = codeDAO.create(minutes);

        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().printf("{\"code\":\"%s\",\"generated_date\":\"%s\",\"expired_time\":\"%s\"}",
                code.getCodeValue(), code.getGeneratedDate(), code.getExpiredTime());
    }
}
