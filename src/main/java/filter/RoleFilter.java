package filter;

import model.User;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * RoleFilter
 * - 관리자(교사) 권한 확인
 * - role != 'admin' 인 사용자는 접근 불가
 */
public class RoleFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        if (session == null) {
            res.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (user == null || !"admin".equals(user.getRole())) {
            res.sendRedirect(req.getContextPath() + "/student.jsp");
            return;
        }

        chain.doFilter(request, response);
    }
}
