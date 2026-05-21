package com.example.moviesapp_yasmine_part1;

public class MyMovieData {
    private String movieName;
    private String movieDate;
    private Integer movieImage;
    private Float rating;
    private String genre;
    private String description;
    private String mood;

    public MyMovieData(String movieName, String movieDate, Integer movieImage, Float rating, String genre, String description, String mood) {
        this.movieName = movieName;
        this.movieDate = movieDate;
        this.movieImage = movieImage;
        this.rating = rating;
        this.genre = genre;
        this.description = description;
        this.mood = mood;
    }

    public String getMovieName() { return movieName; }
    public String getMovieDate() { return movieDate; }
    public Integer getMovieImage() { return movieImage; }
    public Float getRating() { return rating; }
    public String getGenre() { return genre; }
    public String getDescription() { return description; }
    public String getMood() { return mood; }
}