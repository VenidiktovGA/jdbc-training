package ru.venidiktov.jdbc.starter.blob;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import ru.venidiktov.jdbc.starter.util.ConnectionManager;

/**
 * BLOB (Binary Large Object) - тип данных предназначен для хранение больших бинарных данных,
 * таких как картинки, аудио - видео записи и т.д. В PostgreSQl аналог BLOB называется bytea,
 * соответственно драйвер postgre не поддерживает метод createBlob()
 * <p>
 * Для втавки BLOB и CLOB нужно открывать транзакцию, но для postgres это не нужно так как у него для них
 * есть свои типы данных: bytea, text а это простые типы данных!
 */
public class BlobInsertForPostgreSQLEx {
    public static void main(String[] args) throws SQLException, IOException {

        String insertTrainSql = "INSERT INTO train (name, image) values(?, ?)";
        try (var connection = ConnectionManager.getConnection();
             var prepareStatement = connection.prepareStatement(insertTrainSql)) {

            /**
             * Так как для postgres BLOB это обычный массив байт самому управлять транзакцией не нужно
             */
            var blob = Files.readAllBytes(Path.of("src/main/resources/", "hogwarts-express.jpg"));

            prepareStatement.setString(1, "hogwarts express");
            prepareStatement.setBytes(2, blob); // Для posrgres BLOB это обычный массив байт
            prepareStatement.executeUpdate();
        }
    }
}
