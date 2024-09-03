-- +goose Up
-- +goose StatementBegin
create table users (
  email VARCHAR,
  username VARCHAR not null primary key,
  password_hash VARCHAR not null
);
create table notes (
  id uuid default gen_random_uuid() primary key,
  username VARCHAR not null,
  note VARCHAR not null CHECK ( length(note) > 5 ),
  date bigint default (EXTRACT(epoch FROM now()) * (1000)::numeric) not null,
  FOREIGN KEY (username) REFERENCES users(username)
);
-- +goose StatementEnd

-- +goose Down
-- +goose StatementBegin
drop table notes CASCADE;
drop table users CASCADE;
-- +goose StatementEnd
