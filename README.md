
# 🎬 Movie Watchlist – OOP3 Assignment

This is a Spring Boot backend project implementing a Movie Watchlist system. It fetches data from the OMDb and TMDb APIs, downloads related images, persists the data in a database, and exposes a RESTful interface for client consumption.

---

## 🧠 Project Summary

When a user adds a movie by title, the system:

1. Retrieves **basic data** (title, director, genre, year) from the **OMDb API**.
2. Fetches **additional images and similar movies** from the **TMDb API**.
3. Downloads **up to 3 movie posters/backdrops** and stores them in the local file system.
4. Saves the full movie record into an H2 in-memory database, including:
   - `watched` flag (boolean)
   - `rating` (1–5)
   - `imagePath` to the representative image

---

## 🌐 Exposed API

| Method | Endpoint                       | Description                         |
|--------|--------------------------------|-------------------------------------|
| `POST` | `/movies`                      | Add a new movie by title            |
| `GET`  | `/movies?page=0&size=10`       | Retrieve paginated movie list       |
| `PUT`  | `/movies/{id}/watched?watched=true` | Toggle watched status         |
| `PUT`  | `/movies/{id}/rating?rating=4` | Set movie rating (1–5)              |
| `DELETE` | `/movies/{id}`              | Delete a movie                      |
| `GET`  | `/movies/{id}/similar`         | Get list of similar movies          |

## ⚙️ Configuration

**API Keys and DB setup** (see `application.properties`):

```properties
omdb.api.key=your_omdb_key
tmdb.api.key=your_tmdb_key

spring.datasource.url=jdbc:h2:mem:moviedb
spring.h2.console.enabled=true
```

**Schema** (`schema.sql`) ensures correct table creation:

```sql
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
```

## 🚀 Getting Started

1. Clone repository:
   ```bash
   git clone https://github.com/ChuprinD/oop3-assignment-714618.git
   cd oop3-assignment-714618
   ```

2. Run with Maven:
   ```bash
   mvn spring-boot:run
   ```

3. Test endpoints using curl.