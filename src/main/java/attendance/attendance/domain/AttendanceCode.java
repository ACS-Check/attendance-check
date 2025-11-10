package attendance.attendance.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceCode {
    private int codeId;
    private String code;
    private int createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private int lectureId;

    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
}
