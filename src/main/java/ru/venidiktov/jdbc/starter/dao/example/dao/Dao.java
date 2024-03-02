package ru.venidiktov.jdbc.starter.dao.example.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<K, E> {
    Optional<E> findById(K id);

    List<E> findAll(); //TODO еще есть с фильтром!

    E create(E entity);

    void update(E entity);

    boolean delete(K id);
}
