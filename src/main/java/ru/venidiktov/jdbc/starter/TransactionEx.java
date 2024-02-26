package ru.venidiktov.jdbc.starter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import ru.venidiktov.jdbc.starter.util.MyConnectionManager;

/**
 * Транзакция - это неделимая атомарна единица работы в рамках соединения с базой данных
 * В данном примере делаем два запроса которые обязательно должны быть выполнены оба,
 * в любых других случаях запросы не должны выполнится вовсе, например если хотя бы один из них завершился неудачно
 */
public class TransactionEx {
    public static void main(String[] args) throws SQLException {

        String deleteMachinistSql = "DELETE FROM machinist where id = ?";
        String deleteTrainSql = "DELETE FROM train where machinist_id = ?";
        Connection connection = null; // Должна быть видима в блока catch поэтому try with resources не подходит
        PreparedStatement deleteTrain = null; // Должна быть видима в блока catch поэтому try with resources не подходит
        PreparedStatement deleteMachinist = null; // Должна быть видима в блока catch поэтому try with resources не подходит
        try {
            connection = MyConnectionManager.getConnection();
            deleteTrain = connection.prepareStatement(deleteTrainSql);
            deleteMachinist = connection.prepareStatement(deleteMachinistSql);
            var machinistId = UUID.fromString("4b7a8ba8-6d34-49fc-a81c-1c8ab0d34b96");
            deleteMachinist.setObject(1, machinistId);
            deleteTrain.setObject(1, machinistId);

            connection.setAutoCommit(false); // Убираем AutoCommit режим

            deleteTrain.executeUpdate();
            deleteMachinist.executeUpdate();

            connection.commit(); // Фиксируем транзакция, только если код дойдет до сюда произойдут изменения в БД

        } catch (Exception e) {
            if (connection != null) connection.rollback(); // Закрываем ресурс
            throw e; // Пробрасываем exception который может произойти при rollback
        } finally {
            if (connection != null) connection.close(); // Закрываем ресурс
            if (deleteTrain != null) deleteTrain.close(); // Закрываем ресурс
            if (deleteMachinist != null) deleteMachinist.close(); // Закрываем ресурс
        }
    }
}
