package attendance.attendance.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRecord {
    private int recordId;
    private LocalDateTime attendedAt;
    private int codeId;
    private int userId;
}
