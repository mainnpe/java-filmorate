-- DROP TABLES
DROP TABLE IF EXISTS users, films, friends, film_likes, film_genre_rel, mpa_age_ratings, film_genres, user_events, reviews, review_scores, director, director_rel;

-- create tables
CREATE TABLE IF NOT EXISTS users
(
    id INTEGER AUTO_INCREMENT(1) PRIMARY KEY,
    email VARCHAR(64) NOT NULL,
    login VARCHAR(64) NOT NULL,
    name VARCHAR(100) NOT NULL,
    birthday DATE
    CONSTRAINT invalid_email CHECK (email <> '' AND INSTR(email, '@') > 0),
    CONSTRAINT invalid_login CHECK (login <> '' AND INSTR(login, ' ') = 0),
    CONSTRAINT invalid_birthday CHECK (birthday < CURRENT_DATE)
);

CREATE TABLE IF NOT EXISTS mpa_age_ratings
(
    rating_id INTEGER PRIMARY KEY,
    rating_name VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS films
(
   id INTEGER AUTO_INCREMENT(1) PRIMARY KEY,
   name VARCHAR(64) NOT NULL,
   description VARCHAR(200),
   release_date DATE NOT NULL,
   duration INTEGER NOT NULL,
   rate INTEGER NOT NULL DEFAULT 0,
   mpa_rating_id INTEGER REFERENCES mpa_age_ratings,
    CONSTRAINT name_empty CHECK (name <> ''),
    CONSTRAINT duration_positive CHECK (duration > 0),
    CONSTRAINT release_date_constr CHECK (release_date >= DATE '1895-12-28')
);

CREATE TABLE IF NOT EXISTS friends
(
    user_id INTEGER REFERENCES users ON DELETE CASCADE,
    friend_id INTEGER REFERENCES users ON DELETE CASCADE,
    status BOOLEAN DEFAULT FALSE,
    CONSTRAINT friends_pk PRIMARY KEY (user_id, friend_id),
    CONSTRAINT self_friend CHECK (user_id <> friend_id)
);

CREATE TABLE IF NOT EXISTS film_likes
(
    film_id INTEGER REFERENCES films ON DELETE CASCADE,
    user_id INTEGER REFERENCES users ON DELETE CASCADE,
    CONSTRAINT flikes_pk PRIMARY KEY (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS film_genres
(
    genre_id INTEGER PRIMARY KEY,
    genre_name VARCHAR(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genre_rel
(
    film_id INTEGER REFERENCES films ON DELETE CASCADE,
    genre_id INTEGER REFERENCES film_genres,
    CONSTRAINT fgr_pk PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS user_events
(
    event_id INTEGER AUTO_INCREMENT PRIMARY KEY,
    entity_id INTEGER,
    user_id INTEGER,
    event_type VARCHAR(64),
    user_operation VARCHAR(64),
    cdate TIMESTAMP DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS uevents ON user_events (event_id, entity_id, user_id);
ALTER TABLE user_events ADD FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;

CREATE TABLE IF NOT EXISTS reviews
(
    id INTEGER AUTO_INCREMENT(1) PRIMARY KEY,
    film_id INTEGER REFERENCES films ON DELETE CASCADE,
    user_id INTEGER REFERENCES users ON DELETE CASCADE,
    is_positive BOOLEAN,
    content VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS review_scores
(
    review_id INTEGER REFERENCES reviews ON DELETE CASCADE,
    user_id INTEGER REFERENCES users ON DELETE CASCADE,
    score INTEGER,
    PRIMARY KEY (review_id, user_id, score)
);

CREATE TABLE IF NOT EXISTS director
(
    id INTEGER AUTO_INCREMENT(1) PRIMARY KEY,
    director_name VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS director_rel
(
    film_id INTEGER REFERENCES films ON DELETE CASCADE,
    id INTEGER REFERENCES director ON DELETE CASCADE,
    CONSTRAINT director_rel_pk PRIMARY KEY (film_id, id)
);

CREATE INDEX IF NOT EXISTS dir_name ON director (director_name);
CREATE INDEX IF NOT EXISTS film_title ON films (name);
