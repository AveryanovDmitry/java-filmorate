CREATE TABLE IF NOT EXISTS GENRE (
 GENRE_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
 NAME VARCHAR(40) NOT NULL,
 CONSTRAINT GENRE_PK PRIMARY KEY (GENRE_ID)
);

CREATE TABLE IF NOT EXISTS RATING (
    RATING_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
 NAME VARCHAR(40) NOT NULL,
 CONSTRAINT RATING_PK PRIMARY KEY (RATING_ID)
);

CREATE TABLE IF NOT EXISTS FILM (
 FILM_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
 NAME VARCHAR(200) NOT NULL,
 DESCRIPTION VARCHAR,
 RELEASE_DATE DATE NOT NULL,
 DURATION INTEGER,
 RATING_ID INTEGER NOT NULL,
 CONSTRAINT FILM_PK PRIMARY KEY (FILM_ID),
 CONSTRAINT FILM_FK FOREIGN KEY (RATING_ID) REFERENCES RATING(RATING_ID) ON DELETE RESTRICT ON UPDATE RESTRICT
);
CREATE INDEX IF NOT EXISTS FILM_FK_INDEX_2 ON FILM (RATING_ID);

CREATE TABLE IF NOT EXISTS FILM_GENRE (
 FILM_GENRE_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
 FILM_ID INTEGER NOT NULL,
 GENRE_ID INTEGER NOT NULL,
 CONSTRAINT FILM_GENRE_PK PRIMARY KEY (FILM_GENRE_ID),
 CONSTRAINT FILM_GENRE_FK FOREIGN KEY (GENRE_ID) REFERENCES GENRE(GENRE_ID) ON DELETE CASCADE ON UPDATE CASCADE,
 CONSTRAINT FILM_GENRE_FK_1 FOREIGN KEY (FILM_ID) REFERENCES FILM(FILM_ID) ON DELETE RESTRICT ON UPDATE RESTRICT
);
CREATE INDEX IF NOT EXISTS FILM_GENRE_FK_1_INDEX_7 ON FILM_GENRE (FILM_ID);
CREATE INDEX IF NOT EXISTS FILM_GENRE_FK_INDEX_7 ON FILM_GENRE (GENRE_ID);

CREATE TABLE IF NOT EXISTS "USER" (
 USER_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
 EMAIL VARCHAR(256) NOT NULL,
 LOGIN VARCHAR(40) NOT NULL,
 NAME VARCHAR(100) NOT NULL,
 BIRTHDAY DATE NOT NULL,
 CONSTRAINT USER_PK PRIMARY KEY (USER_ID)
);

CREATE TABLE IF NOT EXISTS FRIENDSHIP (
 FRIENDSHIP_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
 FOLLOWING_USER_ID INTEGER NOT NULL,
 FOLLOWED_USER_ID INTEGER NOT NULL,
 ACCEPTED BOOLEAN DEFAULT FALSE,
 CONSTRAINT FRIENDSHIP_PK PRIMARY KEY (FRIENDSHIP_ID),
 CONSTRAINT FRIENDSHIP_FK FOREIGN KEY (FOLLOWING_USER_ID) REFERENCES "USER"(USER_ID) ON DELETE CASCADE ON UPDATE CASCADE,
 CONSTRAINT FRIENDSHIP_FK_1 FOREIGN KEY (FOLLOWED_USER_ID) REFERENCES "USER"(USER_ID) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE INDEX IF NOT EXISTS FRIENDSHIP_FK_1_INDEX_4 ON FRIENDSHIP (FOLLOWED_USER_ID);
CREATE INDEX IF NOT EXISTS FRIENDSHIP_FK_INDEX_4 ON FRIENDSHIP (FOLLOWING_USER_ID);

CREATE TABLE IF NOT EXISTS "LIKES" (
 LIKE_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
 USER_ID INTEGER NOT NULL,
 FILM_ID INTEGER NOT NULL,
 CONSTRAINT LIKES_PK PRIMARY KEY (LIKE_ID),
 CONSTRAINT LIKES_FK FOREIGN KEY (USER_ID) REFERENCES "USER"(USER_ID) ON DELETE RESTRICT ON UPDATE RESTRICT,
 CONSTRAINT LIKES_FK_1 FOREIGN KEY (FILM_ID) REFERENCES FILM(FILM_ID) ON DELETE RESTRICT ON UPDATE RESTRICT
);
CREATE INDEX IF NOT EXISTS LIKES_FK_1_INDEX_4 ON "LIKES" (FILM_ID);
CREATE INDEX IF NOT EXISTS LIKES_FK_INDEX_4 ON "LIKES" (USER_ID);

CREATE TABLE IF NOT EXISTS "REVIEWS"
(
    REVIEW_ID   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    CONTENT     VARCHAR(256) NOT NULL,
    IS_POSITIVE BOOLEAN      NOT NULL,
    USER_ID     INTEGER REFERENCES FILM (FILM_ID) ON DELETE RESTRICT ON UPDATE RESTRICT,
    FILM_ID     INTEGER REFERENCES USER (USER_ID) ON DELETE RESTRICT ON UPDATE RESTRICT,
    USEFUL      INTEGER      NOT NULL
);