package attendance.course.repository;

import attendance.course.domain.Course;

import java.util.List;

public interface CourseRepository {
    Course save(Course course);

    List<Course> findAll();

    Course findById(int courseId);

    List<Course> findByTeacherId(int userId);

    void delete(int courseId);
}
