package util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * PasswordUtil
 * - 비밀번호 암호화 및 검증 유틸리티 클래스
 * - BCrypt 알고리즘 사용 (단방향 해시)
 */
public class PasswordUtil {

    /**
     * 비밀번호 해시 생성
     * @param plainPassword 사용자가 입력한 평문 비밀번호
     * @return 해시된 비밀번호 문자열
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isBlank()) {
            throw new IllegalArgumentException("비밀번호가 비어 있습니다.");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12)); // 12 라운드 해시
    }

    /**
     * 비밀번호 일치 여부 검증
     * @param plainPassword 입력된 평문 비밀번호
     * @param hashedPassword DB에 저장된 해시된 비밀번호
     * @return 비밀번호가 일치하면 true, 아니면 false
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) return false;
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
