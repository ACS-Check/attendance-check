package model;

import java.time.LocalDate;
import java.time.LocalTime;

public class AttendanceRecord {
    private int attendId;         // 출석 ID (PK)
    private int userId;           // 학생 ID (FK)
    private LocalDate attendDate; // 출석 날짜
    private LocalTime attendTime; // 출석 시간
    private String status;        // 출석 상태 (출석/지각/결석)
    private int codeId;           // 사용된 출석 코드 (FK)

    // ✅ 기본 생성자
    public AttendanceRecord() {}

    // ✅ 전체 필드 생성자
    public AttendanceRecord(int attendId, int userId, LocalDate attendDate,
                            LocalTime attendTime, String status, int codeId) {
        this.attendId = attendId;
        this.userId = userId;
        this.attendDate = attendDate;
        this.attendTime = attendTime;
        this.status = status;
        this.codeId = codeId;
    }

    // ✅ Getter & Setter
    public int getAttendId() { return attendId; }
    public void setAttendId(int attendId) { this.attendId = attendId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public LocalDate getAttendDate() { return attendDate; }
    public void setAttendDate(LocalDate attendDate) { this.attendDate = attendDate; }

    public LocalTime getAttendTime() { return attendTime; }
    public void setAttendTime(LocalTime attendTime) { this.attendTime = attendTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getCodeId() { return codeId; }
    public void setCodeId(int codeId) { this.codeId = codeId; }

    @Override
    public String toString() {
        return "AttendanceRecord{" +
                "attendId=" + attendId +
                ", userId=" + userId +
                ", attendDate=" + attendDate +
                ", attendTime=" + attendTime +
                ", status='" + status + '\'' +
                ", codeId=" + codeId +
                '}';
    }
}
