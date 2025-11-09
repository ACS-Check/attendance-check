package util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

public class TimeUtil {
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    public static LocalDate today() { return LocalDate.now(KST); }
    public static LocalTime now()   { return LocalTime.now(KST); }

    // 만료시간 = 현재시간 + N분 (TIME 컬럼에 저장용)
    public static LocalTime expiresAfterMinutes(int minutes) {
        return now().plusMinutes(minutes);
    }
}
