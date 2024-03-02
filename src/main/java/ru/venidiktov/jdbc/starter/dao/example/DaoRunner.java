package ru.venidiktov.jdbc.starter.dao.example;

import java.io.IOException;
import java.sql.SQLException;
import ru.venidiktov.jdbc.starter.dao.example.dao.MachinistDao;
import ru.venidiktov.jdbc.starter.dao.example.dao.TrainDao;
import ru.venidiktov.jdbc.starter.dao.example.dto.TrainFilter;
import ru.venidiktov.jdbc.starter.dao.example.entity.Machinist;
import ru.venidiktov.jdbc.starter.dao.example.entity.Train;

/**
 * Применяем паттер DAO для связи реляционной модели базы данных и объектной модели java приложения
 */
public class DaoRunner {
    public static void main(String[] args) throws SQLException, IOException {
        var trainDao = TrainDao.INSTANCE;
        var machinistDao = MachinistDao.INSTANCE;

        var machinist = Machinist.builder().name("Копатычь").build();
        System.out.println("Машинист для создания - %s".formatted(machinist));
        var createdMachinist = machinistDao.create(machinist);
        System.out.println("Машинист создан - %s".formatted(createdMachinist));

        var newTrain = Train.builder().name("Tomas").machinist(createdMachinist).build();
        System.out.println("Поезд для создания - %s".formatted(newTrain));

        var createdTrain = trainDao.create(newTrain);
        System.out.println("Поезд создан - %s".formatted(createdTrain));

        System.out.println("-------------------[][][]-------------------");

        createdTrain.setName("Mario");
        trainDao.update(createdTrain);
        System.out.println("Обновлено имя поезда - %s".formatted(createdTrain));

        var updatedTrain = trainDao.findById(createdTrain.getId());
        System.out.println("Обновленный поезд из базы данных - %s".formatted(updatedTrain));

        System.out.println("-------------------[][][]-------------------");

        var trains = trainDao.findAll();
        System.out.println("Все поезда из базы");
        trains.stream().forEach(System.out::println);

        System.out.println("-------------------[][][]-------------------");

        var filter = new TrainFilter(3, 0, "hogwarts");
        var trainsAfterFiltering = trainDao.findAll(filter);
        System.out.println("Поезда после поиска по фильтру - %s".formatted(filter));
        trainsAfterFiltering.stream().forEach(System.out::println);
    }
}
