package ru.venidiktov.jdbc.starter;

import java.sql.ResultSet;
import java.sql.SQLException;
import ru.venidiktov.jdbc.starter.util.ConnectionManager;

/**
 * FetchSize параметр определяющий максимальное количество записей которое будет изыматься по очереди из выборки возникшей в результате выполнении запроса в БД
 * setQueryTimeout - устанавливает максимальное время которое мы будем ожидать результата прежде чем разорвем соединение
 * setMaxRows - максимальное количество записей в результате которое можно вернуть
 */
// TODO хорошо бы увидеть логи что запрос не делается 3 раза а всего 1 и потом просто из результата достаются данные!
public class FetchSize {
    public static void main(String[] args) throws SQLException {
        String sql = "select * from train where name = ?";
        try (var connection = ConnectionManager.getConnection();
             var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setFetchSize(2); // FetchSize указывается для каждого запроса
            preparedStatement.setQueryTimeout(2); // Максимальное количество секунд ожидания перед закрытием соединения если ответ не получен!
            preparedStatement.setMaxRows(1); // Для всех запросов устанавливает максимум записей для выборки, по сути проставляет limit в sql запрос!
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
