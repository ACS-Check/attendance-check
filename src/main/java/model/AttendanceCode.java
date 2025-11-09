package model;

import java.time.LocalDate;
import java.time.LocalTime;

public class AttendanceCode {
    private int codeId;               // 코드 고유번호 (PK)
    private String codeValue;         // 실제 출석 코드 (예: ABC123)
    private LocalDate generatedDate;  // 생성일
    private LocalTime expiredTime;    // 만료시간

    // ✅ 기본 생성자
    public AttendanceCode() {}

    // ✅ 전체 필드 생성자 (선택)
    public AttendanceCode(int codeId, String codeValue, LocalDate generatedDate, LocalTime expiredTime) {
        this.codeId = codeId;
        this.codeValue = codeValue;
        this.generatedDate = generatedDate;
        this.expiredTime = expiredTime;
    }

    // ✅ Getter & Setter
    public int getCodeId() { return codeId; }
    public void setCodeId(int codeId) { this.codeId = codeId; }

    public String getCodeValue() { return codeValue; }
    public void setCodeValue(String codeValue) { this.codeValue = codeValue; }

    public LocalDate getGeneratedDate() { return generatedDate; }
    public void setGeneratedDate(LocalDate generatedDate) { this.generatedDate = generatedDate; }

    public LocalTime getExpiredTime() { return expiredTime; }
    public void setExpiredTime(LocalTime expiredTime) { this.expiredTime = expiredTime; }

    @Override
    public String toString() {
        return "AttendanceCode{" +
                "codeId=" + codeId +
                ", codeValue='" + codeValue + '\'' +
                ", generatedDate=" + generatedDate +
                ", expiredTime=" + expiredTime +
                '}';
    }
}
