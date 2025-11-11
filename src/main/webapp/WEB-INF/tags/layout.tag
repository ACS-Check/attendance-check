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
      /* Layout (standard mode) */
      body.std-body { margin:0; background:#f3f4f6; color:#111827; }
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
      <% 
        String currentURI = req.getRequestURI();
        boolean hideHeader = currentURI.endsWith("/login.jsp") || currentURI.endsWith("/register.jsp");
      %>
      <% if (!hideHeader) { %>
      <header class="h-16 bg-primary-100 shadow-md">
        <div class="flex justify-between items-center w-full px-6 h-full">
          <!-- Left: Logo and Title -->
          <div class="flex items-center space-x-4">
            <img src="<%=ctx%>/static/icon-aws.svg" alt="Logo" class="h-8 w-8">
            <span class="text-black font-bold text-lg">AWS Cloud School</span>
          </div>

          <!-- Right: User Info and Tooltip -->
          <% if (userName != null) { %>
            <div class="relative">
              <button id="user-menu" class="text-black font-medium flex items-center space-x-2">
                <span>
                  <%= userName %> (
                  <% if ("admin".equalsIgnoreCase(role)) { %>
                    강사
                  <% } else if ("student".equalsIgnoreCase(role)) { %>
                    학생
                  <% } %>
                  )
                </span>
                <i data-lucide="chevron-down" class="w-4 h-4"></i>
              </button>
              <div id="tooltip-menu" class="hidden absolute right-0 top-8 w-48 bg-white rounded-md shadow-lg">
                <a href="<%=ctx%>/logout" class="block px-4 py-2 text-gray-700 hover:bg-gray-100">Logout</a>
              </div>
            </div>
            <script>
              document.getElementById('user-menu').addEventListener('click', function () {
                const tooltip = document.getElementById('tooltip-menu');
                tooltip.classList.toggle('hidden');
              });
            </script>
          <% } %>
        </div>
      </header>
      <% } %>
      <main class="flex-1 flex justify-center items-center">
        <jsp:doBody/>
      </main>
      <footer class="site">
        <p>&copy; 2025 Attendance System</p>
      </footer>
    <% } %>
  </body>
</html>