package ru.venidiktov.jdbc.starter.blob;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import ru.venidiktov.jdbc.starter.util.MyConnectionManager;

/**
 * BLOB (Binary Large Object) - тип данных предназначен для хранение больших бинарных данных,
 * таких как картинки, аудио - видео записи и т.д. В PostgreSQl аналог BLOB называется bytea,
 * соответственно драйвер postgre не поддерживает метод createBlob()
 * <p>
 * Для втавки BLOB и CLOB нужно открывать транзакцию, но для postgres это не нужно так как у него для них
 * есть свои типы данных: bytea, text
 */
public class BlobInsertExceptPostgreSQLEx {
    public static void main(String[] args) throws SQLException, IOException {

        String insertTrainSql = "INSERT INTO train (name, image) values(?, ?)";
        Connection connection = null;
        PreparedStatement prepareStatement = null;
        try {
            connection = MyConnectionManager.getConnection();
            prepareStatement = connection.prepareStatement(insertTrainSql);
            connection.setAutoCommit(false); // Убираем AutoCommit режим

            /**
             * BLOB это сложный объект нужно самому управлять транзакцией
             */
            var blob = connection.createBlob(); // Создали объект с типом BLOB
            // В BLOB положили с самого начала байты которые представляют собой картинку
            blob.setBytes(1, Files.readAllBytes(Path.of("resources", "hogwarts-express.jpg")));

            prepareStatement.setString(1, "hogwarts express");
            prepareStatement.setBlob(2, blob);
            prepareStatement.executeUpdate();

            connection.commit(); // Фиксируем изменения
        } catch (Exception e) {
            if (connection != null) connection.rollback(); // Закрываем ресурс
            throw e; // Пробрасываем exception который может произойти при rollback
        } finally {
            if (connection != null) connection.close(); // Закрываем ресурс
        }
    }
}
