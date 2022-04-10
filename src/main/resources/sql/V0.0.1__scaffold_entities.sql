CREATE TABLE poll(
    id          UUID                        PRIMARY KEY,
    name        VARCHAR(255)                NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE    NOT NULL
);

CREATE TABLE voter(
    poll_id     UUID            NOT NULL,
    code        UUID            PRIMARY KEY,
    email       VARCHAR(254)    NOT NULL,

    FOREIGN KEY (poll_id) REFERENCES poll(id) ON DELETE CASCADE
);

CREATE TABLE question(
    id          UUID            PRIMARY KEY,
    poll_id     UUID            NOT NULL,
    text        VARCHAR         NOT NULL,
    type        VARCHAR         NOT NULL,
    picks       VARCHAR ARRAY   NULL,
    minimum     INTEGER         NULL,
    maximum     INTEGER         NULL,

    FOREIGN KEY (poll_id) REFERENCES poll(id) ON DELETE CASCADE
);

CREATE TABLE answer(
    poll_id         UUID            NOT NULL,
    question_id     UUID            NOT NULL,
    email           VARCHAR(254)    NOT NULL,
    answers         VARCHAR ARRAY   NULL,
    number          INTEGER         NULL,

    FOREIGN KEY (poll_id) REFERENCES poll(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES question(id)
);