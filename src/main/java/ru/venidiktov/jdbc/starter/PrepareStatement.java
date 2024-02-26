package ru.venidiktov.jdbc.starter;

import java.sql.ResultSet;
import java.sql.SQLException;
import ru.venidiktov.jdbc.starter.util.MyConnectionManager;

/**
 * PrepareStatement позволяет избежать sql инъекций
 */
public class PrepareStatement {
    public static void main(String[] args) throws SQLException {
        String sql = "select * from train where name = ?"; // Знак ? обозначает места куда должны быть подставлены значения
        try (var connection = MyConnectionManager.getConnection();
             var preparedStatement = connection.prepareStatement(sql)) {// В PrepareStatement sql запрос нужно передать сразу
            System.out.println("PrepareStatement создан: " + preparedStatement);
            preparedStatement.setString(1, "hogwarts express");
            System.out.println("PrepareStatement установлен параметр: " + preparedStatement);

            ResultSet executeResult = preparedStatement.executeQuery();
            while (executeResult.next()) {
                System.out.println("[|d = '%s' |name = '%s']".formatted(executeResult.getObject("id"), executeResult.getObject("name")));
            }
        }
    }
}
