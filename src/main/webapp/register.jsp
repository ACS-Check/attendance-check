<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html><html><head><meta charset="UTF-8"><title>회원가입</title></head>
<body>
  <h2>회원가입</h2>
  <form method="post" action="register">
    <label>아이디: <input name="username" required></label><br/>
    <label>비밀번호: <input type="password" name="password" required></label><br/>
    <label>이름: <input name="name" required></label><br/>
    <label>역할:
      <select name="role">
        <option value="student">student</option>
        <option value="admin">admin</option>
      </select>
    </label><br/>
    <button type="submit">가입</button>
  </form>
  <p><a href="index.jsp">← 홈</a></p>
</body></html>
