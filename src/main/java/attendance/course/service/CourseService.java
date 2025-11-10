package attendance.course.service;

import attendance.course.domain.Course;
import attendance.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    public Course createCourse(Course course) {
        if (course.getName() == null || course.getName().isEmpty()) {
            throw new IllegalArgumentException("Course name cannot be empty");
        }
        return courseRepository.save(course);
    }

    public List<Course> getCoursesByTeacher(int teacherId) {
        return courseRepository.findByTeacherId(teacherId);
    }

    public Course getCourseById(int courseId) {
        return courseRepository.findById(courseId);
    }

    public void deleteCourse(int courseId) {
        courseRepository.delete(courseId);
    }
}
