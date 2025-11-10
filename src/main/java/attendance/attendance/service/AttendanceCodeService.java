package attendance.attendance.service;

import attendance.attendance.domain.AttendanceCode;
import attendance.attendance.repository.AttendanceCodeRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Random;

@RequiredArgsConstructor
public class AttendanceCodeService {
    private final AttendanceCodeRepository attendanceCodeRepository;

    private final int CODE_LENGTH = 6;

    public AttendanceCode createCode(int lectureId, int teacherId) {
        String code;

        while (true) {
            code = generateRandomCode(CODE_LENGTH);
            try {
                attendanceCodeRepository.findByCode(code);
            } catch (NoSuchElementException e) {
                break;
            }
        }

        LocalDateTime now = LocalDateTime.now();

        AttendanceCode newCode = new AttendanceCode(
                0,
                code,
                teacherId,
                now,
                now.plusMinutes(10),
                lectureId
        );
        return attendanceCodeRepository.save(newCode);
    }

    public AttendanceCode getByCodeId(int codeId) {
        return attendanceCodeRepository.findById(codeId);
    }

    public AttendanceCode getByCode(String code) {
        return attendanceCodeRepository.findByCode(code);
    }

    public void deleteCode(AttendanceCode code) {
        attendanceCodeRepository.delete(code);
    }

    // 랜덤 코드 생성
    private String generateRandomCode(int length) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }


}
