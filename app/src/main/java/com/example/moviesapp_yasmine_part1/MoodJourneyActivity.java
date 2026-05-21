package com.example.moviesapp_yasmine_part1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public class MoodJourneyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_mood_journey);

        Toolbar toolbar = findViewById(R.id.journey_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.journey_coordinator), (v, insets) -> {
            var systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, 0);
            return insets;
        });

        String mood = getIntent().getStringExtra("selected_mood");
        if (mood == null) mood = "HAPPY";

        ImageView ivHeader = findViewById(R.id.ivJourneyHeader);
        setMoodHeader(ivHeader, mood);

        TextView tvTitle = findViewById(R.id.tvJourneyTitle);
        tvTitle.setText(getMoodTitle(mood));
        
        TextView tvSubtitle = findViewById(R.id.tvJourneySubtitle);
        tvSubtitle.setText(getMoodSubtitle(mood));

        RecyclerView rvJourney = findViewById(R.id.rvJourney);
        rvJourney.setLayoutManager(new LinearLayoutManager(this));

        List<MyMovieData> moodMovies = MovieRepository.getMoviesByMood(mood);
        MyMovieAdapter adapter = new MyMovieAdapter(moodMovies.toArray(new MyMovieData[0]), this);
        
        adapter.setOnItemClickListener((movie, imageView) -> {
            Intent intent = new Intent(this, MovieDetailsActivity.class);
            intent.putExtra("movie_name", movie.getMovieName());
            intent.putExtra("movie_date", movie.getMovieDate());
            intent.putExtra("movie_image", movie.getMovieImage());
            intent.putExtra("movie_rating", movie.getRating());
            intent.putExtra("movie_genre", movie.getGenre());
            intent.putExtra("movie_description", movie.getDescription());
            intent.putExtra("movie_mood", movie.getMood());
            
            androidx.core.app.ActivityOptionsCompat options = androidx.core.app.ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this, imageView, "poster_transition");
            startActivity(intent, options.toBundle());
        });

        rvJourney.setAdapter(adapter);
        rvJourney.scheduleLayoutAnimation();
    }

    private void setMoodHeader(ImageView imageView, String mood) {
        int resId;
        switch (mood.toUpperCase()) {
            case "SAD": resId = R.drawable.bg_mood_sad; break;
            case "RELAXED": resId = R.drawable.bg_mood_relaxed; break;
            case "ROMANTIC": resId = R.drawable.bg_mood_romantic; break;
            case "STRESSED": resId = R.drawable.bg_mood_stressed; break;
            default: resId = R.drawable.bg_mood_happy; break;
        }
        imageView.setImageResource(resId);
    }

    private String getMoodTitle(String mood) {
        switch (mood.toUpperCase()) {
            case "HAPPY": return "Besoin de rire ?";
            case "SAD": return "Émotions profondes";
            case "STRESSED": return "Évasion totale";
            case "RELAXED": return "Détente & Sérénité";
            case "ROMANTIC": return "Amour & Passion";
            default: return "Voyage Cinématographique";
        }
    }

    private String getMoodSubtitle(String mood) {
        switch (mood.toUpperCase()) {
            case "HAPPY": return "Des comédies pour illuminer votre journée";
            case "SAD": return "Des drames qui touchent le cœur";
            case "STRESSED": return "De l'action pour oublier le quotidien";
            case "RELAXED": return "Des films pour se ressourcer";
            case "ROMANTIC": return "Histoires d'amour inoubliables";
            default: return "Une sélection adaptée à votre humeur";
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}