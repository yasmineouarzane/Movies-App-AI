package com.example.moviesapp_yasmine_part1.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MovieDao {
    @Query("SELECT * FROM favorites")
    LiveData<List<MovieEntity>> getAllFavorites();

    @Insert
    void insert(MovieEntity movie);

    @Delete
    void delete(MovieEntity movie);

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE title = :movieTitle LIMIT 1)")
    boolean isFavorite(String movieTitle);

    @Query("DELETE FROM favorites WHERE title = :movieTitle")
    void deleteByTitle(String movieTitle);
}
