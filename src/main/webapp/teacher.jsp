<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html><html><head><meta charset="UTF-8"><title>출석코드 생성(관리자)</title></head>
<body>
  <h2>출석코드 생성 (관리자)</h2>
  <form method="post" action="teacher/code">
    <label>유효 분(min): <input type="number" name="validMinutes" value="10" min="1" required></label><br/>
    <button type="submit">코드 생성</button>
  </form>
  <p>※ 로그인/세션이 필요한 엔드포인트라서, 먼저 <a href="login.jsp">로그인</a>해야 유효.</p>
  <p><a href="index.jsp">← 홈</a></p>
</body></html>
