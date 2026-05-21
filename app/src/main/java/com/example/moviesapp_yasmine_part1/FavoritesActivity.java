package com.example.moviesapp_yasmine_part1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.moviesapp_yasmine_part1.database.AppDatabase;
import com.example.moviesapp_yasmine_part1.database.MovieEntity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.widget.Toast;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MyMovieAdapter adapter;
    private TextView emptyStateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Edge-to-edge
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
        
        setContentView(R.layout.activity_favorites);

        Toolbar toolbar = findViewById(R.id.toolbar_favorites);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.favorites_title));
        }

        // Apply Insets for transparent system bars
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerViewFavorites);
        emptyStateText = findViewById(R.id.emptyStateText);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        FloatingActionButton fabSync = findViewById(R.id.fabSyncCloud);
        fabSync.setOnClickListener(v -> syncFavoritesToCloud());

        observeFavorites();
    }

    private void syncFavoritesToCloud() {
        AppDatabase.getDatabase(this).movieDao().getAllFavorites().observe(this, entities -> {
            if (entities != null && !entities.isEmpty()) {
                SharedPreferences prefs = getSharedPreferences("MoviesApp_Prefs", MODE_PRIVATE);
                String email = prefs.getString("user_email", "guest@test.com");
                
                for (MovieEntity entity : entities) {
                    SupabaseManager.addFavoriteToCloud(
                        email,
                        entity.title,
                        entity.genre,
                        entity.rating,
                        entity.description,
                        entity.imageRes
                    );
                }
                Toast.makeText(this, "Synchronisation de " + entities.size() + " films...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Rien à synchroniser", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void observeFavorites() {
        AppDatabase.getDatabase(this).movieDao().getAllFavorites().observe(this, favoriteEntities -> {
            if (favoriteEntities == null || favoriteEntities.isEmpty()) {
                emptyStateText.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                emptyStateText.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                
                List<MyMovieData> favoriteMovies = new ArrayList<>();
                for (MovieEntity entity : favoriteEntities) {
                    favoriteMovies.add(new MyMovieData(
                        entity.title,
                        entity.date,
                        entity.imageRes,
                        entity.rating,
                        entity.genre,
                        entity.description,
                        entity.mood
                    ));
                }

                adapter = new MyMovieAdapter(favoriteMovies.toArray(new MyMovieData[0]), this);
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
                });
                recyclerView.setAdapter(adapter);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
