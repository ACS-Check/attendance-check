package attendance.attendance.repository;

import attendance.attendance.domain.AttendanceCode;

public interface AttendanceCodeRepository {
    AttendanceCode save(AttendanceCode attendanceCode);

    AttendanceCode findById(int codeId);

    AttendanceCode findByCode(String code);

    void delete(AttendanceCode attendanceCode);
}
