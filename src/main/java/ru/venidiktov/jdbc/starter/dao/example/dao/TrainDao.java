package ru.venidiktov.jdbc.starter.dao.example.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.SneakyThrows;
import ru.venidiktov.jdbc.starter.dao.example.dao.dto.TrainFilter;
import ru.venidiktov.jdbc.starter.dao.example.entity.Train;
import ru.venidiktov.jdbc.starter.dao.example.exception.DaoException;
import ru.venidiktov.jdbc.starter.util.MyConnectionPool;

/**
 * Согласно паттерну DAO этот класс представляет уровень доступа к хранилищу данных.
 * Класс DAO должен быть singleton, хоть у него и нет состояния, он будет потока безопасный!
 * <p>
 * Да singleton анти паттенр, но иногда без него не обойтись! Используем Enum для singleton
 * Не следует делать класс DAO final так как ORM framwork'и очень часто делают proxy на наши классы
 */
//@NoArgsConstructor(access = AccessLevel.PRIVATE)
public enum TrainDao {
    INSTANCE;

    private static final String DELETE_TRAIN = "DELETE FROM train WHERE id = ?";
    private static final String INSERT_TRAIN = "INSERT INTO train (name, machinist_id, image) values(?, ?, ?)";
    private static final String UPDATE_TRAIN = "UPDATE train SET name = ?, machinist_id = ?, image = ? where id = ?";
    private static final String SELECT_ALL_TRAIN = "SELECT id, name, machinist_id, image from train";
    private static final String SELECT_TRAIN_BY_ID = SELECT_ALL_TRAIN + " where id = ?";

    public Optional<Train> findById(UUID id) {
        try (var connection = MyConnectionPool.getConnectionFromPool();
             var preparedStatement = connection.prepareStatement(SELECT_TRAIN_BY_ID)) {
            preparedStatement.setObject(1, id);
            var result = preparedStatement.executeQuery();
            Train train = null;
            if (result.next()) {
                train = buildTrain(result);
            }
            return Optional.ofNullable(train);
        } catch (SQLException e) {
            throw new DaoException(e); // Мы же не хотим в сервисах обрабатывать SQLException
        }
    }

    public List<Train> findAll() {
        try (var connection = MyConnectionPool.getConnectionFromPool();
             var preparedStatement = connection.prepareStatement(SELECT_ALL_TRAIN)) {
            var result = preparedStatement.executeQuery();
            List<Train> trains = new ArrayList<>();
            while (result.next()) {
                var train = buildTrain(result);
                trains.add(train);
            }
            return trains;
        } catch (SQLException e) {
            throw new DaoException(e); // Мы же не хотим в сервисах обрабатывать SQLException
        }
    }

    /**
     * Просто заметка, для более детального понимания проблемы нужно вникнуть в проблему и ее решения
     * Проблема выбора нужного количества строк используя offset в том что база данных прежде чем найти нужное количество
     * строк начиная с требуемого смещения пройдет по всем строкам до начала смещения, например мы хотим 20 записей (limit)
     * сдвинутых на 1000 (offset) база данных последовательно пройдет по 1000 записей прочитает их пропустит и только потом возьмет
     * 20, индекс при этом использоваться не будет просто последовательное сканирование.
     * Как хак можно решить это убрав offset и использую where по id, где id = необходимому смещению, но тут надо разобраться будет ли это надежно!!!!
     */
    public List<Train> findAll(TrainFilter filter) {
        List<Object> parameters = new ArrayList<>();
        List<String> whereSql = new ArrayList<>();
        String limitOffset = " LIMIT ? OFFSET ?";
        if (filter.name() != null) {
            whereSql.add("name LIKE ?");
            parameters.add("%" + filter.name() + "%");
        }
        parameters.add(filter.limit());
        parameters.add(filter.offset());
        String where = limitOffset;
        if (!whereSql.isEmpty()) {
            where = whereSql.stream().collect(joining(" AND ", " WHERE ", limitOffset));
        }
        var sql = SELECT_ALL_TRAIN + where;
        var trains = new ArrayList<Train>();

        try (var connection = MyConnectionPool.getConnectionFromPool();
             var preparedStatement = connection.prepareStatement(sql)) {
            for (int i = 0; i < parameters.size(); i++) {
                preparedStatement.setObject(i + 1, parameters.get(i)); // Удобно добавлять параметры фильтра, даже если добавятся новые параметры
            }
            System.out.println(preparedStatement);
            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                trains.add(buildTrain(resultSet));
            }
            return trains;
        } catch (SQLException e) {
            throw new DaoException(e); // Мы же не хотим в сервисах обрабатывать SQLException
        }

    }

    public Train create(Train train) {
        try (var connection = MyConnectionPool.getConnectionFromPool();
             var preparedStatement = connection.prepareStatement(INSERT_TRAIN, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, train.getName());
            preparedStatement.setObject(2, train.getMachinistId());
            preparedStatement.setBytes(3, train.getImage());
            preparedStatement.executeUpdate();
            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                train.setId((UUID) generatedKeys.getObject("id"));
            }
            return train;
        } catch (SQLException e) {
            throw new DaoException(e); // Мы же не хотим в сервисах обрабатывать SQLException
        }

    }

    /**
     * Можно перехватить исключение через @SneakyThrows, но там выкинется throw Lombok.sneakyThrow(t);
     */
    @SneakyThrows(SQLException.class) // Мы же не хотим в сервисах обрабатывать SQLException
    public void update(Train train) {
        try (var connection = MyConnectionPool.getConnectionFromPool();
             var preparedStatement = connection.prepareStatement(UPDATE_TRAIN)) {
            preparedStatement.setString(1, train.getName());
            preparedStatement.setObject(2, train.getMachinistId());
            preparedStatement.setBytes(3, train.getImage());
            preparedStatement.setObject(4, train.getId());
            preparedStatement.executeUpdate();
        }
    }

    public boolean delete(UUID id) {
        try (var connection = MyConnectionPool.getConnectionFromPool();
             var preparedStatement = connection.prepareStatement(DELETE_TRAIN)) {
            preparedStatement.setObject(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e); // Мы же не хотим в сервисах обрабатывать SQLException
        }
    }

    private Train buildTrain(ResultSet resultSet) throws SQLException {
        return Train.builder().id((UUID) resultSet.getObject("id"))
                .name(resultSet.getString("name"))
                .machinistId((UUID) resultSet.getObject("machinist_id"))
                .image(resultSet.getBytes("image")).build();
    }

}
