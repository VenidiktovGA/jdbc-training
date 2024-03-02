package ru.venidiktov.jdbc.starter.dao.example.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import ru.venidiktov.jdbc.starter.dao.example.entity.Machinist;
import ru.venidiktov.jdbc.starter.dao.example.exception.DaoException;
import ru.venidiktov.jdbc.starter.util.MyConnectionPool;

public enum MachinistDao implements Dao<UUID, Machinist> {
    INSTANCE;

    private final String INSERT_MACHINIST = "INSERT INTO machinist (name) values(?)";
    private final String SELECT_MACHINIST_BY_ID = "SELECT id, name FROM machinist where id = ?";

    @Override
    public Optional<Machinist> findById(UUID id) {
        try (var connection = MyConnectionPool.getConnectionFromPool();
             var preparedStatement = connection.prepareStatement(SELECT_MACHINIST_BY_ID)) {
            preparedStatement.setObject(1, id);
            var result = preparedStatement.executeQuery();
            Machinist machinist = null;
            if (result.next()) {
                machinist = buildMachinist(result);
            }
            return Optional.ofNullable(machinist);
        } catch (SQLException e) {
            throw new DaoException(e); // Мы же не хотим в сервисах обрабатывать SQLException
        }
    }

    /**
     * Метод это простое решение проблемы когда нужно получить дочернюю сущность и для этого в
     * родительской сущности вызывается Dao класс дочерней сущности с методом получения дочерней,
     * и там заново открывается новое соединение.
     * Тут соединение передается
     */
    public Optional<Machinist> findById(UUID id, Connection connection) {
        try (var preparedStatement = connection.prepareStatement(SELECT_MACHINIST_BY_ID)) {
            preparedStatement.setObject(1, id);
            var result = preparedStatement.executeQuery();
            Machinist machinist = null;
            if (result.next()) {
                machinist = buildMachinist(result);
            }
            return Optional.ofNullable(machinist);
        } catch (SQLException e) {
            throw new DaoException(e); // Мы же не хотим в сервисах обрабатывать SQLException
        }
    }

    @Override
    public List<Machinist> findAll() {
        return null;
    }

    @Override
    public Machinist create(Machinist machinist) {
        try (var connection = MyConnectionPool.getConnectionFromPool();
             var preparedStatement = connection.prepareStatement(INSERT_MACHINIST, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, machinist.getName());
            preparedStatement.executeUpdate();
            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                machinist.setId((UUID) generatedKeys.getObject("id"));
            }
            return machinist;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void update(Machinist entity) {

    }

    @Override
    public boolean delete(UUID id) {
        return false;
    }

    private Machinist buildMachinist(ResultSet resultSet) throws SQLException {
        return Machinist.builder().id((UUID) resultSet.getObject("id"))
                .name(resultSet.getString("name")).build();
    }
}
