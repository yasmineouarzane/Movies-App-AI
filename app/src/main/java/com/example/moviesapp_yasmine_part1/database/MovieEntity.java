package com.example.moviesapp_yasmine_part1.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorites")
public class MovieEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String title;
    public String description;
    public int imageRes;
    public float rating;
    public String genre;
    public String date;
    public String mood;

    public MovieEntity(String title, String description, int imageRes, float rating, String genre, String date, String mood) {
        this.title = title;
        this.description = description;
        this.imageRes = imageRes;
        this.rating = rating;
        this.genre = genre;
        this.date = date;
        this.mood = mood;
    }
}
