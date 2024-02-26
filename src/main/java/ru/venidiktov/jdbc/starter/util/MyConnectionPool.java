package ru.venidiktov.jdbc.starter.util;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Создадим свой connection pool для того что бы понять как устроены существующие connection pool'ы
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MyConnectionPool {
    private static final String URL_KEY = "db.url";
    private static final String USERNAME_KEY = "db.username";
    private static final String PASSWORD_KEY = "db.password";
    private static final String POOL_SIZE_KEY = "db.pool.size";
    private static final int DEFAULT_POOL_SIZE = 10;
    private static List<Connection> sourceConnections; // Оригинальные connection
    private static BlockingQueue<Connection> proxyPool; // Тут хранятся proxy на connection

    /**
     * Поддержка версий java до 1.8
     */
    static {
        loadDriver();
        initConnectionPool();
    }

    private static void initConnectionPool() {
        var poolSize = PropertiesUtil.get(POOL_SIZE_KEY);
        /**
         * Connection Pool используется во всем приложении всеми потоками,
         * поэтому контейнер соединений должен быть потока безопасным
         */
        var size = poolSize == null ? DEFAULT_POOL_SIZE : Integer.parseInt(poolSize);
        proxyPool = new ArrayBlockingQueue<>(size); // Proxy на соединения чтобы не закрывать их в методе close() а возвращать в пул
        sourceConnections = new ArrayList<>(size); // Оригинальные connection для их закрытия в конце работы приложения

        for (int i = 0; i < size; i++) {
            var connection = getConnection();
            sourceConnections.add(connection);
            /**
             * У нас есть два пути что бы вернуть в нашем самописном пуле соединений соединение в него обратно,
             * 1 Реализовать класс обертку реализовав интерфейс Connection и переопределив все методы там, а в методе
             * close() выполнить pool.add(this)
             * 2 Создать proxy объект на сonnection и засовываем его в пул,
             */
            var proxyConnection = (Connection) Proxy.newProxyInstance(
                    MyConnectionManager.class.getClassLoader(), // Все классы загружаются одним и тем же classLoader'ом или в одной и той же иерархии загрузчика классов
                    new Class[]{Connection.class}, // Интерфейсы которые нам интересны
                    // Если у proxy вызвался метод close() то мы возвращаем объект в пул
                    (proxy, method, args) -> method.getName().equals("close") ? proxyPool.add((Connection) proxy) : method.invoke(connection, args) // Обработчик нашего proxy
            );
            proxyPool.add(proxyConnection); // Добавляем соединение в очередь
        }
    }

    /**
     * Метод дает соединение из нашего пула соединений
     */
    public static Connection getConnectionFromPool() {
        try {
            return proxyPool.take(); // take() вернет Connection если он есть в очереди его если нет он будет ждать
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Так как мы в коллекции храним proxy на реальные соединения и в них переопределено поведение метода close() на
     * то что бы не закрывать соединение а возвращать его в очередь, нам нужна оригинальная коллекция соединений на
     * которую мы создали proxy их мы и будем закрывать
     */
    public static void closePool() {
        for (Connection connection : sourceConnections) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * До java 1.8 при подключении библиотеки вручную в class path они там автоматом не находились,
     * их приходилось загружать вот так
     */
    private static void loadDriver() {
        try {
            Class.forName("org.postgresql.Driver"); // Загружаем класс в metaspace (память jvm)
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Метод закрыть что бы никто не мог создать соединение,
     * если соединение необходимо его можно брать только из connection pool из его очереди
     */
    private static Connection getConnection() {
        try {
            return DriverManager.getConnection(PropertiesUtil.get(URL_KEY), PropertiesUtil.get(USERNAME_KEY), PropertiesUtil.get(PASSWORD_KEY));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
