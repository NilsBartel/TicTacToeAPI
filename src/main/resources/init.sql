create table if not exists public.users
(
    user_id serial
            primary key,
    username varchar(255),
    password varchar(255),
    answer1  varchar(255),
    answer2  varchar(255)
);

alter table public.users
    owner to postgres;

create table if not exists public.score
(
    score_id serial
        primary key,
    player   integer,
    computer integer,
    draw     integer,
    user_id  integer not null
        references public.users
);

alter table public.score
    owner to postgres;

create table if not exists public.match
(
    match_id     serial
        primary key,
    difficulty   varchar(255),
    status       varchar(255),
    isplayerturn boolean,
    user_id      integer not null
        references public.users
);

alter table public.match
    owner to postgres;

create table if not exists public.board
(
    board_id serial
        primary key,
    match_id integer not null
        references public.match
);

alter table public.board
    owner to postgres;

create table if not exists public.row
(
    row_id   serial
        primary key,
    row      integer,
    board_id integer not null
        references public.board
);

alter table public.row
    owner to postgres;

create table if not exists public.field
(
    field_id serial
        primary key,
    field    integer,
    symbol   char,
    row_id   integer not null
        references public.row
);

alter table public.field
    owner to postgres;

create table if not exists public.time
(
    time_id   serial
        primary key,
    starttime timestamp with time zone,
    endtime   timestamp with time zone,
    match_id  integer not null
        references public.match
);

alter table public.time
    owner to postgres;

