CREATE TABLE bus (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    plate VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(id)
);

CREATE TABLE route (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    distance DECIMAL(10, 2) NOT NULL,
    start_point VARCHAR(255) NOT NULL,
    end_point VARCHAR(255) NOT NULL
);

CREATE TABLE route_schedule (
    id VARCHAR(255) PRIMARY KEY,
    "timestamp" TIMESTAMP NOT NULL,
    route_id VARCHAR(255) NOT NULL,
    bus_id VARCHAR(255) NOT NULL,
    FOREIGN KEY(route_id) REFERENCES route(id),
    FOREIGN KEY(bus_id) REFERENCES bus(id)
);