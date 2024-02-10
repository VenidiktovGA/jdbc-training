--Пока скрипт вызывается только руками
--changeset Veniditkov G.A:create-table-train
CREATE TABLE train (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR NOT NULL
);