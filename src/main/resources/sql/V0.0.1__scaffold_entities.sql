CREATE TABLE poll(
    id          UUID                        PRIMARY KEY,
    name        VARCHAR(255)                NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE    NOT NULL
);

CREATE TABLE voter(
    code            UUID            PRIMARY KEY,
    poll_id         UUID            NOT NULL,
    email_address   VARCHAR(254)    NOT NULL,

    FOREIGN KEY (poll_id) REFERENCES poll(id) ON DELETE CASCADE
);

CREATE TABLE question(
    poll_id     UUID            NOT NULL,
    number      INTEGER         NOT NULL,
    text        VARCHAR         NOT NULL,
    type        VARCHAR         NOT NULL,
    picks       VARCHAR ARRAY   NOT NULL DEFAULT '{}',
    minimum     INTEGER         NULL,
    maximum     INTEGER         NULL,

    PRIMARY KEY (poll_id, number),
    FOREIGN KEY (poll_id) REFERENCES poll(id) ON DELETE CASCADE
);

CREATE TABLE answer(
    poll_id             UUID            NOT NULL,
    question_number     INTEGER         NOT NULL,
    email_address       VARCHAR(254)    NOT NULL,
    answers             VARCHAR ARRAY   NOT NULL DEFAULT '{}',
    number              INTEGER         NULL,

    PRIMARY KEY (poll_id, question_number, email_address),
    FOREIGN KEY (poll_id) REFERENCES poll(id) ON DELETE CASCADE
);