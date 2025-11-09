<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html><html><head><meta charset="UTF-8"><title>출석체크</title></head>
<body>
  <h2>출석체크 (학생)</h2>
  <form method="post" action="attend/mark">
    <label>출석코드: <input name="code" required></label><br/>
    <button type="submit">출석</button>
  </form>
  <p>※ 먼저 로그인 필요: <a href="login.jsp">로그인</a></p>
  <p><a href="index.jsp">← 홈</a></p>
</body></html>
