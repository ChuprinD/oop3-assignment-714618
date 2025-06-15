package com.moviewatchlist.repository;

import com.moviewatchlist.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing and managing Movie entities in the database.
 * <p>
 * Inherits standard CRUD operations (create, read, update, delete) and pagination
 * from Spring Data JPA's {@link JpaRepository}.
 * <p>
 */
@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

}
