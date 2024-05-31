create database calendar;

\c calendar;

create table notes (
    id text not null,
    text text not null,
    user_login text not null,
    date bigint not null,
    primary key (id)
);

create table users (
    login text not null,
    primary key (login)
);

create table auth (
    user_login text not null,
    hashed_password text not null
);