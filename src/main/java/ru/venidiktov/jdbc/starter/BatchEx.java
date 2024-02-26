package ru.venidiktov.jdbc.starter;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import ru.venidiktov.jdbc.starter.util.MyConnectionManager;

/**
 * Пакетное обновление позволяет послать 1 запрос базе данных в котором будет вставляться или обновляться много записей например 1000,
 * и благодаря тому что база данных видит что в одном запросе делаются операции над многими записями она может оптимизировать его или распараллелить.
 * <p>
 * Пакетное обновление эффективно когда затраты на TCP/IP запрос и ответ велики,
 * тогда мы упаковываем какое то количество sql запросов в пачку и пачкой отправляем их через 1 TCP/IP запрос в БД.
 */
public class BatchEx {
    public static void main(String[] args) throws SQLException {
        var machinistId = UUID.fromString("a475d863-fe66-4b00-9d7c-367b05a15ccf");

        String deleteMachinistSql = "DELETE FROM machinist where id = '%s'".formatted(machinistId);
        String deleteTrainSql = "DELETE FROM train where machinist_id = '%s'".formatted(machinistId);
        Connection connection = null; // Должна быть видима в блока catch поэтому try with resources не подходит
        Statement statement = null; // Statement позволяет делать Batch, PreparedStatement не позволяет делать Batch
        try {
            connection = MyConnectionManager.getConnection();
            connection.setAutoCommit(false); // Убираем AutoCommit режим

            statement = connection.createStatement();
            statement.addBatch(deleteTrainSql); // Добавляем sql запрос в Batch
            statement.addBatch(deleteMachinistSql); // Добавляем sql запрос в Batch
            var res = statement.executeBatch(); // Выполняем все запросы в Batch, возвращает массив обновленных значений по 1 элементу для каждой команды

            connection.commit(); // Фиксируем транзакция, только если код дойдет до сюда произойдут изменения в БД

        } catch (Exception e) {
            if (connection != null) connection.rollback(); // Закрываем ресурс
            throw e; // Пробрасываем exception который может произойти при rollback
        } finally {
            if (connection != null) connection.close(); // Закрываем ресурс
            if (statement != null) statement.close(); // Закрываем ресурс
        }
    }
}
