-- 1) 데이터베이스 생성
CREATE DATABASE IF NOT EXISTS attendance
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

USE attendance;

-- 2) users 테이블
CREATE TABLE IF NOT EXISTS users (
  user_id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  name VARCHAR(100) NOT NULL,
  role ENUM('student','admin') DEFAULT 'student',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 3) attendance_code 테이블
CREATE TABLE IF NOT EXISTS attendance_code (
  code_id INT AUTO_INCREMENT PRIMARY KEY,
  code_value VARCHAR(16) NOT NULL UNIQUE,
  generated_date DATE NOT NULL,
  expired_time TIME NOT NULL
) ENGINE=InnoDB;

-- 4) attendance 테이블
CREATE TABLE IF NOT EXISTS attendance (
  attend_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  attend_date DATE NOT NULL,
  attend_time TIME NOT NULL,
  status ENUM('출석','지각') NOT NULL,
  code_id INT NOT NULL,
  UNIQUE KEY uniq_user_date (user_id, attend_date),
  INDEX idx_user (user_id),
  INDEX idx_code (code_id),
  CONSTRAINT fk_att_user FOREIGN KEY (user_id)
    REFERENCES users(user_id) ON DELETE CASCADE,
  CONSTRAINT fk_att_code FOREIGN KEY (code_id)
    REFERENCES attendance_code(code_id) ON DELETE CASCADE
) ENGINE=InnoDB;
