<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  // JWT 토큰 기반 리다이렉트 처리
  String token = util.JwtUtil.getTokenFromRequest(request);
  String role = null;
  
  if (token != null && util.JwtUtil.validateToken(token)) {
    role = util.JwtUtil.getRoleFromToken(token);
  }
  
  if (role == null) {
    response.sendRedirect(request.getContextPath() + "/login");
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
  response.sendRedirect(request.getContextPath() + "/login");
%>
