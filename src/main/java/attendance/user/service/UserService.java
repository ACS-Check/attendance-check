package attendance.user.service;

import attendance.user.domain.User;
import attendance.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    // 회원 가입
    public User register(User user) {
        return userRepository.save(user);
    }

    // 로그인
    public User login(String username, String password) {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (user.getUserName().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        throw new IllegalStateException("Invalid username or password");
    }

    // 회원 탈퇴
    public void deleteUser(int userId) {
        userRepository.delete(userId);
    }
}
