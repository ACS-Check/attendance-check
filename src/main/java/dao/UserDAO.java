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
        String sql = "INSERT INTO user (username, password, name, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, PasswordUtil.hashPassword(user.getPassword()));
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
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT user_id, username, password, name, role, created_at FROM user WHERE username=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = map(rs);
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /** 로그인 검증 (username, password 확인) */
    public Optional<User> validateLogin(String username, String password) {
        Optional<User> userOpt = findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (PasswordUtil.checkPassword(password, user.getPassword())) {
                return Optional.of(user);
            }
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
