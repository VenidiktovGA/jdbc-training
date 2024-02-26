package ru.venidiktov.jdbc.starter.clob;

import java.io.IOException;
import java.sql.SQLException;
import ru.venidiktov.jdbc.starter.util.MyConnectionManager;

/**
 * BLOB (Binary Large Object) - тип данных предназначен для хранение больших бинарных данных,
 * таких как картинки, аудио - видео записи и т.д. В PostgreSQl аналог BLOB называется bytea,
 * соответственно драйвер postgre не поддерживает метод createBlob()
 * <p>
 * Для втавки BLOB и CLOB нужно открывать транзакцию, но для postgres это не нужно так как у него для них
 * есть свои типы данных: bytea, text а это простые типы данных!
 */
public class ClobForPostgreSQLEx {
    public static void main(String[] args) throws SQLException, IOException {

        String insertTrainSql = "INSERT INTO train (name, image) values(?, ?)";
        try (var connection = MyConnectionManager.getConnection();
             var prepareStatement = connection.prepareStatement(insertTrainSql)) {

            /**
             * Так как для postgres CLOB это обычная строка самому управлять транзакцией не нужно
             */
            prepareStatement.setString(1, "hogwarts express");
            // Для posrgres СLOB это обычная строка
            prepareStatement.setString(2, "Очень большой объем символьных данных");
            prepareStatement.executeUpdate();
        }
    }
}
