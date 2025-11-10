package attendance.attendance.repository;

import attendance.connection.DBConnectionUtil;
import attendance.attendance.domain.AttendanceCode;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.NoSuchElementException;

@Slf4j
public class JdbcAttendanceCodeRepository implements AttendanceCodeRepository {

    private static final String INSERT_SQL =
        "INSERT INTO attendance_code (code, created_by, created_at, expires_at, lecture_id) VALUES (?, ?, ?, ?, ?)";
    private static final String FIND_BY_ID_SQL =
        "SELECT * FROM attendance_code WHERE code_id = ?";
    private static final String FIND_BY_CODE_SQL =
        "SELECT * FROM attendance_code WHERE code = ?";
    private static final String FIND_ALL_BY_LECTURE_SQL =
        "SELECT * FROM attendance_code WHERE lecture_id = ?";
    private static final String DELETE_SQL =
        "DELETE FROM attendance_code WHERE code_id = ?";

    @Override
    public AttendanceCode save(AttendanceCode attendanceCode) {
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, attendanceCode.getCode());
            pstmt.setInt(2, attendanceCode.getCreatedBy());
            pstmt.setTimestamp(3, Timestamp.valueOf(attendanceCode.getCreatedAt()));
            pstmt.setTimestamp(4, Timestamp.valueOf(attendanceCode.getExpiresAt()));
            pstmt.setInt(5, attendanceCode.getLectureId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating attendance code failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    attendanceCode.setCodeId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating attendance code failed, no ID obtained.");
                }
            }
            log.info("[ AttendanceCodeRepository ] Saved AttendanceCode with ID: {}", attendanceCode.getCodeId());
            return attendanceCode;
        } catch (SQLException e) {
            log.error("[ AttendanceCodeRepository ] Error saving AttendanceCode: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public AttendanceCode findById(int codeId) {
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(FIND_BY_ID_SQL)) {

            pstmt.setInt(1, codeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    AttendanceCode attendanceCode = mapRow(rs);
                    log.info("[ AttendanceCodeRepository ] Found AttendanceCode by ID {}: {}", codeId, attendanceCode);
                    return attendanceCode;
                } else {
                    log.warn("[ AttendanceCodeRepository ] No AttendanceCode found with ID {}", codeId);
                    throw new NoSuchElementException("No AttendanceCode found with ID " + codeId);
                }
            }
        } catch (SQLException e) {
            log.error("[ AttendanceCodeRepository ] Error finding AttendanceCode by ID {}: {}", codeId, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public AttendanceCode findByCode(String code) {
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(FIND_BY_CODE_SQL)) {

            pstmt.setString(1, code);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    AttendanceCode attendanceCode = mapRow(rs);
                    log.info("[ AttendanceCodeRepository ] Found AttendanceCode by code {}: {}", code, attendanceCode);
                    return attendanceCode;
                } else {
                    log.warn("[ AttendanceCodeRepository ] No AttendanceCode found with code {}", code);
                    throw new NoSuchElementException("No AttendanceCode found with code " + code);
                }
            }
        } catch (SQLException e) {
            log.error("[ AttendanceCodeRepository ] Error finding AttendanceCode by code {}: {}", code, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(AttendanceCode attendanceCode) {
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_SQL)) {

            pstmt.setInt(1, attendanceCode.getCodeId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                log.info("[ AttendanceCodeRepository ] Deleted AttendanceCode with ID {}", attendanceCode.getCodeId());
            } else {
                log.warn("[ AttendanceCodeRepository ] No AttendanceCode found to delete with ID {}", attendanceCode.getCodeId());
            }
        } catch (SQLException e) {
            log.error("[ AttendanceCodeRepository ] Error deleting AttendanceCode with ID {}: {}", attendanceCode.getCodeId(), e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private AttendanceCode mapRow(ResultSet rs) throws SQLException {
        AttendanceCode attendanceCode = new AttendanceCode();
        attendanceCode.setCodeId(rs.getInt("code_id"));
        attendanceCode.setCode(rs.getString("code"));
        attendanceCode.setCreatedBy(rs.getInt("created_by"));
        Timestamp createdAtTs = rs.getTimestamp("created_at");
        if (createdAtTs != null) {
            attendanceCode.setCreatedAt(createdAtTs.toLocalDateTime());
        }
        Timestamp expiresAtTs = rs.getTimestamp("expires_at");
        if (expiresAtTs != null) {
            attendanceCode.setExpiresAt(expiresAtTs.toLocalDateTime());
        }
        attendanceCode.setLectureId(rs.getInt("lecture_id"));
        return attendanceCode;
    }
}
