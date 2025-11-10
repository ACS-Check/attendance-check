package attendance.lecture.repository;

import attendance.lecture.domain.Lecture;

import java.util.List;

public interface LectureRepository {
    Lecture save(Lecture lecture);

    Lecture findById(int lectureId);

    List<Lecture> findByCourseId(int courseId);

    void delete(int lectureId);
}
