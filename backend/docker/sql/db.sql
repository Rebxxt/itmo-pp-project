create database calendar;
\c calendar;

create table notes (
    id serial not null,
    text text not null,
    user_id bigint not null,
    `date` bigint not null,
    primary key (id)
);

create table users (
    id serial not null,
    `name` text not null,
    primary key (id)
)