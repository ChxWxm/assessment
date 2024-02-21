DROP TABLE IF EXISTS lottery CASCADE;
DROP TABLE IF EXISTS user_account CASCADE;
DROP TABLE IF EXISTS user_ticket CASCADE;

CREATE TABLE lottery (
    id SERIAL PRIMARY KEY,
    ticket VARCHAR(6) NOT NULL,
    price INTEGER NOT NULL,
    amount INTEGER
);

CREATE TABLE user_account (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    roles VARCHAR[],
    cost INTEGER
);

CREATE TABLE user_ticket (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES user_account(id) ON DELETE CASCADE,
    lottery_id INTEGER NOT NULL REFERENCES lottery(id) ON DELETE CASCADE
);


-- Initial data
INSERT INTO lottery (ticket, price, amount) VALUES
    ('000001', 80, 1),
    ('999932', 130, 2);

INSERT INTO user_account (username, password, roles, cost) VALUES
    ('admin', '$2a$12$3ICN3cFfNOxzbiuzu7SNQ.j2irLbJj73MaCnWZEfU3Kkn88gSSWry', '{"ADMIN"}', 0),
    ('user', '$2a$10$eFRbnpUP0f0trzo9hjnPse8VQk0wUbxTRLnZ/kA3saK6tB.LcDOp2', '{"USER"}', 0);