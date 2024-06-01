create database calendar;

\c calendar;

create table users
(
    login text not null,
    primary key (login)
);

create table notes
(
    id         text   not null,
    text       text   not null,
    user_login text   not null references users (login),
    date       bigint not null,
    primary key (id)
);



create table auth
(
    user_login      text not null references users (login),
    hashed_password text not null
);