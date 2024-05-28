create database calendar;

\c calendar;

create table notes (
    id text not null,
    text text not null,
    user_id text not null,
    date bigint not null,
    primary key (id)
);

create table users (
    id text not null,
    name text not null,
    primary key (id)
);

create table auth (
    id text not null,
    hashed_password text not null
);