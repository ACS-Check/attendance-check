package dao;

import model.User;
import util.PasswordUtil;

import java.sql.*;
import java.util.Optional;

/**
 * UserDAO
 * - 회원 정보 조회, 등록, 로그인 검증
 */
public class UserDAO {

    /** 회원 등록 */
    public boolean insertUser(User user) {
        String sql = "INSERT INTO users (username, password, name, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, PasswordUtil.hashPassword(user.getPassword())); // 비밀번호 해시 저장
            ps.setString(3, user.getName());
            ps.setString(4, user.getRole());
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** username으로 회원 조회 */
    public User findByUsername(String username) {
        String sql = "SELECT user_id, username, password, name, role, created_at FROM users WHERE username=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setUserId(rs.getInt("user_id"));
                    u.setUsername(rs.getString("username"));
                    u.setPassword(rs.getString("password"));
                    u.setName(rs.getString("name"));
                    u.setRole(rs.getString("role"));
                    u.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return u;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("UserDAO.findByUsername failed", e);
        }
        return null;
    }

    /** 로그인 검증 (username, password 확인) */
    public Optional<User> validateLogin(String username, String password) {
        User user = findByUsername(username); // Optional 제거 → User 직접 반환
        if (user != null && PasswordUtil.checkPassword(password, user.getPassword())) {
            return Optional.of(user);
        }
        return Optional.empty();
    }

    // ---- private helper ----
    private User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setName(rs.getString("name"));
        u.setRole(rs.getString("role"));
        u.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return u;
    }
}
