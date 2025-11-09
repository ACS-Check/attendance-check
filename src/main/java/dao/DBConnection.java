package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class DBConnection {
    private static final String HOST = env("DB_HOST", "localhost");
    private static final String PORT = env("DB_PORT", "3306");
    private static final String NAME = env("DB_NAME", "attendance");
    private static final String USER = env("DB_USER", "root");
    private static final String PASS = env("DB_PASS", "0000");

    static {
        try {
            // MySQL 8+ 드라이버 (pom.xml: mysql-connector-j)
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL driver not found", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://" + HOST + ":" + PORT + "/" + NAME
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Seoul&characterEncoding=UTF-8";
        return DriverManager.getConnection(url, USER, PASS);
    }

    private static String env(String k, String def) {
        String v = System.getenv(k);
        return (v == null || v.isBlank()) ? def : v;
    }
}
