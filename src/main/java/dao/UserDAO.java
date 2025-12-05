package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import model.User;
import util.PasswordUtil;

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

    /** user_id로 회원 조회 (JWT 토큰용) */
    public User findById(int userId) {
        String sql = "SELECT user_id, username, password, name, role, created_at FROM users WHERE user_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
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
            throw new RuntimeException("UserDAO.findById failed", e);
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

    /** role이 'student'인 사용자 전체 목록 */
    public List<User> listStudents() {
        String sql = "SELECT user_id, username, password, name, role, created_at FROM users WHERE role = 'student' ORDER BY created_at DESC";
        List<User> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("UserDAO.listStudents failed", e);
        }
        return list;
    }

    /** 검색/페이징: q는 username/name LIKE, includeInactive가 true면 active 조건 해제 */
    public List<User> listStudentsPaged(String q, int page, int size) {
        int offset = Math.max(0, (page - 1) * size);
        StringBuilder sb = new StringBuilder("SELECT user_id, username, password, name, role, created_at FROM users WHERE role='student'");
        if (q != null && !q.isBlank()) sb.append(" AND (username LIKE ? OR name LIKE ?)");
        sb.append(" ORDER BY created_at DESC LIMIT ? OFFSET ?");
        List<User> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sb.toString())) {
            int idx = 1;
            if (q != null && !q.isBlank()) {
                String like = "%" + q + "%";
                ps.setString(idx++, like);
                ps.setString(idx++, like);
            }
            ps.setInt(idx++, size);
            ps.setInt(idx, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("UserDAO.listStudentsPaged failed", e);
        }
        return list;
    }

    public int countStudents(String q) {
        StringBuilder sb = new StringBuilder("SELECT COUNT(*) FROM users WHERE role='student'");
        if (q != null && !q.isBlank()) sb.append(" AND (username LIKE ? OR name LIKE ?)");
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sb.toString())) {
            int idx = 1;
            if (q != null && !q.isBlank()) {
                String like = "%" + q + "%";
                ps.setString(idx++, like);
                ps.setString(idx, like);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("UserDAO.countStudents failed", e);
        }
        return 0;
    }

    // restoreStudent removed (active 컬럼 제거)

    /** 학생 삭제 (admin 용) */
    public boolean deleteStudent(int userId) {
        String sql = "DELETE FROM users WHERE user_id=? AND role='student'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("UserDAO.deleteStudent failed", e);
        }
    }

    /** 학생 비밀번호 초기화/변경 */
    public boolean resetPassword(int userId, String rawPassword) {
        String hashed = PasswordUtil.hashPassword(rawPassword);
        String sql = "UPDATE users SET password=? WHERE user_id=? AND role='student'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hashed);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("UserDAO.resetPassword failed", e);
        }
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
