--Пока скрипт вызывается только руками
--changeset Veniditkov G.A:init-database

CREATE TABLE machinist(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR NOT NULL
);

CREATE TABLE train (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR NOT NULL,
    machinist_id UUID references machinist(id)
);

INSERT INTO machinist (name) values ('Rubeus Hagrid');
INSERT INTO train (name, machinist_id) values ('hogwarts express', SELECT id FROM machinist where name = 'Rubeus Hagrid');