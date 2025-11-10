package attendance.user.repository;

import attendance.connection.DBConnectionUtil;
import attendance.user.domain.Role;
import attendance.user.domain.User;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
public class JdbcUserRepository implements UserRepository {

    private static final String INSERT_SQL =
        "INSERT INTO users (username, password, name, role) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_SQL =
        "UPDATE users SET username = ?, password = ?, name = ?, role = ? WHERE user_id = ?";
    private static final String FIND_BY_ID_SQL =
        "SELECT * FROM users WHERE user_id = ?";
    private static final String FIND_ALL_SQL =
        "SELECT * FROM users";
    private static final String DELETE_SQL =
        "DELETE FROM users WHERE user_id = ?";

    @Override
    public User save(User user) {
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL)) {

            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getName());
            pstmt.setString(4, String.valueOf(user.getRole()));
            pstmt.executeUpdate();

            log.info("[ UserRepository ] User created successfully: username={}, role={}", user.getUserName(), user.getRole());
            return user;
        } catch (SQLException e) {
            log.error("[ UserRepository ] DB Error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(User user) {
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_SQL)) {

            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getName());
            pstmt.setString(4, String.valueOf(user.getRole()));
            pstmt.setInt(5, user.getUserId());

            int result = pstmt.executeUpdate();

            if (result > 0) {
                log.info("[ UserRepository ] User updated successfully: username={}", user.getUserName());
            } else {
                throw new NoSuchElementException("User not found with userId=" + user.getUserId());
            }
        } catch (SQLException e) {
            log.error("[ UserRepository ] DB Error: ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public User findById(int userId) {
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(FIND_BY_ID_SQL)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = mapRow(rs);
                    log.info("[ UserRepository ] User found: id={}, username={}", user.getUserId(), user.getUserName());
                    return user;
                } else {
                    throw new NoSuchElementException("User not found with userId=" + userId);
                }
            }
        } catch (SQLException e) {
            log.error("[ UserRepository ] DB Error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(FIND_ALL_SQL);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                User user = mapRow(rs);
                users.add(user);
            }
            log.info("[ UserRepository ] Retrieved {} users", users.size());
            return users;

        } catch (SQLException e) {
            log.error("[ UserRepository ] DB Error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int userId) {
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_SQL)) {

            pstmt.setInt(1, userId);
            int result = pstmt.executeUpdate();

            if (result > 0) {
                log.info("[ UserRepository ] User deleted successfully: userId={}", userId);
            } else {
                throw new NoSuchElementException("User not found with userId=" + userId);
            }
        } catch (SQLException e) {
            log.error("[ UserRepository ] DB Error", e);
            throw new RuntimeException(e);
        }

    }

    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("name"),
                Role.valueOf(rs.getString("role"))
        );
    }
}
