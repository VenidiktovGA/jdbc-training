package ru.venidiktov.jdbc.starter.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConnectionManager {
    private static final String URL_KEY = "db.url";
    private static final String USERNAME_KEY = "db.username";
    private static final String PASSWORD_KEY = "db.password";

    /**
     * Поддержка версий java до 1.8
     */
    static  {
        loadDriver();
    }

    /**
     * До java 1.8 при подключении библиотеки вручную в class path они там автоматом не находились,
     * их приходилось загружать вот так
     */
    private static void loadDriver() {
        try {
            Class.forName("org.postgresql.Driver"); // Загружаем класс в metaspace (память jvm)
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(PropertiesUtil.get(URL_KEY), PropertiesUtil.get(USERNAME_KEY), PropertiesUtil.get(PASSWORD_KEY));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
