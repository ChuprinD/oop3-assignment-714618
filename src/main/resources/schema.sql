CREATE TABLE movies (
    id IDENTITY PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    release_year VARCHAR(10),
    director VARCHAR(255),
    genre VARCHAR(255),
    watched BOOLEAN DEFAULT FALSE,
    rating INT CHECK (rating BETWEEN 0 AND 5),
    image_path VARCHAR(500)
);
