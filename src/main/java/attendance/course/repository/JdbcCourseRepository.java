package attendance.course.repository;

import attendance.connection.DBConnectionUtil;
import attendance.course.domain.Course;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
public class JdbcCourseRepository implements CourseRepository {

    private static final String INSERT_SQL =
        "INSERT INTO course (name, user_id) VALUES (?, ?)";
    private static final String FIND_ALL_SQL =
        "SELECT * FROM course";
    private static final String FIND_BY_ID_SQL =
        "SELECT * FROM course WHERE course_id = ?";
    private static final String FIND_BY_TEACHER_ID_SQL =
        "SELECT * FROM course WHERE user_id = ?";
    private static final String DELETE_SQL =
        "DELETE FROM course WHERE course_id = ?";

    @Override
    public Course save(Course course) {
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, course.getName());
            pstmt.setInt(2, course.getUserId());

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) course.setCourseId(rs.getInt(1));
            }
            log.info("[ CourseRepository ] Course saved successfully: name={}, teacherId={}", course.getName(), course.getUserId());
            return course;
        } catch (SQLException e) {
            log.error("[ CourseRepository ] Failed to save course {}", course.getName(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Course> findAll() {
        List<Course> courses = new ArrayList<>();

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(FIND_ALL_SQL);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                courses.add(mapRow(rs));
            }
            log.info("[ CourseRepository ] Retrieved {} courses", courses.size());
            return courses;
        } catch (SQLException e) {
            log.error("[ CourseRepository ] Failed to retrieve courses", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Course findById(int courseId) {
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(FIND_BY_ID_SQL)) {

            pstmt.setInt(1, courseId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Course course = mapRow(rs);
                    log.info("[ CourseRepository ] Course found: id={}, name={}", courseId, course.getName());
                    return course;
                } else {
                    throw new NoSuchElementException("Course not found with courseId=" + courseId);
                }

            }
        } catch (SQLException e) {
            log.error("[ CourseRepository ] Failed to retrieve course {}", courseId, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Course> findByTeacherId(int userId) {
        List<Course> courses = new ArrayList<>();

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(FIND_BY_TEACHER_ID_SQL)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(mapRow(rs));
                }
            }

            log.info("[ CourseRepository ] Retrieved {} courses for teacherId={}", courses.size(), userId);
            return courses;
        } catch (SQLException e) {
            log.error("[ CourseRepository ] Failed to retrieve courses for teacherId={}", userId, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int courseId) {
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_SQL)) {

            pstmt.setInt(1, courseId);
            int result = pstmt.executeUpdate();
            if (result > 0) {
                log.info("[ CourseRepository ] Course {} deleted successfully", courseId);
            } else {
                throw new NoSuchElementException("Course not found with courseId=" + courseId);
            }
        } catch (SQLException e) {
            log.error("[ CourseRepository ] Failed to delete course id={}", courseId, e);
            throw new RuntimeException(e);
        }

    }

    private Course mapRow(ResultSet rs) throws SQLException {
        return new Course(
                rs.getInt("course_id"),
                rs.getString("name"),
                rs.getInt("user_id"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}
