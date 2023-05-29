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
    VALUES (1, 'G');
MERGE INTO MPA (mpa_id, mpa_name)
    VALUES (2, 'PG');
MERGE INTO MPA (mpa_id, mpa_name)
    VALUES (3, 'PG-13');
MERGE INTO MPA (mpa_id, mpa_name)
    VALUES (4, 'R');
MERGE INTO MPA (mpa_id, mpa_name)
    VALUES (5, 'NC-17');

MERGE INTO GENRE (genre_id, name)
    VALUES (1, 'Комедия');
MERGE INTO GENRE (genre_id, name)
    VALUES (2, 'Драма');
MERGE INTO GENRE (genre_id, name)
    VALUES (3, 'Мультфильм');
MERGE INTO GENRE (genre_id, name)
    VALUES (4, 'Триллер');
MERGE INTO GENRE (genre_id, name)
    VALUES (5, 'Документальный');
MERGE INTO GENRE (genre_id, name)
    VALUES (6, 'Боевик');