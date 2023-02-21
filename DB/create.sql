CREATE OR REPLACE TABLE tags
(
    id   BIGINT AUTO_INCREMENT
        PRIMARY KEY,
    name VARCHAR(16) NOT NULL,
    CONSTRAINT tags_pk
        UNIQUE (name)
);

CREATE OR REPLACE TABLE users
(
    id         BIGINT AUTO_INCREMENT
        PRIMARY KEY,
    first_name VARCHAR(32) NOT NULL,
    last_name  VARCHAR(32) NOT NULL,
    email      VARCHAR(50) NOT NULL,
    username   VARCHAR(50) NOT NULL,
    password   VARCHAR(50) NOT NULL,
    join_date  datetime    NOT NULL,
    CONSTRAINT users_pk2
        UNIQUE (email)
);

CREATE OR REPLACE TABLE permissions
(
    user_id    BIGINT               NOT NULL
        PRIMARY KEY,
    is_blocked tinyint(1) DEFAULT 0 NOT NULL,
    is_admin   tinyint(1) DEFAULT 0 NOT NULL,
    CONSTRAINT features_users_fk
        FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE OR REPLACE TABLE phones
(
    user_id      BIGINT      NOT NULL
        PRIMARY KEY,
    phone_number VARCHAR(16) NULL,
    CONSTRAINT phones_users_fk
        FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE OR REPLACE TABLE photos
(
    user_id BIGINT NOT NULL
        PRIMARY KEY,
    photo   BLOB   NULL,
    CONSTRAINT photos_users_fk
        FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE OR REPLACE TABLE posts
(
    id           BIGINT AUTO_INCREMENT
        PRIMARY KEY,
    title        VARCHAR(100) NOT NULL,
    content      text         NULL,
    likes        INT          NULL,
    user_id      BIGINT       NOT NULL,
    date_created datetime     NOT NULL,
    CONSTRAINT posts_users_fk
        FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE OR REPLACE TABLE comments
(
    id           BIGINT AUTO_INCREMENT
        PRIMARY KEY,
    post_id      BIGINT   NOT NULL,
    user_id      BIGINT   NOT NULL,
    content      text     NULL,
    date_created datetime NOT NULL,
    CONSTRAINT comments_posts_fk
        FOREIGN KEY (post_id) REFERENCES posts (id),
    CONSTRAINT comments_users_fk
        FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE OR REPLACE TABLE posts_tags
(
    post_id BIGINT NOT NULL,
    tag_id  BIGINT NOT NULL,
    PRIMARY KEY (post_id, tag_id),
    CONSTRAINT posts_tags_posts_fk
        FOREIGN KEY (post_id) REFERENCES posts (id),
    CONSTRAINT posts_tags_tags_fk
        FOREIGN KEY (tag_id) REFERENCES tags (id)
);