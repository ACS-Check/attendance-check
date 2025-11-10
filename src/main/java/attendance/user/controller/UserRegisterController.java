package attendance.user.controller;

import attendance.user.domain.User;
import attendance.user.domain.Role;
import attendance.user.service.UserService;
import attendance.user.repository.JdbcUserRepository;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/users/register")
public class UserRegisterController extends HttpServlet {

    private final UserService userService = new UserService(new JdbcUserRepository());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/user/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String name = request.getParameter("name");
        String roleParam = request.getParameter("role");
        Role role = Role.valueOf(roleParam.toUpperCase());

        User user = new User(0, username, password, name, role);
        userService.register(user);

        response.sendRedirect(request.getContextPath() + "/users/login");
    }
}
