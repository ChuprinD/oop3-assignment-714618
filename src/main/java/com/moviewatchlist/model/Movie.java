package com.moviewatchlist.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {
    private Long id;
    private String title;
    private String director;
    private String year;
    private String genre;
    private boolean watched;
    private int rating;
    private String imagePath;
}
