package attendance.lecture.repository;

import attendance.connection.DBConnectionUtil;
import attendance.lecture.domain.Lecture;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
public class JdbcLectureRepository implements LectureRepository {

    private static final String INSERT_SQL =
        "INSERT INTO lecture (name, date, started_at, ended_at, course_id) VALUES (?, ?, ?, ?, ?)";
    private static final String FIND_BY_ID_SQL =
        "SELECT * FROM lecture WHERE lecture_id = ?";
    private static final String FIND_BY_COURSE_ID_SQL =
        "SELECT * FROM lecture WHERE course_id = ?";
    private static final String DELETE_SQL =
        "DELETE FROM lecture WHERE lecture_id = ?";

    @Override
    public Lecture save(Lecture lecture) {

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, lecture.getName());
            pstmt.setDate(2, java.sql.Date.valueOf(lecture.getDate()));
            pstmt.setTime(3, java.sql.Time.valueOf(lecture.getStartedAt()));
            pstmt.setTime(4, java.sql.Time.valueOf(lecture.getEndedAt()));
            pstmt.setInt(5, lecture.getCourseId());

            int result = pstmt.executeUpdate();
            if (result == 0) {
                throw new SQLException("Lecture insert failed");
            }

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    lecture.setLectureId(rs.getInt(1));
                }
            }
            log.info("[ LectureRepository ] Lecture saved successfully: {}", lecture);
            return lecture;
        } catch (SQLException e) {
            log.error("[ LectureRepository ] Lecture insert failed: {}", lecture.getName(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Lecture findById(int lectureId) {

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(FIND_BY_ID_SQL)) {

            pstmt.setInt(1, lectureId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                } else {
                    throw new NoSuchElementException("Lecture not found with lectureId=" + lectureId);
                }
            }
        } catch (SQLException e) {
            log.error("[ LectureRepository ] Failed to retrieve Lecture for lectureId={}", lectureId, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Lecture> findByCourseId(int courseId) {
        List<Lecture> lectures = new ArrayList<>();

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(FIND_BY_COURSE_ID_SQL)) {
            pstmt.setInt(1, courseId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lectures.add(mapRow(rs));
                }
            }
            log.info("[ LectureRepository ] Found {} lectures for courseId={}", lectures.size(), courseId);
            return lectures;
        } catch (SQLException e) {
            log.error("[ LectureRepository ] Failed to retrieve lectures for courseId={}", courseId, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int lectureId) {

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_SQL)) {
            pstmt.setInt(1, lectureId);
            int result = pstmt.executeUpdate();

            if (result == 0) {
                throw new NoSuchElementException("Lecture not found with lectureId=" + lectureId);
            } else {
                log.info("[ LectureRepository ] Lecture deleted successfully: lectureId={}", lectureId);
            }
        } catch (SQLException e) {
            log.error("[ LectureRepository ] Failed to delete lecture with lectureId={}", lectureId, e);
            throw new RuntimeException(e);
        }
    }

    private Lecture mapRow(ResultSet rs) throws SQLException {
        Time startedAt = rs.getTime("started_at");
        Time endedAt = rs.getTime("ended_at");

        return new Lecture(
                rs.getInt("lecture_id"),
                rs.getString("name"),
                rs.getDate("date").toLocalDate(),
                startedAt != null ? startedAt.toLocalTime() : null,
                endedAt != null ? endedAt.toLocalTime() : null,
                rs.getInt("course_id")
        );
    }
}
