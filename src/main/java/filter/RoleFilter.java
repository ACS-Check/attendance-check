package filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * RoleFilter
 * - 관리자(교사) 권한 확인
 * - JWT 토큰의 role 값이 'admin'이 아닐 경우 접근 불가
 */
public class RoleFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // ✅ AuthFilter에서 설정한 role attribute 가져오기
        String role = (String) req.getAttribute("role");

        // ✅ 권한 확인 (role이 'admin'이 아닐 경우 접근 제한)
        if (role == null || !"admin".equalsIgnoreCase(role)) {
            // 일반 학생이면 학생 페이지로 리다이렉트
            res.sendRedirect(req.getContextPath() + "/student/attendanceList");
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
