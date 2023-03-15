create or replace table tags
(
    id   bigint auto_increment
        primary key,
    name varchar(32) not null,
    constraint tags_pk
        unique (name)
);

create or replace table users
(
    id         bigint auto_increment
        primary key,
    first_name varchar(32)                          not null,
    last_name  varchar(32)                          not null,
    email      varchar(50)                          not null,
    username   varchar(50)                          not null,
    password   varchar(50)                          not null,
    join_date  datetime default current_timestamp() not null,
    constraint users_pk
        unique (username),
    constraint users_pk2
        unique (email)
);

create or replace table permissions
(
    user_id    bigint               not null
        primary key,
    is_deleted tinyint(1) default 0 not null,
    is_blocked tinyint(1) default 0 not null,
    is_admin   tinyint(1) default 0 not null,
    constraint permissions_users_fk
        foreign key (user_id) references users (id)
);

create or replace table phones
(
    user_id      bigint      not null
        primary key,
    phone_number varchar(16) null,
    constraint phones_users_fk
        foreign key (user_id) references users (id)
);

create or replace table photos
(
    user_id bigint not null
        primary key,
    photo   blob   null,
    constraint photos_users_fk
        foreign key (user_id) references users (id)
);

create or replace table posts
(
    id           bigint auto_increment
        primary key,
    title        varchar(100)                         not null,
    content      text                                 null,
    user_id      bigint                               not null,
    date_created datetime default current_timestamp() not null,
    constraint posts_users_fk
        foreign key (user_id) references users (id)
);

create or replace table comments
(
    id           bigint auto_increment
        primary key,
    post_id      bigint                               not null,
    user_id      bigint                               not null,
    content      text                                 null,
    date_created datetime default current_timestamp() not null,
    constraint comments_posts_fk
        foreign key (post_id) references posts (id),
    constraint comments_users_fk
        foreign key (user_id) references users (id)
);

create or replace table likes
(
    user_id bigint not null,
    post_id bigint not null,
    primary key (user_id, post_id),
    constraint likes_posts_fk
        foreign key (post_id) references posts (id),
    constraint likes_users_fk
        foreign key (user_id) references users (id)
);

create or replace table posts_tags
(
    post_id bigint not null,
    tag_id  bigint not null,
    primary key (post_id, tag_id),
    constraint posts_tags_posts_fk
        foreign key (post_id) references posts (id),
    constraint posts_tags_tags_fk
        foreign key (tag_id) references tags (id)
);

