package attendance.lecture.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Lecture {
    private int lectureId;
    private String name;
    private LocalDate date;
    private LocalTime startedAt;
    private LocalTime endedAt;
    private int courseId;
}
