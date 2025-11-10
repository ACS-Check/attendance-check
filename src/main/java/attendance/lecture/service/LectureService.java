package attendance.lecture.service;

import attendance.lecture.domain.Lecture;
import attendance.lecture.repository.LectureRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class LectureService {
    private final LectureRepository lectureRepository;

    public Lecture createLecture(Lecture lecture) {
        return lectureRepository.save(lecture);
    }

    public Lecture getLectureById(int lectureId) {
        return lectureRepository.findById(lectureId);
    }

    public List<Lecture> getLecturesByCourse(int courseId) {
        return lectureRepository.findByCourseId(courseId);
    }

    public void deleteLecture(int lectureId) {
        lectureRepository.delete(lectureId);
    }

}
