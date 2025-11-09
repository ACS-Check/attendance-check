package filter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * RoleFilter
 * - 관리자(교사) 권한 확인
 * - 세션의 role 값이 'admin'이 아닐 경우 접근 불가
 */
public class RoleFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // ✅ 세션 확인 (로그인 여부)
        HttpSession session = req.getSession(false);
        if (session == null) {
            // 로그인 안 된 상태 → 로그인 페이지로 이동
            res.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        // ✅ 세션에서 role 가져오기
        String role = (String) session.getAttribute("role");

        // ✅ 권한 확인 (role이 'admin'이 아닐 경우 접근 제한)
        if (role == null || !"admin".equalsIgnoreCase(role)) {
            // 일반 학생이면 학생 페이지로 리다이렉트
            res.sendRedirect(req.getContextPath() + "/student.jsp");
            return;
        }

        // ✅ 통과: 교사(admin)인 경우만 다음 단계 실행
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void destroy() {}
}
