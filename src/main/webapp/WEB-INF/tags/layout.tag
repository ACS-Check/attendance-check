<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="title"        required="false" type="java.lang.String" %>
<%@ attribute name="rootOnly"     required="false" type="java.lang.Boolean" %>
<%@ attribute name="rootId"       required="false" type="java.lang.String" %>
<%@ attribute name="extraHead"    required="false" type="java.lang.String" %>
<%@ attribute name="bodyClass"    required="false" type="java.lang.String" %>
<%
  jakarta.servlet.http.HttpServletRequest req = (jakarta.servlet.http.HttpServletRequest) request;
  String ctx = req.getContextPath();
  jakarta.servlet.http.HttpSession s = req.getSession(false);
  String role = s != null ? (String) s.getAttribute("role") : null;
  Object uObj = s != null ? s.getAttribute("user") : null;
  String userName = null;
  if (uObj instanceof model.User) {
    userName = ((model.User) uObj).getName();
  }
  boolean onlyRoot = rootOnly != null && rootOnly.booleanValue();
  String rid = (rootId != null && !rootId.isBlank()) ? rootId : "root";
  String pageTitle = (title != null && !title.isBlank()) ? title : "Attendance System";
%>
<!doctype html>
<html lang="ko">
  <head>
    <meta charset="UTF-8" />
    <link rel="icon" href="<%=ctx%>/favicon.ico" />
    <link rel="icon" type="image/svg+xml" href="<%=ctx%>/icon-aws.svg" />
    <link rel="mask-icon" href="<%=ctx%>/icon-aws.svg" color="#ffffff" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title><%= pageTitle %></title>
    <style>
      :root {
        font-family: system-ui, Avenir, Helvetica, Arial, sans-serif;
        line-height: 1.5; font-weight: 400; color-scheme: light;
        color:#213547; background-color:#ffffff; font-synthesis:none;
        text-rendering: optimizeLegibility; -webkit-font-smoothing: antialiased; -moz-osx-font-smoothing: grayscale;
      }
      a { font-weight:500; color:#646cff; text-decoration:none; }
      a:hover { color:#535bf2; }
      button { border-radius:8px; border:1px solid transparent; padding:.6em 1.2em; font-size:1em; font-weight:500; font-family:inherit; background:#f9f9f9; cursor:pointer; transition:border-color .25s; }
      button:hover { border-color:#646cff; }
      button:focus, button:focus-visible { outline:4px auto -webkit-focus-ring-color; }
      /* Layout (standard mode) */
      body.std-body { margin:0; background:#f3f4f6; color:#111827; }
      header.site { background:#111827; color:#fff; padding:12px 20px; display:flex; align-items:center; justify-content:space-between; }
      header.site a { color:#f3f4f6; margin-right:14px; font-size:14px; }
      header.site a:last-child { margin-right:0; }
      header.site a:hover { text-decoration:underline; }
      <%-- main { margin:20px auto; background:#ffffff; padding:24px; border-radius:12px; box-shadow:0 2px 4px rgba(0,0,0,.08); } --%>
      footer.site { text-align:center; font-size:12px; color:#6b7280; padding:32px 0 40px; }
      .role-badge { font-size:11px; background:#374151; color:#e5e7eb; padding:2px 6px; border-radius:6px; margin-left:8px; }
      .muted { color:#6b7280; font-size:12px; margin-top:0; }
      /* Root-only mode ensures full viewport mount */
      body:not(.std-body) { margin:0; min-height:100vh; display:flex; flex-direction:column; }
      #<%=rid%> { width:100%; height:100vh; }
    </style>
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://cdn.jsdelivr.net/npm/lucide@latest/dist/umd/lucide.min.js"></script>
    <script>
      document.addEventListener("DOMContentLoaded", function () {
        lucide.createIcons();
      });
    </script>
    <script>
      tailwind.config = {
        theme: {
          extend: {
            colors: {
              primary: {
                100: '#FFE6D1',
                200: '#FFCE99',
                300: '#FFB566',
                400: '#FFA033',
                500: '#FF9900', // base
                600: '#E68A00',
                700: '#CC7A00',
                800: '#A86300',
                900: '#854E00',
                DEFAULT: '#FF9900',
              },
            },
          },
        },
      };
    </script>
    <%= (extraHead != null) ? extraHead : "" %>
  </head>
  <body class="min-h-screen">
    <% if (onlyRoot) { %>
      <div id="<%=rid%>"></div>
      <jsp:doBody/>
    <% } else { %>
      <header class="site">
        <div class="flex justify-between items-center w-full">
          <strong>Attendance</strong>
          <% if (userName != null) { %><span class="role-badge"><%= role %></span><% } %>
        </div>
      </header>
      <main class="flex-1 flex justify-center items-center">
        <% if (userName != null) { %>
          <p class="muted">안녕하세요, <strong><%= userName %></strong>님.</p>
        <% } %>
        <jsp:doBody/>
      </main>
      <footer class="site">
        <p>&copy; 2025 Attendance System</p>
      </footer>
    <% } %>
  </body>
</html>