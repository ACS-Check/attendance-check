<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  // 최소한의 리다이렉트 처리 (JSP 컴파일 안정성 위해 불필요한 마크업 제거)
  jakarta.servlet.http.HttpSession s = request.getSession(false);
  System.out.print("session:" + s);
  if (s == null) {
    response.sendRedirect(request.getContextPath() + "/login.jsp");
    return;
  }
  String role = (String) s.getAttribute("role");
  if (role == null) {
    response.sendRedirect(request.getContextPath() + "/login.jsp");
    return;
  }
  if ("admin".equalsIgnoreCase(role)) {
    response.sendRedirect(request.getContextPath() + "/teacher/attendanceList");
    return;
  }
  if ("student".equalsIgnoreCase(role)) {
    response.sendRedirect(request.getContextPath() + "/student/attendanceList");
    return;
  }
  // 알 수 없는 role 기본 처리
  response.sendRedirect(request.getContextPath() + "/login.jsp");
%>
