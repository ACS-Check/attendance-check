-- 1️⃣ 데이터베이스 생성
CREATE DATABASE IF NOT EXISTS attendance
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

USE attendance;

-- 2️⃣ user 테이블 (로그인/회원 관리)
CREATE TABLE IF NOT EXISTS user (
  user_id INT AUTO_INCREMENT PRIMARY KEY,           -- 회원 고유번호
  username VARCHAR(50) NOT NULL UNIQUE,              -- 로그인 아이디
  password VARCHAR(255) NOT NULL,                    -- 암호화된 비밀번호
  name VARCHAR(100) NOT NULL,                        -- 이름
  role ENUM('student','admin') DEFAULT 'student',    -- 권한 구분
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP      -- 가입 일시
) ENGINE=InnoDB;

-- 3️⃣ attendance_code 테이블 (교사 출석코드 생성)
CREATE TABLE IF NOT EXISTS attendance_code (
  code_id INT AUTO_INCREMENT PRIMARY KEY,            -- 코드 고유번호
  code_value VARCHAR(16) NOT NULL UNIQUE,            -- 실제 출석코드 (랜덤 문자열)
  generated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 생성시각
  expires_at DATETIME NOT NULL,                      -- 만료시각
  created_by INT NOT NULL,                           -- 생성한 교사 (user_id)
  FOREIGN KEY (created_by) REFERENCES user(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 4️⃣ attendance 테이블 (학생 출석기록)
CREATE TABLE IF NOT EXISTS attendance (
  attend_id INT AUTO_INCREMENT PRIMARY KEY,          -- 출석 기록 ID
  user_id INT NOT NULL,                              -- 학생 ID
  code_id INT NOT NULL,                              -- 출석코드 ID
  attend_date DATE NOT NULL,                         -- 출석일 (YYYY-MM-DD)
  attend_time TIME NOT NULL,                         -- 출석한 시간
  status ENUM('출석','지각','결석') NOT NULL,        -- 상태
  UNIQUE KEY uniq_user_date (user_id, attend_date),  -- 하루 1회만 출석 가능
  FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE,
  FOREIGN KEY (code_id) REFERENCES attendance_code(code_id) ON DELETE RESTRICT,
  INDEX idx_user (user_id),
  INDEX idx_code (code_id)
) ENGINE=InnoDB;
