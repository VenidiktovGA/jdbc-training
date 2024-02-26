package ru.venidiktov.jdbc.starter;

import java.sql.SQLException;
import ru.venidiktov.jdbc.starter.util.MyConnectionPool;

public class UseMyConnectionPool {

    public static void main(String[] args) throws SQLException {
        String sql = "INSERT INTO train (name) values (?)";
        try (var connection = MyConnectionPool.getConnectionFromPool();
             var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, "hogwarts express");
            preparedStatement.executeUpdate();
        } finally {
            MyConnectionPool.closePool(); // Закрываем все соединения из нашего пула при окончании работы приложения
        }
    }

}
