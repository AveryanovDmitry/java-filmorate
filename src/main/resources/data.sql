DELETE
FROM GENRE_FILM;
DELETE
FROM LIKES;
DELETE
FROM GENRE;
DELETE
FROM FRIENDS;
DELETE
FROM USERS;
DELETE
FROM FILMS;

ALTER TABLE USERS
    ALTER COLUMN id RESTART WITH 1;
ALTER TABLE FILMS
    ALTER COLUMN id RESTART WITH 1;

MERGE INTO MPA (mpa_id, mpa_name)
    VALUES (1, 'G'),
    (2, 'PG'),
    (3, 'PG-13'),
    (4, 'R'),
    (5, 'NC-17');


MERGE INTO GENRE (genre_id, name)
    VALUES (1, 'Комедия'),
    (2, 'Драма'),
    (3, 'Мультфильм'),
    (4, 'Триллер'),
    (5, 'Документальный'),
    (6, 'Боевик');