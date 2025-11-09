package filter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * AuthFilter
 * - 로그인 여부 확인
 * - 세션에 user가 없으면 /login.jsp 로 리다이렉트
 */
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        boolean loggedIn = (session != null && session.getAttribute("user") != null);

        String uri = req.getRequestURI();

        // 로그인 / 회원가입 / 정적 파일은 필터 제외
        boolean allowed = uri.endsWith("login.jsp") ||
                          uri.endsWith("register.jsp") ||
                          uri.endsWith("index.jsp") ||
                          uri.endsWith("/login") ||
                          uri.endsWith("/register") ||
                          uri.contains("/static/");

        if (loggedIn || allowed) {
            chain.doFilter(request, response);
        } else {
            res.sendRedirect(req.getContextPath() + "/login.jsp");
        }
    }
}
