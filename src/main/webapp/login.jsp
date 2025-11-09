<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html><html><head><meta charset="UTF-8"><title>로그인</title></head>
<body>
  <h2>로그인</h2>
  <form method="post" action="login">
    <label>아이디: <input name="username" required></label><br/>
    <label>비밀번호: <input type="password" name="password" required></label><br/>
    <button type="submit">로그인</button>
  </form>
  <p><a href="index.jsp">← 홈</a></p>
</body></html>
