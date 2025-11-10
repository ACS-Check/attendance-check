package attendance.attendance.service;

import attendance.attendance.domain.AttendanceRecord;
import attendance.attendance.repository.AttendanceRecordRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class AttendanceRecordService {

    private final AttendanceRecordRepository attendanceRecordRepository;

    public AttendanceRecord save(AttendanceRecord record) {
        return attendanceRecordRepository.save(record);
    }

    public AttendanceRecord findById(int recordId) {
        return attendanceRecordRepository.findById(recordId);
    }

    public AttendanceRecord findByUserAndCode(int userId, int codeId) {
        return attendanceRecordRepository.findByUserAndCode(userId, codeId);
    }

    public List<AttendanceRecord> findAllByLectureId(int lectureId) {
        return attendanceRecordRepository.findAllByLectureId(lectureId);
    }

    public List<AttendanceRecord> findAllByUserId(int userId) {
        return attendanceRecordRepository.findAllByUserId(userId);
    }

    public void delete(int recordId) {
        attendanceRecordRepository.delete(recordId);
    }

}
