<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="title"        required="false" type="java.lang.String" %>
<%@ attribute name="rootOnly"     required="false" type="java.lang.Boolean" %>
<%@ attribute name="rootId"       required="false" type="java.lang.String" %>
<%@ attribute name="extraHead"    required="false" type="java.lang.String" %>
<%@ attribute name="bodyClass"    required="false" type="java.lang.String" %>
<%
  jakarta.servlet.http.HttpServletRequest req = (jakarta.servlet.http.HttpServletRequest) request;
  String ctx = req.getContextPath();
  
  // JWT 토큰에서 사용자 정보 가져오기
  String token = util.JwtUtil.getTokenFromRequest(req);
  String role = null;
  String userId = null;
  String userName = null;
  
  if (token != null && util.JwtUtil.validateToken(token)) {
    role = util.JwtUtil.getRoleFromToken(token);
    userId = util.JwtUtil.getUserIdFromToken(token);
    
    // UserDAO로 사용자 이름 조회
    if (userId != null) {
      try {
        dao.UserDAO userDAO = new dao.UserDAO();
        model.User user = userDAO.findById(Integer.parseInt(userId));
        if (user != null) {
          userName = user.getName();
        }
      } catch (Exception e) {
        // 오류 발생 시 기본값 사용
      }
    }
  }
  
  boolean onlyRoot = rootOnly != null && rootOnly.booleanValue();
  String rid = (rootId != null && !rootId.isBlank()) ? rootId : "root";
  String pageTitle = (title != null && !title.isBlank()) ? title : "출석 관리 시스템";
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
      body.std-body { margin:0; background:#f8f9fa; color:#111827; }
      <%-- main { margin:20px auto; background:#ffffff; padding:24px; border-radius:12px; box-shadow:0 2px 4px rgba(0,0,0,.08); } --%>
      footer.site { text-align:center; font-size:12px; color:#e0e7ff; padding:32px 0 40px; }
      .role-badge { font-size:11px; background:#10b981; color:#ffffff; padding:2px 6px; border-radius:6px; margin-left:8px; }
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
                100: '#ffe6d1',
                200: '#ffce99',
                300: '#ffb566',
                400: '#ffa033',
                500: '#ff9900', // base - AWS orange
                600: '#e68a00',
                700: '#cc7a00',
                800: '#b36b00',
                900: '#995c00',
                DEFAULT: '#ff9900',
              },
              dark: {
                900: '#232f3e', // AWS dark blue
              },
            },
          },
        },
      };
    </script>
    <%= (extraHead != null) ? extraHead : "" %>
  </head>
  <body class="min-h-screen min-w-full flex flex-col bg-gray-50">
    <% if (onlyRoot) { %>
      <div id="<%=rid%>"></div>
      <jsp:doBody/>
    <% } else { %>
      <% 
        String currentURI = req.getRequestURI();
        boolean hideHeader = currentURI.endsWith("/login.jsp") || currentURI.endsWith("/register.jsp");
      %>
      <% if (!hideHeader) { %>
      <div class="flex flex-col md:h-screen md:w-full md:flex-row w-screen h-full">
        <header class="">
            <!-- Navigation Menu -->
            <nav class="bg-[#232f3e] text-white w-full h-full md:w-56 flex-shrink-0 shadow-xl">
            <div class="h-16 flex items-center justify-between px-4 text-lg font-semibold tracking-tight">
                <div class="flex items-center gap-3 justify-between w-full">
                <div class="rounded-md bg-white p-1">
                    <img
                    src="<%=ctx%>/static/icon-aws.svg"
                    alt="AWS Cloud School"
                    class="h-8 w-8 block"
                    />
                </div>
                <div class="relative" id="user-menu-container">
                    <button
                    id="user-menu-button"
                    class="flex items-center gap-2 rounded-md bg-white/30 px-3 py-1 text-sm hover:bg-white/20 focus:outline-none text-black box-shadow-md"
                    >
                    <span class="truncate"><%= userName != null ? userName : "사용자" %>
                        <% if (!"student".equalsIgnoreCase(role)) { %>
                        (<%= "admin".equalsIgnoreCase(role) ? "관리자" : role %>)
                        <% } %>
                    </span>
                    <i data-lucide="chevron-down" class="w-4 h-4"></i>
                    </button>
                    <div
                    id="user-menu-dropdown"
                    class="hidden absolute right-0 mt-2 max-w-30 rounded-md bg-white text-black shadow-lg z-20"
                    >
                    <a
                        href="<%=ctx%>/logout"
                        class="block px-4 py-2 text-sm hover:bg-gray-100 rounded-md text-right whitespace-nowrap"
                    >
                        로그아웃
                    </a>
                    </div>
                </div>
                </div>
            </div>
            <ul class="space-y-1 p-2">
                <%-- Dynamic Menu Items --%>
        <% if ("admin".equalsIgnoreCase(role)) { %>
        <li>
          <a href="<%=ctx%>/teacher/studentList" class="group flex items-center gap-3 rounded-md px-3 py-2 text-sm outline-none <%= (req.getRequestURI().endsWith("student-list.jsp") || req.getRequestURI().endsWith("/teacher/studentList") || req.getRequestURI().endsWith("/teacher/students")) ? "bg-primary-600 text-white" : "text-white/90" %> hover:bg-primary-600 hover:text-white">
                    <i data-lucide="users" class="h-5 w-5"></i>
                    <span class="truncate">학생관리</span>
                    </a>
                </li>
                <li>
          <a href="<%=ctx%>/teacher/attendanceList" class="group flex items-center gap-3 rounded-md px-3 py-2 text-sm outline-none <%= req.getRequestURI().contains("/teacher/attendance") ? "bg-primary-600 text-white" : "text-white/90" %> hover:bg-primary-600 hover:text-white">
                    <i data-lucide="calendar-check" class="h-5 w-5"></i>
                    <span class="truncate">출석관리</span>
                    </a>
                </li>
                <% } else if ("student".equalsIgnoreCase(role)) { %>
                <li>
          <a href="<%=ctx%>/student/attendanceList" class="group flex items-center gap-3 rounded-md px-3 py-2 text-sm outline-none <%= req.getRequestURI().endsWith("/attendance-list.jsp") ? "bg-primary-600 text-white" : "text-white/90" %> hover:bg-primary-600 hover:text-white">
                    <i data-lucide="calendar-check" class="h-5 w-5"></i>
                    <span class="truncate">출결 현황</span>
                    </a>
                </li>
                <li>
          <a href="<%=ctx%>/student/attendance-check.jsp" class="group flex items-center gap-3 rounded-md px-3 py-2 text-sm outline-none <%= req.getRequestURI().endsWith("/student/attendance-check.jsp") ? "bg-primary-600 text-white" : "text-white/90" %> hover:bg-primary-600 hover:text-white">
                    <i data-lucide="clipboard-check" class="h-5 w-5"></i>
                    <span class="truncate">출석하기</span>
                    </a>
                </li>
                <% } %>
            </ul>
            </nav>
            <script>
            document.addEventListener("DOMContentLoaded", function () {
                const userMenuButton = document.getElementById("user-menu-button");
                const userMenuDropdown = document.getElementById("user-menu-dropdown");

                userMenuButton.addEventListener("click", function () {
                userMenuDropdown.classList.toggle("hidden");
                });

                document.addEventListener("click", function (event) {
                const menuContainer = document.getElementById("user-menu-container");
                if (!menuContainer.contains(event.target)) {
                    userMenuDropdown.classList.add("hidden");
                }
                });
            });
            </script>
        </header>
        <% } %>
        <main class="flex-1 flex flex-col overflow-auto <%= hideHeader ? "justify-center items-center" : "" %>">
            <jsp:doBody/>
        </main>
      </div>
    <% } %>
  </body>
</html>