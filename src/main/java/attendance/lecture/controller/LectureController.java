package attendance.lecture.controller;

import attendance.lecture.domain.Lecture;
import attendance.lecture.service.LectureService;
import attendance.lecture.repository.JdbcLectureRepository;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@WebServlet("/lectures")
public class LectureController extends HttpServlet {
    private final LectureService lectureService = new LectureService(new JdbcLectureRepository());

    // 강의 조회
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String lectureIdParam = req.getParameter("lectureId");
        String courseIdParam = req.getParameter("courseId");
        if (lectureIdParam != null) {
            try {
                int lectureId = Integer.parseInt(lectureIdParam);
                Lecture lecture = lectureService.getLectureById(lectureId);
                if (lecture == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                req.setAttribute("lecture", lecture);
                req.getRequestDispatcher("/WEB-INF/views/lecture/detail.jsp").forward(req, resp);
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else if (courseIdParam != null) {
            try {
                int courseId = Integer.parseInt(courseIdParam);
                List<Lecture> lectures = lectureService.getLecturesByCourse(courseId);
                req.setAttribute("lectures", lectures);
                req.getRequestDispatcher("/WEB-INF/views/lecture/list.jsp").forward(req, resp);
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    // 강의 생성
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String courseIdParam = req.getParameter("courseId");
        String dateParam = req.getParameter("date");
        String startedAtParam = req.getParameter("startedAt");
        String endedAtParam = req.getParameter("endedAt");

        if (name == null || courseIdParam == null || dateParam == null || startedAtParam == null || endedAtParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            int courseId = Integer.parseInt(courseIdParam);
            LocalDate date = LocalDate.parse(dateParam);
            LocalTime startedAt = LocalTime.parse(startedAtParam);
            LocalTime endedAt = LocalTime.parse(endedAtParam);

            Lecture lecture = new Lecture();
            lecture.setLectureId(0);
            lecture.setName(name);
            lecture.setCourseId(courseId);
            lecture.setDate(date);
            lecture.setStartedAt(startedAt);
            lecture.setEndedAt(endedAt);
            lectureService.createLecture(lecture);
            resp.sendRedirect(req.getContextPath() + "/lectures?courseId=" + courseId);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    // 강의 삭제
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String lectureIdParam = req.getParameter("lectureId");
        if (lectureIdParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        try {
            int lectureId = Integer.parseInt(lectureIdParam);
            lectureService.deleteLecture(lectureId);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
