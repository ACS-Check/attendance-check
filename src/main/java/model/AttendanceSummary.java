package model;

import java.time.LocalDate;

/**
 * 날짜별 출석 요약 DTO
 */
public class AttendanceSummary {
    private LocalDate date;       // 날짜
    private int attendanceCount;  // 출석 인원 수
    private int totalStudents;    // 전체 학생 수

    public AttendanceSummary() {}

    public AttendanceSummary(LocalDate date, int attendanceCount, int totalStudents) {
        this.date = date;
        this.attendanceCount = attendanceCount;
        this.totalStudents = totalStudents;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getAttendanceCount() {
        return attendanceCount;
    }

    public void setAttendanceCount(int attendanceCount) {
        this.attendanceCount = attendanceCount;
    }

    public int getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(int totalStudents) {
        this.totalStudents = totalStudents;
    }
}
