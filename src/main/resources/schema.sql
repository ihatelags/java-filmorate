DROP TABLE IF EXISTS films_genres CASCADE;
DROP TABLE IF EXISTS friendships CASCADE;
DROP TABLE IF EXISTS films_likes CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS films CASCADE;
DROP TABLE IF EXISTS genres CASCADE;
DROP TABLE IF EXISTS ratings CASCADE;

CREATE TABLE IF NOT EXISTS GENRES
(
    GENRE_ID BIGINT auto_increment,
    GENRE_NAME CHARACTER VARYING(64),
    constraint PK_GENRE_ID
        primary key (GENRE_ID)
);

CREATE TABLE IF NOT EXISTS RATINGS
(
    RATING_ID BIGINT auto_increment,
    RATING_NAME CHARACTER VARYING(10) not null
        constraint UC_RATING_NAME
            unique,
    constraint PK_RATING_ID
        primary key (RATING_ID)
);

CREATE TABLE IF NOT EXISTS FILMS
(
    FILM_ID BIGINT auto_increment,
    FILM_NAME CHARACTER VARYING(64) not null,
    DESCRIPTION CHARACTER VARYING(200) not null,
    RELEASE_DATE DATE,
    DURATION INTEGER,
    RATE INTEGER,
    RATING_ID BIGINT not null,
    constraint PK_FILM_ID
        primary key (FILM_ID),
    constraint FK_FILM_RATING_ID
        foreign key (RATING_ID) references RATINGS
);

CREATE TABLE IF NOT EXISTS FILMS_GENRE
(
    FILM_ID BIGINT REFERENCES films(film_id),
    GENRE_ID BIGINT REFERENCES genres(genre_id)
);

CREATE TABLE IF NOT EXISTS USERS
(
    USER_ID BIGINT auto_increment,
    EMAIL CHARACTER VARYING(64) not null
        constraint UC_USER_EMAIL
            unique,
    LOGIN CHARACTER VARYING(64) not null
        constraint UC_USER_LOGIN
            unique,
    USER_NAME CHARACTER VARYING(64),
    BIRTHDAY DATE,
    constraint "pk_ user_id"
        primary key (USER_ID)
);

CREATE TABLE IF NOT EXISTS FILMS_LIKES
(
    FILM_ID BIGINT not null,
    USER_ID BIGINT not null,
    constraint PK_FILMS_LIKES
        primary key (FILM_ID, USER_ID),
    constraint FK_FILMS_LIKES_FILM_ID
        foreign key (FILM_ID) references FILMS,
    constraint FK_FILMS_LIKES_USER_ID
        foreign key (USER_ID) references USERS
);

CREATE TABLE IF NOT EXISTS FRIENDSHIPS
(
    USER_ID BIGINT not null,
    FRIEND_ID BIGINT not null,
    constraint PK_FRIENDSHIP
        primary key (USER_ID, FRIEND_ID),
    constraint FK_FRIENDSHIP_FRIEND_ID
        foreign key (FRIEND_ID) references USERS,
    constraint FK_FRIENDSHIP_USER_ID
        foreign key (USER_ID) references USERS
);

