package controller;

import dao.UserDAO;
import model.User;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * 학생 목록 관리 페이지
 * GET /teacher/studentList?q=&page=&size=
 */
@WebServlet(
    name = "StudentListServlet",
    urlPatterns = {
        "/teacher/studentList",
        "/teacher/students",
        "/teacher/students/delete",
        "/teacher/students/resetPassword"
    }
)
public class StudentListServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();
    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // ambiguous 문자 제외
    private static final java.security.SecureRandom RND = new java.security.SecureRandom();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // RoleFilter에서 admin 확인된다고 가정
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("role") == null ||
                !"admin".equalsIgnoreCase((String) session.getAttribute("role"))) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        String q = req.getParameter("q");
        int page = parseIntOr(req.getParameter("page"), 1);
        int size = parseIntOr(req.getParameter("size"), 10);
    List<User> students = userDAO.listStudentsPaged(q, page, size);
    int total = userDAO.countStudents(q);
        int totalPages = (int) Math.ceil(total / (double) size);

        req.setAttribute("students", students);
        req.setAttribute("q", q == null ? "" : q);
        req.setAttribute("page", page);
        req.setAttribute("size", size);
        req.setAttribute("total", total);
        req.setAttribute("totalPages", totalPages);
        RequestDispatcher rd = req.getRequestDispatcher("/teacher/student-list.jsp");
        rd.forward(req, resp);
    }

    private int parseIntOr(String v, int def) {
        try { return v == null ? def : Integer.parseInt(v); } catch (NumberFormatException e) { return def; }
    }

    private String generateTempPassword(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(CHARS.charAt(RND.nextInt(CHARS.length())));
        return sb.toString();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Admin check
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("role") == null ||
                !"admin".equalsIgnoreCase((String) session.getAttribute("role"))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String servletPath = req.getServletPath(); // e.g. /teacher/students/delete
        if (servletPath.endsWith("/delete")) {
            handleDelete(req, resp);
        } else if (servletPath.endsWith("/resetPassword")) {
            handleResetPassword(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idStr = req.getParameter("id");
        if (idStr == null || idStr.isBlank()) { resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID_REQUIRED"); return; }
        int id;
        try { id = Integer.parseInt(idStr); } catch (NumberFormatException e) { resp.sendError(400, "INVALID_ID"); return; }
        boolean ok = userDAO.deleteStudent(id);
        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().write("{\"success\":" + ok + "}");
    }

    private void handleResetPassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idStr = req.getParameter("id");
        if (idStr == null || idStr.isBlank()) { resp.sendError(400, "ID_REQUIRED"); return; }
        try {
            int id = Integer.parseInt(idStr);
            String tempPwd = generateTempPassword(10);
            boolean ok = userDAO.resetPassword(id, tempPwd);
            resp.setContentType("application/json; charset=UTF-8");
            if (ok) {
                resp.getWriter().write("{\"success\":true,\"tempPassword\":\"" + tempPwd + "\"}");
            } else {
                resp.getWriter().write("{\"success\":false}");
            }
        } catch (NumberFormatException e) {
            resp.sendError(400, "INVALID_ID");
        }
    }
}
