package com.example.moviesapp_yasmine_part1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.moviesapp_yasmine_part1.database.AppDatabase;
import com.example.moviesapp_yasmine_part1.database.MovieEntity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class MovieDetailsActivity extends AppCompatActivity {

    private boolean isFavorite = false;
    private SharedPreferences sharedPreferences;
    private String movieName;
    private String movieDate;
    private int movieImgRes;
    private float movieRating;
    private String movieGenre;
    private String movieDesc;
    private String movieMood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Edge-to-edge
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
        
        setContentView(R.layout.activity_movie_details);

        sharedPreferences = getSharedPreferences("MoviesApp_Prefs", Context.MODE_PRIVATE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Apply Insets for transparent system bars
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView movieImage = findViewById(R.id.movie_detail_image);
        TextView title = findViewById(R.id.movie_detail_title);
        TextView genreView = findViewById(R.id.movie_detail_genre);
        TextView year = findViewById(R.id.movie_detail_year);
        TextView description = findViewById(R.id.movie_detail_description);
        RatingBar ratingBar = findViewById(R.id.movie_detail_rating);
        FloatingActionButton fabFavorite = findViewById(R.id.fab_favorite);
        Button btnTrailer = findViewById(R.id.btn_watch_trailer);

        // Get data from Intent
        movieName = getIntent().getStringExtra("movie_name");
        movieDate = getIntent().getStringExtra("movie_date");
        movieImgRes = getIntent().getIntExtra("movie_image", 0);
        movieRating = getIntent().getFloatExtra("movie_rating", 0.0f);
        movieGenre = getIntent().getStringExtra("movie_genre");
        movieDesc = getIntent().getStringExtra("movie_description");
        movieMood = getIntent().getStringExtra("movie_mood");

        // Set data
        title.setText(movieName);
        year.setText(movieDate);
        genreView.setText(movieGenre);
        description.setText(movieDesc);
        movieImage.setImageResource(movieImgRes);
        ratingBar.setRating(movieRating / 2);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(movieName);
        }

        // Check if favorite
        AppDatabase.databaseWriteExecutor.execute(() -> {
            isFavorite = AppDatabase.getDatabase(this).movieDao().isFavorite(movieName);
            runOnUiThread(() -> updateFavoriteIcon(fabFavorite));
        });

        // Favorite Toggle
        fabFavorite.setOnClickListener(v -> {
            isFavorite = !isFavorite;
            String userEmail = sharedPreferences.getString("user_email", "guest@test.com");

            AppDatabase.databaseWriteExecutor.execute(() -> {
                if (isFavorite) {
                    // Sauvegarde locale
                    AppDatabase.getDatabase(this).movieDao().insert(
                        new MovieEntity(movieName, movieDesc, movieImgRes, movieRating, movieGenre, movieDate, movieMood)
                    );
                    // Sauvegarde Cloud Supabase
                    SupabaseManager.addFavoriteToCloud(userEmail, movieName, movieGenre, movieRating, movieDesc, movieImgRes);
                } else {
                    AppDatabase.getDatabase(this).movieDao().deleteByTitle(movieName);
                }
                runOnUiThread(() -> {
                    updateFavoriteIcon(fabFavorite);
                    String message = isFavorite ? "Ajouté et synchronisé !" : "Retiré des favoris";
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                });
            });
        });

        // Trailer Button
        btnTrailer.setOnClickListener(v -> {
            String query = movieName + " official trailer";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=" + query));
            startActivity(intent);
        });

        setupRecommendations(movieGenre, movieMood, movieName);
    }

    private void updateFavoriteIcon(FloatingActionButton fab) {
        if (isFavorite) {
            fab.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            fab.setImageResource(android.R.drawable.btn_star_big_off);
        }
    }

    private void setupRecommendations(String genre, String mood, String currentMovie) {
        RecyclerView recyclerViewRec = findViewById(R.id.recyclerViewRecommendations);
        recyclerViewRec.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        
        List<MyMovieData> recommendations = MovieRepository.getRecommendations(genre, mood, currentMovie);
        
        if (!recommendations.isEmpty()) {
            MyMovieData[] recArray = recommendations.toArray(new MyMovieData[0]);
            MyMovieAdapter adapter = new MyMovieAdapter(recArray, this, true); // true for horizontal cards
            adapter.setOnItemClickListener((movie, movieImageView) -> {
                Intent intent = new Intent(this, MovieDetailsActivity.class);
                intent.putExtra("movie_name", movie.getMovieName());
                intent.putExtra("movie_date", movie.getMovieDate());
                intent.putExtra("movie_image", movie.getMovieImage());
                intent.putExtra("movie_rating", movie.getRating());
                intent.putExtra("movie_genre", movie.getGenre());
                intent.putExtra("movie_description", movie.getDescription());
                intent.putExtra("movie_mood", movie.getMood());
                startActivity(intent);
                finish(); // Close current details and open new one
            });
            recyclerViewRec.setAdapter(adapter);
            recyclerViewRec.scheduleLayoutAnimation();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
