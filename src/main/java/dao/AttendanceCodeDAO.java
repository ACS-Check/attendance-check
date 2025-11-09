package dao;

import model.AttendanceCode;
import util.CodeUtil;
import util.TimeUtil;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public class AttendanceCodeDAO {

    /** 출석코드 생성 & 저장 (validMinutes: 유효 분) */
    public AttendanceCode create(int validMinutes) {
        String code = CodeUtil.randomCode(6);
        LocalDate genDate = TimeUtil.today();
        LocalTime expTime = TimeUtil.expiresAfterMinutes(validMinutes);

        String sql = "INSERT INTO attendance_code(code_value, generated_date, expired_time) VALUES (?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, code);
            ps.setDate(2, Date.valueOf(genDate));
            ps.setTime(3, Time.valueOf(expTime));
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    AttendanceCode ac = new AttendanceCode();
                    ac.setCodeId(rs.getInt(1));
                    ac.setCodeValue(code);
                    ac.setGeneratedDate(genDate);
                    ac.setExpiredTime(expTime);
                    return ac;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("AttendanceCodeDAO.create failed", e);
        }
        throw new RuntimeException("AttendanceCodeDAO.create failed: no generated key");
    }

    /** 코드 문자열로 조회 */
    public Optional<AttendanceCode> findByValue(String code) {
        String sql = "SELECT code_id, code_value, generated_date, expired_time FROM attendance_code WHERE code_value=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    AttendanceCode ac = new AttendanceCode();
                    ac.setCodeId(rs.getInt("code_id"));
                    ac.setCodeValue(rs.getString("code_value"));
                    ac.setGeneratedDate(rs.getDate("generated_date").toLocalDate());
                    ac.setExpiredTime(rs.getTime("expired_time").toLocalTime());
                    return Optional.of(ac);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("AttendanceCodeDAO.findByValue failed", e);
        }
        return Optional.empty();
    }

    /** 만료 여부 판단 (DATE ≠ 오늘이면 만료, 같으면 TIME 비교) */
    public boolean isExpired(AttendanceCode code) {
        if (!code.getGeneratedDate().isEqual(TimeUtil.today())) return true;
        return TimeUtil.now().isAfter(code.getExpiredTime());
    }
}
