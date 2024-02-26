--Пока скрипт вызывается только руками
--changeset Veniditkov G.A:init-database

CREATE TABLE machinist(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR NOT NULL
);

CREATE TABLE train (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR NOT NULL,
    machinist_id UUID references machinist(id),
    image bytea
);

INSERT INTO machinist (name) values ('Rubeus Hagrid');
INSERT INTO train (name, machinist_id) SELECT 'hogwarts express', id FROM machinist where name = 'Rubeus Hagrid';