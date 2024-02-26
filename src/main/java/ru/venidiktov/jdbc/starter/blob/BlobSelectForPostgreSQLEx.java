package ru.venidiktov.jdbc.starter.blob;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.util.UUID;
import ru.venidiktov.jdbc.starter.util.ConnectionManager;

/**
 * BLOB (Binary Large Object) - тип данных предназначен для хранение больших бинарных данных,
 * таких как картинки, аудио - видео записи и т.д. В PostgreSQl аналог BLOB называется bytea,
 * соответственно драйвер postgre не поддерживает метод createBlob()
 * <p>
 * Для втавки BLOB и CLOB нужно открывать транзакцию, но для postgres это не нужно так как у него для них
 * есть свои типы данных: bytea, text а это простые типы данных!
 */
public class BlobSelectForPostgreSQLEx {
    public static void main(String[] args) throws SQLException, IOException {

        String selectTrainSql = "SELECT image FROM train where id = ?";
        try (var connection = ConnectionManager.getConnection();
             var prepareStatement = connection.prepareStatement(selectTrainSql)) {

            prepareStatement.setObject(1, UUID.fromString("ff270f0c-c961-4be9-a4be-e642a55f4d60"));
            var result = prepareStatement.executeQuery();

            if (result.next()) {
                var image = result.getBytes("image");// Для postgres BLOB это массив байт!
                Files.write(Path.of("src/main/resources/", "selected-train-image.jpg"), image, StandardOpenOption.CREATE);
            }
        }
    }
}
