package controller;

import java.io.IOException;

import dao.AttendanceCodeDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.AttendanceCode;

@WebServlet(name="CreateCodeServlet", urlPatterns={"/teacher/code"})
public class CreateCodeServlet extends HttpServlet {
    private final AttendanceCodeDAO codeDAO = new AttendanceCodeDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String minutesParam = req.getParameter("validMinutes");
        int minutes = 10; // 기본 10분
        try { if (minutesParam != null) minutes = Integer.parseInt(minutesParam); } catch (NumberFormatException ignored) {}

        AttendanceCode code = codeDAO.create(minutes);

        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().printf("{\"code\":\"%s\",\"generated_date\":\"%s\",\"expired_time\":\"%s\"}",
                code.getCodeValue(), code.getGeneratedDate(), code.getExpiredTime());
    }
}
