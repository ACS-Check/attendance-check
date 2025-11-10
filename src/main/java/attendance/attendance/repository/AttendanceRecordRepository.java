package attendance.attendance.repository;

import attendance.attendance.domain.AttendanceRecord;

import java.util.List;

public interface AttendanceRecordRepository {

    AttendanceRecord save(AttendanceRecord record);

    AttendanceRecord findById(int recordId);

    AttendanceRecord findByUserAndCode(int userId, int codeId);

    List<AttendanceRecord> findAllByLectureId(int lectureId);

    List<AttendanceRecord> findAllByUserId(int userId);

    void delete(int recordId);
}
