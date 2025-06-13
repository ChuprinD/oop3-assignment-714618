package com.moviewatchlist.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "movies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String release_year;
    private String director;
    private String genre;
    private boolean watched;
    private int rating;
    private String imagePath;
}
