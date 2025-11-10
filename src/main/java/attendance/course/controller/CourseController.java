package attendance.course.controller;

import attendance.course.domain.Course;
import attendance.course.service.CourseService;
import attendance.course.repository.JdbcCourseRepository;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/courses")
public class CourseController extends HttpServlet {

    private final CourseService courseService = new CourseService(new JdbcCourseRepository());

    // Handle GET request
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String courseIdParam = req.getParameter("courseId");
        String teacherIdParam = req.getParameter("teacherId");

        if (courseIdParam != null) {
            try {
                int courseId = Integer.parseInt(courseIdParam);
                Course course = courseService.getCourseById(courseId);
                req.setAttribute("course", course);
                req.getRequestDispatcher("/WEB-INF/views/course/detail.jsp").forward(req, resp);
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid courseId");
            }
        } else if (teacherIdParam != null) {
            try {
                int teacherId = Integer.parseInt(teacherIdParam);
                List<Course> courses = courseService.getCoursesByTeacher(teacherId);
                req.setAttribute("courses", courses);
                req.getRequestDispatcher("/WEB-INF/views/course/list.jsp").forward(req, resp);
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid teacherId");
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing courseId or teacherId parameter");
        }
    }

    // Handle POST request
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String teacherIdParam = req.getParameter("teacherId");

        if (name == null || name.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Course name is required");
            return;
        }

        if (teacherIdParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Teacher ID is required");
            return;
        }

        try {
            int teacherId = Integer.parseInt(teacherIdParam);
            Course course = new Course(0, name, teacherId, null);
            courseService.createCourse(course);
            resp.sendRedirect(req.getContextPath() + "/courses?teacherId=" + teacherId);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid teacherId");
        }
    }

    // Handle DELETE request
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String courseIdParam = req.getParameter("courseId");

        if (courseIdParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Course ID is required for deletion");
            return;
        }

        try {
            int courseId = Integer.parseInt(courseIdParam);
            courseService.deleteCourse(courseId);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid courseId");
        }
    }
}
