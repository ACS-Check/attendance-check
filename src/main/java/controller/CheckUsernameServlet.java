package controller;

import java.io.IOException;
import java.io.PrintWriter;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 아이디 중복 체크 API
 * - GET /api/check-username?username=xxx
 */
@WebServlet(name = "CheckUsernameServlet", urlPatterns = {"/api/check-username"})
public class CheckUsernameServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // CORS 및 캐시 방지 헤더 추가
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        String username = request.getParameter("username");

        PrintWriter out = response.getWriter();

        try {
            if (username == null || username.trim().isEmpty()) {
                out.write("{\"available\": false, \"message\": \"아이디를 입력하세요.\"}");
                out.flush();
                return;
            }

            UserDAO dao = new UserDAO();
            boolean exists = (dao.findByUsername(username) != null);

            if (exists) {
                out.write("{\"available\": false, \"message\": \"이미 사용 중인 아이디입니다.\"}");
            } else {
                out.write("{\"available\": true, \"message\": \"사용 가능한 아이디입니다.\"}");
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"available\": false, \"message\": \"서버 오류가 발생했습니다.\"}");
            out.flush();
        }
    }
}
