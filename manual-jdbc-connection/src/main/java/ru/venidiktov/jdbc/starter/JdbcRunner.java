package ru.venidiktov.jdbc.starter;

import java.sql.SQLException;
import ru.venidiktov.jdbc.starter.util.ConnectionManager;


public class JdbcRunner
{
    public static void main( String[] args ) throws SQLException {
        /**
         * Из DriverManger мы можем получить объект Connection.
         * Connection - представляет из себя соединение с базой данных.
         * Connection нужно закрывать так как на соединение с базой данных тратятся ресурсы (Connection реализует AutoCloseable)
         * Часто используемые методы класса Connection:
         * close() - закрыть соединение с БД
         * commit() - зафиксировать транзакцию
         * rollback() - откатить транзакцию
         * методы для создания сложных объектов createClob()
         * getTransactionIsolation() - показывает уровень изолированности транзакций
         * createStatement() - для создания обычного не изменяющегося запроса типа DDL
         * prepareCall() - вызов хранимых процедур (редко используется)
         * prepareStatement() - для создания запросов с параметрами, например поиск по id где id в запрос можно подставить в приложении
         */
        try (var connection = ConnectionManager.getConnection()) {
            System.out.println("По умолчанию в postgres уровень изоляции транзакций \"read committed\" = цифра 2!");
            System.out.println(connection.getTransactionIsolation());
        }
    }
}
