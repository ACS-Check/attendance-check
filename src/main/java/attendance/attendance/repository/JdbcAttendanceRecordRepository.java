package attendance.attendance.repository;

import attendance.connection.DBConnectionUtil;
import attendance.attendance.domain.AttendanceRecord;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JdbcAttendanceRecordRepository implements AttendanceRecordRepository {

    private static final String INSERT_SQL =
            "INSERT INTO attendance_record (code_id, user_id, attended_at) VALUES (?, ?, ?)";
    private static final String FIND_BY_ID_SQL =
            "SELECT * FROM attendance_record WHERE record_id = ?";
    private static final String FIND_BY_USER_AND_CODE_SQL =
            "SELECT * FROM attendance_record WHERE user_id = ? AND code_id = ?";
    private static final String FIND_ALL_BY_LECTURE_SQL = """
    SELECT ar.* 
    FROM attendance_record ar
    JOIN attendance_code ac ON ar.code_id = ac.code_id
    WHERE ac.lecture_id = ?;
    """;
    private static final String FIND_ALL_BY_USER_SQL =
            "SELECT * FROM attendance_record WHERE user_id = ?";
    private static final String DELETE_SQL =
            "DELETE FROM attendance_record WHERE record_id = ?";

    @Override
    public AttendanceRecord save(AttendanceRecord record) {
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, record.getRecordId());
            pstmt.setInt(2, record.getUserId());

            Timestamp ts = (record.getAttendedAt() != null)
                    ? Timestamp.valueOf(record.getAttendedAt())
                    : new Timestamp(System.currentTimeMillis());
            pstmt.setTimestamp(3, ts);

            int result = pstmt.executeUpdate();
            if (result == 0) {
                throw new SQLException("Saving attendance record failed");
            }

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    record.setRecordId(rs.getInt(1));
                } else {
                    throw new SQLException("Saving attendance record failed");
                }
            }

            if (record.getAttendedAt() == null) {
                record.setAttendedAt(ts.toLocalDateTime());
            }

            log.info("[ AttendanceRecordRepository ] Saved record: {}", record);
            return record;
        } catch (SQLException e) {
            log.error("[ AttendanceRecordRepository ] Saving attendance record failed", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public AttendanceRecord findById(int recordId) {
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(FIND_BY_ID_SQL)) {
            pstmt.setInt(1, recordId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    AttendanceRecord record = mapRow(rs);
                    log.info("[ AttendanceRecordRepository ] Found record by id {}: {}", recordId, record);
                    return record;
                }
            }
            log.info("[ AttendanceRecordRepository ] No record found with id {}", recordId);
            return null;
        } catch (SQLException e) {
            log.error("[ AttendanceRecordRepository ] Finding attendance record by id failed", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public AttendanceRecord findByUserAndCode(int userId, int codeId) {
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(FIND_BY_USER_AND_CODE_SQL)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, codeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    AttendanceRecord record = mapRow(rs);
                    log.info("[ AttendanceRecordRepository ] Found record by userId {} and codeId {}: {}", userId, codeId, record);
                    return record;
                }
            }
            log.info("[ AttendanceRecordRepository ] No record found with userId {} and codeId {}", userId, codeId);
            return null;
        } catch (SQLException e) {
            log.error("[ AttendanceRecordRepository ] Finding attendance record by user and code failed", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AttendanceRecord> findAllByLectureId(int lectureId) {
        List<AttendanceRecord> records = new ArrayList<>();
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(FIND_ALL_BY_LECTURE_SQL)) {
            pstmt.setInt(1, lectureId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    records.add(mapRow(rs));
                }
            }
            log.info("[ AttendanceRecordRepository ] Found {} records by lectureId {}", records.size(), lectureId);
            return records;
        } catch (SQLException e) {
            log.error("[ AttendanceRecordRepository ] Finding attendance records by lecture id failed", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AttendanceRecord> findAllByUserId(int userId) {
        List<AttendanceRecord> records = new ArrayList<>();
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(FIND_ALL_BY_USER_SQL)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    records.add(mapRow(rs));
                }
            }
            log.info("[ AttendanceRecordRepository ] Found {} records by userId {}", records.size(), userId);
            return records;
        } catch (SQLException e) {
            log.error("[ AttendanceRecordRepository ] Finding attendance records by user id failed", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int recordId) {
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_SQL)) {
            pstmt.setInt(1, recordId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                log.warn("[ AttendanceRecordRepository ] No record deleted for id {}", recordId);
            } else {
                log.info("[ AttendanceRecordRepository ] Deleted record with id {}", recordId);
            }
        } catch (SQLException e) {
            log.error("[ AttendanceRecordRepository ] Deleting attendance record failed", e);
            throw new RuntimeException(e);
        }
    }

    private AttendanceRecord mapRow(ResultSet rs) throws SQLException {
        AttendanceRecord record = new AttendanceRecord();
        record.setRecordId(rs.getInt("record_id"));
        record.setUserId(rs.getInt("user_id"));
        record.setCodeId(rs.getInt("code_id"));
        Timestamp ts = rs.getTimestamp("attended_at");
        if (ts != null) {
            record.setAttendedAt(ts.toLocalDateTime());
        }
        return record;
    }
}
