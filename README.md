## Filmorate App
___
#### Учебный проект на базе Spring Boot по созданию приложения, которое позволяет получить информацию о фильмах, оставить отзыв и оценку, получить персональные рекомендации.

Схема БД в виде ER диаграммы представлена [здесь](https://github.com/mainnpe/java-filmorate/blob/main/src/main/resources/ER_diagram.png).

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
WHERE id IN (SELECT friend_id
             FROM friends
             WHERE user_id = ? --id1
                  AND friend_id <> ? --id2
             UNION 
             SELECT friend_id
             FROM friends
             WHERE user_id = ? --id2
                  AND friend_id <> ? --id1
            );
```

			 
* Все фильмы:

```sql
SELECT f.*, fr.rating_name
FROM films f
JOIN film_ratings fr
	ON f.rating_id = fr.rating_id;
```
	
* Фильм с определенным *{id}*:

```sql
SELECT f.*, fr.rating_name
FROM films f
JOIN film_ratings fr
	ON f.rating_id = fr.rating_id
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
SELECT f.*, fr.rating_name
FROM films f
JOIN film_ratings fr
	ON f.rating_id = fr.rating_id
ORDER BY likes_amount DESC
LIMIT ?;
```
