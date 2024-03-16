CREATE TABLE player(
    id int not null primary key,
    username character varying(64) not null,
    password character varying(255) not null
);

INSERT INTO player (id, username, password) values (1, 'daniel', '12345');