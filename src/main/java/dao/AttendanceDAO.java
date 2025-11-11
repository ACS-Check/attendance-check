package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import model.AttendanceRecord;
import model.AttendanceSummary;
import util.TimeUtil;

public class AttendanceDAO {

    /** 출석 체크 (하루 1회 제한: 중복이면 기존 레코드 반환) */
    public Optional<AttendanceRecord> markAttendance(int userId, int codeId, String status) {
        LocalDate d = TimeUtil.today();
        String sql = "INSERT INTO attendance(user_id, attend_date, attend_time, status, code_id) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(d));
            ps.setTime(3, Time.valueOf(TimeUtil.now()));
            ps.setString(4, status);
            ps.setInt(5, codeId);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return findById(id);
                }
            }
        } catch (SQLException e) {
            // 중복키(유니크 user_id+attend_date) → 오늘껀 이미 있음
            if ("23000".equals(e.getSQLState()) || (e.getMessage() != null && e.getMessage().contains("Duplicate"))) {
                return findTodayByUser(userId);
            }
            throw new RuntimeException("AttendanceDAO.markAttendance failed", e);
        }
        return Optional.empty();
    }

    public List<AttendanceRecord> findByUserAndMonth(int userId, YearMonth ym) {
        LocalDate from = ym.atDay(1);
        LocalDate to = ym.atEndOfMonth();
        String q = "SELECT attend_id,user_id,attend_date,attend_time,status,code_id " +
                   "FROM attendance WHERE user_id=? AND attend_date BETWEEN ? AND ? ORDER BY attend_date";

        List<AttendanceRecord> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(q)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(from));
            ps.setDate(3, Date.valueOf(to));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("AttendanceDAO.findByUserAndMonth failed", e);
        }
        return list;
    }

    public List<AttendanceRecord> findByDate(LocalDate date) {
        String q = "SELECT attend_id,user_id,attend_date,attend_time,status,code_id " +
                   "FROM attendance WHERE attend_date=? ORDER BY attend_time";

        List<AttendanceRecord> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(q)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("AttendanceDAO.findByDate failed", e);
        }
        return list;
    }

    public List<AttendanceSummary> getAttendanceSummary() {
        String sql = "SELECT attend_date, COUNT(*) AS attendance_count, " +
                     "(SELECT COUNT(*) FROM users WHERE role='student') AS total_students " +
                     "FROM attendance GROUP BY attend_date ORDER BY attend_date";

        List<AttendanceSummary> summary = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                AttendanceSummary row = new AttendanceSummary(
                    rs.getDate("attend_date").toLocalDate(),
                    rs.getInt("attendance_count"),
                    rs.getInt("total_students")
                );
                summary.add(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException("AttendanceDAO.getAttendanceSummary failed", e);
        }
        return summary;
    }

    // ---- private helpers ----

    private Optional<AttendanceRecord> findById(int id) {
        String q = "SELECT attend_id,user_id,attend_date,attend_time,status,code_id FROM attendance WHERE attend_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(q)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("AttendanceDAO.findById failed", e);
        }
        return Optional.empty();
    }

    private Optional<AttendanceRecord> findTodayByUser(int userId) {
        String q = "SELECT attend_id,user_id,attend_date,attend_time,status,code_id " +
                   "FROM attendance WHERE user_id=? AND attend_date=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(q)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(TimeUtil.today()));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("AttendanceDAO.findTodayByUser failed", e);
        }
        return Optional.empty();
    }

    private AttendanceRecord map(ResultSet rs) throws SQLException {
        AttendanceRecord r = new AttendanceRecord();
        r.setAttendId(rs.getInt("attend_id"));
        r.setUserId(rs.getInt("user_id"));
        r.setAttendDate(rs.getDate("attend_date").toLocalDate());
        r.setAttendTime(rs.getTime("attend_time").toLocalTime());
        r.setStatus(rs.getString("status"));
        r.setCodeId(rs.getInt("code_id"));
        return r;
    }
}
