--Пока скрипт вызывается только руками
--changeset Veniditkov G.A:create-table-train
CREATE TABLE train (
    id UUID PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    name VARCHAR NOT NULL
);