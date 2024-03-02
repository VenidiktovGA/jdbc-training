package ru.venidiktov.jdbc.starter.dao.example.entity;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity это класс который является проекцией на таблицу
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Train {

    /**
     * Для сравнение двух сущностей очень рекомендуется использовать только id, так как в реляционной модели
     * строки однозначно определяются по их id
     */
    private UUID id;

    private String name;

    private Machinist machinist; // При использовании ORM тут будет сама entity Machinist, мы сделаем просто id

    private byte[] image;

    @Override
    public String toString() {
        return "Train{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", machinist=" + machinist +
                ", image=" + (image == null ? "null" : "exist") +
                '}';
    }
}
