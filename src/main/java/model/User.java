package model;

import java.time.LocalDateTime;

/**
 * User 모델 클래스
 * - user 테이블과 매핑
 *   (user_id, username, password, name, role, created_at)
 */
public class User {

    private int userId;             // 회원 고유번호 (PK)
    private String username;        // 로그인 아이디
    private String password;        // 암호화된 비밀번호
    private String name;            // 이름
    private String role;            // 권한 ('student' or 'admin')
    private LocalDateTime createdAt; // 가입 일시

    // ✅ 기본 생성자
    public User() {}

    // ✅ 전체 필드 생성자
    public User(int userId, String username, String password,
                String name, String role, LocalDateTime createdAt) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.name = name;
        this.role = role;
        this.createdAt = createdAt;
    }

    // ✅ Getter & Setter
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
