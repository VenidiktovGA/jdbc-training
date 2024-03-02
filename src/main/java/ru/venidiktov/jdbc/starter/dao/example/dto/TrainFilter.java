package ru.venidiktov.jdbc.starter.dao.example.dto;

/**
 * Данная DTO представляет из себя данные для примитивной пагинации
 */
public record TrainFilter(
        int limit,
        int offset,
        String name
) {
}
