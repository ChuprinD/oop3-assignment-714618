package com.moviewatchlist.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieDTO {
    private String title;
    private String director;
    private String year;
    private String genre;
    private boolean watched;
    private int rating;
    private String imagePath;
}
