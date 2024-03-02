package ru.venidiktov.jdbc.starter.dao.example;

import java.io.IOException;
import java.sql.SQLException;
import ru.venidiktov.jdbc.starter.dao.example.dao.TrainDao;
import ru.venidiktov.jdbc.starter.dao.example.dao.dto.TrainFilter;
import ru.venidiktov.jdbc.starter.dao.example.entity.Train;

/**
 * Применяем паттер DAO для связи реляционной модели базы данных и объектной модели java приложения
 */
public class DaoRunner {
    public static void main(String[] args) throws SQLException, IOException {
        var trainDao = TrainDao.INSTANCE;

        var newTrain = Train.builder().name("Tomas").build();
        System.out.println("Поезд для создания - %s".formatted(newTrain));

        var createdTrain = trainDao.create(newTrain);
        System.out.println("Поезд создан - %s".formatted(createdTrain));

        createdTrain.setName("Mario");
        trainDao.update(createdTrain);
        System.out.println("Обновлено имя поезда - %s".formatted(createdTrain));

        var updatedTrain = trainDao.findById(createdTrain.getId());
        System.out.println("Обновленный поезд из базы данных - %s".formatted(updatedTrain));

        var trains = trainDao.findAll();
        System.out.println("Все поезда из базы");
        trains.stream().forEach(System.out::println);

        var filter = new TrainFilter(3, 0, "hogwarts");
        var trainsAfterFiltering = trainDao.findAll(filter);
        System.out.println("Поезда после поиска по фильтру - %s".formatted(filter));
        trainsAfterFiltering.stream().forEach(System.out::println);
    }
}
