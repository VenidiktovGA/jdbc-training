package ru.venidiktov.jdbc.starter;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import ru.venidiktov.jdbc.starter.util.MyConnectionManager;

/**
 * При подключении к базе данных приложение видит мета данные, такие как:
 * все базы данных которые есть на сервере, все схемы в этих базах данных,
 * и в каждой схеме мы можем получить все таблицы, view, materialize view, index, key, constraint и так далее
 */
public class MetaData {
    private static final String DATABASE_NAME = "train_station";

    public static void main(String[] args) throws SQLException {
        /**
         * Для того что бы получить мета данные не нужно делать запросов к базе данных,
         * достаточно иметь соединение с базой данных!
         */
        try (var connection = MyConnectionManager.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();

            var catalogs = metaData.getCatalogs();
            System.out.println("----- Список баз данных на сервере -----");
            while (catalogs.next()) {
                System.out.println(catalogs.getString("TABLE_CAT"));
            }
            System.out.println();

            var schemas = metaData.getSchemas(DATABASE_NAME, "%"); // % - все
            System.out.println("----- Список схем в базе данных %s -----".formatted(DATABASE_NAME));
            while (schemas.next()) {
                System.out.println("Схема %s, каталог %s".formatted(
                        schemas.getString("TABLE_SCHEM"),
                        schemas.getString("TABLE_CATALOG") == null ? "все" : schemas.getString("TABLE_CATALOG"))
                );
            }
            System.out.println();

            var tables = metaData.getTables(DATABASE_NAME, "public", "%", null); // % - все
            System.out.println("----- Список таблиц в базе данных %s схема public -----".formatted(DATABASE_NAME));
            while (tables.next()) {
                System.out.println("Таблица %s типа %s".formatted(
                        tables.getString("TABLE_NAME"),
                        tables.getString("TABLE_TYPE"))
                );
            }
            System.out.println();

            var columns = metaData.getColumns(DATABASE_NAME, "public", "train", null); // % - все
            System.out.println("----- Список колонок в базе данных %s схема public таблица train -----".formatted(DATABASE_NAME));
            while (columns.next()) {
                System.out.println("Таблица %s колонка %s тип данных %s".formatted(
                        columns.getString("TABLE_NAME"),
                        columns.getString("COLUMN_NAME"),
                        columns.getString("TYPE_NAME"))
                );
            }
            System.out.println();
        }
    }
}
