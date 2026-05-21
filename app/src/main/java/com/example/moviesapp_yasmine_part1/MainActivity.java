package com.example.moviesapp_yasmine_part1;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private MyMovieAdapter myMovieAdapter;
    private MyMovieData[] allMovies;
    private TextToSpeech tts;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private BottomSheetDialog aiDialog;
    private TextView tvAIStatus, tvUserSpeech;
    private LinearProgressIndicator aiWaveform;
    private SharedPreferences sharedPreferences;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView tvLocation;
    private Location lastKnownLocation; // Stocker la position pour Maps

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    startVoiceAssistant();
                } else {
                    Toast.makeText(this, "Permission micro refusée", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Rendre l'interface "Edge-to-Edge" (moderne, sous la barre d'état)
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        
        setContentView(R.layout.activity_main);

        // Appliquer les insets pour éviter le chevauchement avec la barre d'état et de navigation
        View mainView = findViewById(R.id.main_coordinator);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
        
        sharedPreferences = getSharedPreferences("MoviesApp_Prefs", Context.MODE_PRIVATE);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        
        setupUI();
        loadUserProfile();
        checkLocationPermission();
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
        } else {
            getLastLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Permission localisation refusée", Toast.LENGTH_SHORT).show();
                if (tvLocation != null) tvLocation.setText("Localisation refusée");
            }
        }
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    this.lastKnownLocation = location;
                    updateLocationUI(location);
                    updateMapLocation(location);
                } else {
                    // Force la recherche de position si non trouvée en cache
                    com.google.android.gms.location.LocationRequest locationRequest = new com.google.android.gms.location.LocationRequest.Builder(
                            com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, 1000)
                            .setMaxUpdates(1)
                            .build();

                    fusedLocationClient.requestLocationUpdates(locationRequest, new com.google.android.gms.location.LocationCallback() {
                        @Override
                        public void onLocationResult(@NonNull com.google.android.gms.location.LocationResult locationResult) {
                            Location loc = locationResult.getLastLocation();
                            if (loc != null) {
                                lastKnownLocation = loc;
                                updateLocationUI(loc);
                                updateMapLocation(loc);
                            }
                        }
                    }, android.os.Looper.getMainLooper());
                }
            });
        }
    }

    private void updateMapLocation(Location location) {
        // La mise à jour de la carte interne est supprimée car nous utilisons l'app externe
    }

    private void searchCinemasNearby(String movieTitle) {
        if (lastKnownLocation == null) {
            Toast.makeText(this, "Localisation en cours... réessayez dans un instant", Toast.LENGTH_SHORT).show();
            return;
        }

        String query;
        if (movieTitle == null || movieTitle.isEmpty() || movieTitle.equalsIgnoreCase("cinemas")) {
            query = "cinémas à proximité";
        } else {
            // Requête optimisée pour forcer Google Maps à chercher les séances spécifiques
            query = "séances pour le film " + movieTitle;
        }

        // On utilise geo:0,0 avec une requête 'q' pour laisser Google Maps utiliser 
        // la position la plus précise possible tout en filtrant par séances
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(query));
        
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            String msg = (movieTitle.equalsIgnoreCase("cinemas")) ? "Recherche de cinémas..." : "Séances pour : " + movieTitle;
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            startActivity(mapIntent);
        } else {
            // Fallback navigateur avec une recherche Google encore plus précise
            String url = "https://www.google.com/search?q=" + Uri.encode(query + " près de moi");
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }
    }


    private void updateLocationUI(Location location) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                String city = addresses.get(0).getLocality();
                String country = addresses.get(0).getCountryName();
                tvLocation.setText(city + ", " + country);
            }
        } catch (Exception e) {
            tvLocation.setText(location.getLatitude() + ", " + location.getLongitude());
        }
    }

    private void loadUserProfile() {
        String userName = sharedPreferences.getString("user_name", "Utilisateur");
        String userImageUri = sharedPreferences.getString("user_image", null);
        
        TextView tvWelcome = findViewById(R.id.tvWelcomeUser);
        ImageView ivProfile = findViewById(R.id.ivUserIdentity);
        
        // Salutation dynamique selon l'heure
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String greeting;
        if (hour >= 18) greeting = getString(R.string.welcome_evening);
        else if (hour < 6) greeting = getString(R.string.welcome_night);
        else greeting = getString(R.string.welcome_morning);
        
        tvWelcome.setText(greeting + ", " + userName + " !");
        tvLocation = findViewById(R.id.tvLocation);

        // Rendre la localisation cliquable pour ouvrir Google Maps
        View locationContainer = (View) tvLocation.getParent();
        locationContainer.setOnClickListener(v -> {
            if (lastKnownLocation != null) {
                Uri gmmIntentUri = Uri.parse("geo:" + lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude() + "?q=" + Uri.encode(tvLocation.getText().toString()));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    // Fallback si l'app Maps n'est pas installée
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=" + lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude())));
                }
            }
        });
        
        if (userImageUri != null) {
            ivProfile.setImageURI(Uri.parse(userImageUri));
        }

        // Animation au clic sur le profil
        View.OnClickListener openProfile = v -> {
            v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction(() -> {
                v.animate().scaleX(1f).scaleY(1f).setDuration(100);
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            });
        };
        ivProfile.setOnClickListener(openProfile);
        tvWelcome.setOnClickListener(openProfile);
    }

    private void setupUI() {
        allMovies = MovieRepository.getAllMovies();
        
        java.util.List<MyMovieData> trendingList = new java.util.ArrayList<>();
        for (MyMovieData m : allMovies) {
            if (m.getRating() >= 8.5f) {
                trendingList.add(m);
            }
        }
        MyMovieData[] trendingMovies = trendingList.toArray(new MyMovieData[0]);

        RecyclerView recyclerViewTrending = findViewById(R.id.recyclerViewTrending);
        recyclerViewTrending.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        MyMovieAdapter trendingAdapter = new MyMovieAdapter(trendingMovies, this, true);
        recyclerViewTrending.setAdapter(trendingAdapter);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myMovieAdapter = new MyMovieAdapter(allMovies, this);
        recyclerView.setAdapter(myMovieAdapter);

        MyMovieAdapter.OnItemClickListener clickListener = (movie, movieImageView) -> {
            Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
            intent.putExtra("movie_name", movie.getMovieName());
            intent.putExtra("movie_date", movie.getMovieDate());
            intent.putExtra("movie_image", movie.getMovieImage());
            intent.putExtra("movie_rating", movie.getRating());
            intent.putExtra("movie_genre", movie.getGenre());
            intent.putExtra("movie_description", movie.getDescription());
            intent.putExtra("movie_mood", movie.getMood());
            
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    MainActivity.this, movieImageView, "poster_transition");
            startActivity(intent, options.toBundle());
        };

        myMovieAdapter.setOnItemClickListener(clickListener);
        trendingAdapter.setOnItemClickListener(clickListener);

        // Schedule layout animation
        recyclerView.scheduleLayoutAnimation();
        recyclerViewTrending.scheduleLayoutAnimation();

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchCinemasNearby(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                myMovieAdapter.getFilter().filter(newText);
                return false;
            }
        });

        ChipGroup chipGroup = findViewById(R.id.chipGroup);
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Chip chip = findViewById(checkedId);
            if (chip != null) {
                myMovieAdapter.filterByGenre(chip.getText().toString());
            } else {
                myMovieAdapter.filterByGenre("All");
            }
        });

        ImageButton btnQR = findViewById(R.id.btnQRScanner);
        btnQR.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ScannerActivity.class);
            startActivity(intent);
        });

        ImageButton btnFavorites = findViewById(R.id.btnFavorites);
        btnFavorites.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
            startActivity(intent);
        });

        initSpeechRecognizer();
        initTTS();
        
        // Configurer le bouton "Voir les cinémas" (remplace la carte grise)
        View cardMap = findViewById(R.id.cardMap);
        if (cardMap != null) {
            cardMap.setOnClickListener(v -> searchCinemasNearby("cinemas"));
        }

        FloatingActionButton btnAI = findViewById(R.id.btnAI);
        btnAI.setOnClickListener(v -> checkPermissionAndStartAssistant());
        
        // Pulse animation for AI button
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
        btnAI.startAnimation(pulse);

        ExtendedFloatingActionButton fabRandom = findViewById(R.id.fabRandom);
        fabRandom.setOnClickListener(v -> {
            if (allMovies.length > 0) {
                MyMovieData randomMovie = allMovies[new Random().nextInt(allMovies.length)];
                Toast.makeText(this, "Suggestion : " + randomMovie.getMovieName(), Toast.LENGTH_SHORT).show();
                
                Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
                intent.putExtra("movie_name", randomMovie.getMovieName());
                intent.putExtra("movie_date", randomMovie.getMovieDate());
                intent.putExtra("movie_image", randomMovie.getMovieImage());
                intent.putExtra("movie_rating", randomMovie.getRating());
                intent.putExtra("movie_genre", randomMovie.getGenre());
                intent.putExtra("movie_description", randomMovie.getDescription());
                intent.putExtra("movie_mood", randomMovie.getMood());
                startActivity(intent);
            }
        });
    }

    private void showAIDialog() {
        if (aiDialog == null) {
            aiDialog = new BottomSheetDialog(this);
            View view = getLayoutInflater().inflate(R.layout.layout_ai_assistant, null);
            aiDialog.setContentView(view);
            tvAIStatus = view.findViewById(R.id.tvAIStatus);
            tvUserSpeech = view.findViewById(R.id.tvUserSpeech);
            aiWaveform = view.findViewById(R.id.aiWaveform);
        }
        tvAIStatus.setText(R.string.ai_listening);
        tvUserSpeech.setText("");
        aiWaveform.setVisibility(View.VISIBLE);
        aiDialog.show();
    }

    private void initSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.FRENCH.toString());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                tvAIStatus.setText(R.string.ai_listening);
                aiWaveform.setIndeterminate(true);
            }

            @Override
            public void onBeginningOfSpeech() {}

            @Override
            public void onRmsChanged(float rmsdB) {
                int progress = (int) (rmsdB * 5 + 2);
                if (progress > 0) aiWaveform.setProgress(progress, true);
            }

            @Override
            public void onBufferReceived(byte[] buffer) {}

            @Override
            public void onEndOfSpeech() {
                tvAIStatus.setText(R.string.ai_analyzing);
                aiWaveform.setIndeterminate(true);
            }

            @Override
            public void onError(int error) {
                handleSpeechError(error);
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String command = matches.get(0);
                    tvUserSpeech.setText(getString(R.string.ai_user_speech_format, command));
                    processAICommand(command);
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {}

            @Override
            public void onEvent(int eventType, Bundle params) {}
        });
    }

    private void handleSpeechError(int error) {
        String message;
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO: message = "Erreur audio"; break;
            case SpeechRecognizer.ERROR_CLIENT: message = "Erreur client"; break;
            case SpeechRecognizer.ERROR_NETWORK: message = "Erreur réseau"; break;
            case SpeechRecognizer.ERROR_NO_MATCH: message = "Je n'ai pas compris"; break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT: message = "Temps écoulé"; break;
            default: message = "Erreur assistant"; break;
        }
        tvAIStatus.setText(message);
        aiWaveform.setVisibility(View.GONE);
        new Handler().postDelayed(() -> {
            if (aiDialog != null) aiDialog.dismiss();
        }, 2000);
    }

    private void initTTS() {
        tts = new TextToSpeech(this, status -> {
            if (status != TextToSpeech.ERROR) {
                tts.setLanguage(Locale.FRENCH);
                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {}

                    @Override
                    public void onDone(String utteranceId) {
                        if ("START_LISTENING".equals(utteranceId)) {
                            runOnUiThread(() -> {
                                if (aiDialog != null && aiDialog.isShowing()) {
                                    speechRecognizer.startListening(speechRecognizerIntent);
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(String utteranceId) {}
                });
            }
        });
    }

    private void speak(String text, String utteranceId) {
        if (tts != null) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
        }
    }

    private void checkPermissionAndStartAssistant() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
        } else {
            startVoiceAssistant();
        }
    }

    private void startVoiceAssistant() {
        showAIDialog();
        speak("Bonjour ! Je suis votre assistant cinéma. Quel film cherchez-vous ou comment vous sentez-vous ?", "START_LISTENING");
    }

    private void processAICommand(String command) {
        String text = command.toLowerCase().trim();
        
        // 1. RECHERCHE PAR NOM DE FILM (Souple et prioritaire)
        MyMovieData foundMovie = null;
        if (allMovies != null) {
            for (MyMovieData movie : allMovies) {
                String movieTitle = movie.getMovieName().toLowerCase();
                // Nettoyage : "Avatar (2009)" -> "avatar"
                String cleanTitle = movieTitle.replaceAll("\\s*\\(.*?\\)", "").trim();
                
                // On vérifie si le titre est dans la phrase OU si un mot clé du titre est dans la phrase
                if (text.contains(cleanTitle) || cleanTitle.contains(text)) {
                    foundMovie = movie;
                    break;
                }
                
                // Si le titre est composé de plusieurs mots, on vérifie les mots importants
                String[] words = cleanTitle.split(" ");
                for (String word : words) {
                    if (word.length() > 3 && text.contains(word)) {
                        foundMovie = movie;
                        break;
                    }
                }
                if (foundMovie != null) break;
            }
        }

        if (foundMovie != null) {
            String foundMsg = "J'ai trouvé le film " + foundMovie.getMovieName() + ". Voici les détails.";
            tvAIStatus.setText("Film trouvé : " + foundMovie.getMovieName());
            aiWaveform.setVisibility(View.GONE);
            speak(foundMsg, "MOVIE_FOUND");

            final MyMovieData finalMovie = foundMovie;
            new Handler().postDelayed(() -> {
                if (aiDialog != null && aiDialog.isShowing()) {
                    aiDialog.dismiss();
                    Intent intent = new Intent(this, MovieDetailsActivity.class);
                    intent.putExtra("movie_name", finalMovie.getMovieName());
                    intent.putExtra("movie_date", finalMovie.getMovieDate());
                    intent.putExtra("movie_image", finalMovie.getMovieImage());
                    intent.putExtra("movie_rating", finalMovie.getRating());
                    intent.putExtra("movie_genre", finalMovie.getGenre());
                    intent.putExtra("movie_description", finalMovie.getDescription());
                    intent.putExtra("movie_mood", finalMovie.getMood());
                    startActivity(intent);
                }
            }, 2500);
            return;
        }

        // 2. DÉTECTION D'ÉMOTION
        String detectedMood = null;
        if (text.contains("heureux") || text.contains("content") || text.contains("joie") || text.contains("super") || text.contains("bien")) {
            detectedMood = "HAPPY";
        } else if (text.contains("triste") || text.contains("mal") || text.contains("cafard") || text.contains("déprimé")) {
            detectedMood = "SAD";
        } else if (text.contains("stress") || text.contains("peur") || text.contains("anxiété") || text.contains("fatigué")) {
            detectedMood = "STRESSED";
        } else if (text.contains("calme") || text.contains("relax") || text.contains("tranquille") || text.contains("détendu")) {
            detectedMood = "RELAXED";
        } else if (text.contains("amour") || text.contains("romantique") || text.contains("coeur") || text.contains("amoureux")) {
            detectedMood = "ROMANTIC";
        }

        if (detectedMood != null) {
            String message = "Je détecte que vous avez besoin d'un film " + detectedMood.toLowerCase() + ". Lançons votre voyage.";
            tvAIStatus.setText(getString(R.string.ai_detected_mood, detectedMood));
            aiWaveform.setVisibility(View.GONE);
            speak(message, "FINISHED");
            
            final String finalMood = detectedMood;
            new Handler().postDelayed(() -> {
                if (aiDialog != null && aiDialog.isShowing()) {
                    aiDialog.dismiss();
                    Intent intent = new Intent(this, MoodJourneyActivity.class);
                    intent.putExtra("selected_mood", finalMood);
                    startActivity(intent);
                }
            }, 3500);
        } else {
            String errorMsg = "Désolé, je n'ai pas trouvé ce film. Voici une suggestion aléatoire.";
            tvAIStatus.setText(R.string.ai_analysis_error);
            aiWaveform.setVisibility(View.GONE);
            speak(errorMsg, "ERROR");
            new Handler().postDelayed(() -> {
                if (aiDialog != null && aiDialog.isShowing()) {
                    aiDialog.dismiss();
                    findViewById(R.id.fabRandom).performClick();
                }
            }, 3000);
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        super.onDestroy();
    }
}
