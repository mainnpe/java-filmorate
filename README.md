## Filmorate App
___
#### Учебный проект на базе Spring Boot по созданию приложения, которое позволяет получить информацию о фильмах, оставить отзыв и оценку, получить персональные рекомендации.

Схема БД в виде ER диаграммы представлена [здесь](https://github.com/mainnpe/java-filmorate/blob/main/src/main/resources/ER_diagram.png).

#### Основная информация о таблицах.
1. **users** - информация о пользователях
2. **friends** - информация о друзьях пользователя. Поле status: true - если дружба между пользователями подтверждена, false - не подтверждена.
3. **films** - информация о фильмах
4. **film_genres** - содержит перечень всех жанров кино
5. **mpa_age_ratings** - содержит перечень возрастных рейтингов Ассоциации кинокомпаний (Motion Picture Association, сокращённо МРА)
6. **film_genre_rel** - соотнесение фильма с жанрами.
7. **film_likes** - перечень лайков, поставленных пользователями фильму.

#### Примеры основных SQL запросов для выгрузки данных о фильмах и пользователях.


* Все пользователи:

```sql
SELECT *
FROM users;
```
* Пользователь с определенным *{id}*:

```sql
SELECT *
FROM users
WHERE id = ?;
```

* Все друзья пользователя с определенным *{id}*:

```sql
SELECT *
FROM users
WHERE id IN (SELECT friend_id
             FROM friends
             WHERE user_id = ?);
```
			 
* Список общих друзей пользователей с *{id1}* и *{id2}*:

```sql
SELECT *
FROM users
WHERE id IN (SELECT DISTINCT friend_id
             FROM friends
             WHERE user_id IN (?,?) --id1,id2
                  AND friend_id NOT IN (?,?) --id1,id2
            );
```

			 
* Все фильмы:

```sql
SELECT f.*, mar.rating_name
FROM films f
JOIN mpa_age_ratings mar
	ON f.mpa_rating_id = mar.rating_id;
```
	
* Фильм с определенным *{id}*:

```sql
SELECT f.*, mar.rating_name
FROM films f
JOIN mpa_age_ratings mar
  ON f.mpa_rating_id = mar.rating_id
WHERE f.id = ?;
```

* Список жанров фильма с определенным *{id}*:

```sql
SELECT fg.genre_name
FROM film_genre_rel fgr
JOIN film_genres fg
	ON fgr.genre_id = fg.genre_id
WHERE fgr.film_id = ?;
```
	
* Список *{N}* самых популярных фильмов:

```sql
SELECT f.*, mar.rating_name
FROM films f
JOIN mpa_age_ratings mar
  ON f.mpa_rating_id = mar.rating_id
ORDER BY rate DESC
LIMIT ?;
```
