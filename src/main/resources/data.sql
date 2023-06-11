insert into GENRE (NAME)
select *
from VALUES ('Комедия'), ('Драма'), ('Мультфильм'), ('Триллер'), ('Документальный'), ('Боевик')
WHERE C1 NOT IN (select NAME from GENRE);

insert into RATING (NAME)
select *
from VALUES ('G'),
  ('PG'),
  ('PG-13'),
  ('R'),
  ('NC-17')
WHERE C1 NOT IN (select NAME from RATING);