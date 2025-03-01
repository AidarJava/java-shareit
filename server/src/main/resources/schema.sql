CREATE TABLE IF NOT EXISTS users (
id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
name VARCHAR(255) NOT NULL,
email VARCHAR(512) NOT NULL UNIQUE
);
CREATE TABLE IF NOT EXISTS items (
id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
owner BIGINT CHECK (owner > 0),
url VARCHAR(255),
name VARCHAR(255) NOT NULL,
description VARCHAR(255) NOT NULL,
available BOOLEAN,
request_id BIGINT
);
CREATE TABLE IF NOT EXISTS bookings (
id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
start_date TIMESTAMP,
end_date TIMESTAMP,
item_id BIGINT NOT NULL REFERENCES items(id) ON DELETE CASCADE,
booker_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
status VARCHAR(50)
);
CREATE TABLE IF NOT EXISTS comments (
id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
text VARCHAR(512) NOT NULL,
item_id BIGINT NOT NULL REFERENCES items(id) ON DELETE CASCADE,
author_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
created DATE
);
CREATE TABLE IF NOT EXISTS requests (
id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
owner BIGINT NOT NULL,
description VARCHAR(512) NOT NULL,
created DATE
);