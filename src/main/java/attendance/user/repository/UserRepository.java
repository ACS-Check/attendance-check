package attendance.user.repository;

import attendance.user.domain.User;

import java.util.List;

public interface UserRepository {
    User save(User user);

    void update(User user);

    User findById(int userId);

    List<User> findAll();

    void delete(int userId);
}
