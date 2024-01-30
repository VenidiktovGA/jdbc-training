package ru.venidiktov.jdbc.starter.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionManager {
    private static final String URL = "jdbc:postgresql://localhost:5447/train_station";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "postgres";

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
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
