package com.example.moviesapp_yasmine_part1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.util.Log;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.MeteringPoint;
import androidx.camera.core.MeteringPointFactory;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ScannerActivity modernisée utilisant CameraX et ML Kit.
 * Cette activité permet de scanner un titre de film via la caméra ou la galerie.
 */
public class ScannerActivity extends AppCompatActivity {

    private static final String TAG = "ScannerActivity";

    private PreviewView previewView;
    private ProgressBar progressBar;
    private TextView tvScanning;
    private ExecutorService cameraExecutor;
    private boolean isDetected = false;
    private TextRecognizer textRecognizer;
    private BarcodeScanner barcodeScanner;
    private ImageLabeler imageLabeler;
    private Camera camera;
    private boolean isFlashOn = false;
    private ScaleGestureDetector scaleGestureDetector;

    // Launchers modernes pour les permissions et les résultats d'activité
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
        setContentView(R.layout.activity_scanner);

        initUI();
        initLaunchers();
        
        cameraExecutor = Executors.newSingleThreadExecutor();
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        barcodeScanner = BarcodeScanning.getClient();
        imageLabeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);

        checkPermissionsAndStartCamera();
    }

    private void initUI() {
        previewView = findViewById(R.id.previewView);
        progressBar = findViewById(R.id.progressBar);
        tvScanning = findViewById(R.id.tvScanning);
        FloatingActionButton btnGallery = findViewById(R.id.btnGallery);
        FloatingActionButton btnFlash = findViewById(R.id.btnFlash);
        ImageButton btnClose = findViewById(R.id.btnClose);
        View laserLine = findViewById(R.id.laserLine);

        // Apply Insets
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Animation du laser de scan
        if (laserLine != null) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.laser_scan);
            laserLine.startAnimation(animation);
        }

        btnGallery.setOnClickListener(v -> openGallery());
        btnFlash.setOnClickListener(v -> toggleFlash(btnFlash));
        btnClose.setOnClickListener(v -> finish());

        // Tap to focus and Pinch to zoom
        setupInteractions();
    }

    private void setupInteractions() {
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(@NonNull ScaleGestureDetector detector) {
                if (camera == null) return false;
                androidx.camera.core.ZoomState zoomState = camera.getCameraInfo().getZoomState().getValue();
                if (zoomState != null) {
                    float scale = zoomState.getZoomRatio() * detector.getScaleFactor();
                    camera.getCameraControl().setZoomRatio(scale);
                }
                return true;
            }
        });

        previewView.setOnTouchListener((v, event) -> {
            scaleGestureDetector.onTouchEvent(event);
            if (event.getAction() == MotionEvent.ACTION_UP) {
                focusCamera(event.getX(), event.getY());
                v.performClick();
            }
            return true;
        });
    }

    private void focusCamera(float x, float y) {
        if (camera == null) return;
        MeteringPointFactory factory = previewView.getMeteringPointFactory();
        MeteringPoint point = factory.createPoint(x, y);
        FocusMeteringAction action = new FocusMeteringAction.Builder(point).build();
        camera.getCameraControl().startFocusAndMetering(action);
    }

    private void toggleFlash(FloatingActionButton btnFlash) {
        if (camera != null && camera.getCameraInfo().hasFlashUnit()) {
            isFlashOn = !isFlashOn;
            camera.getCameraControl().enableTorch(isFlashOn);
            btnFlash.setImageResource(isFlashOn ? android.R.drawable.btn_star_big_on : android.R.drawable.ic_menu_compass);
        } else {
            Toast.makeText(this, "Flash non disponible", Toast.LENGTH_SHORT).show();
        }
    }

    private void initLaunchers() {
        // Nouveau système pour les permissions (remplace onRequestPermissionsResult)
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        startCamera();
                    } else {
                        Toast.makeText(this, "La permission caméra est requise pour scanner", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
        );

        // Nouveau système pour le résultat d'activité (remplace onActivityResult)
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        processGalleryImage(result.getData().getData());
                    }
                }
        );
    }

    private void checkPermissionsAndStartCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void processGalleryImage(Uri imageUri) {
        if (imageUri == null) return;
        setLoading(true);
        try {
            InputImage image = InputImage.fromFilePath(this, imageUri);
            textRecognizer.process(image)
                    .addOnSuccessListener(visionText -> {
                        processVisionText(visionText);
                        if (isDetected) {
                            setLoading(false);
                        } else {
                            // Si pas de texte, on tente le QR Code
                            barcodeScanner.process(image)
                                    .addOnSuccessListener(barcodes -> {
                                        setLoading(false);
                                        boolean found = false;
                                        for (Barcode barcode : barcodes) {
                                            if (handleResult(barcode.getRawValue())) {
                                                found = true;
                                                break;
                                            }
                                        }
                                        if (!found) {
                                            Toast.makeText(this, "Aucun titre ou QR Code de film reconnu", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(e -> setLoading(false));
                        }
                    })
                    .addOnFailureListener(e -> {
                        setLoading(false);
                        Log.e(TAG, "Erreur scan galerie", e);
                    });
        } catch (IOException e) {
            setLoading(false);
            Log.e(TAG, "Erreur chargement image", e);
        }
    }

    private void setLoading(boolean loading) {
        runOnUiThread(() -> {
            progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
            if (tvScanning != null) tvScanning.setVisibility(loading ? View.VISIBLE : View.GONE);
        });
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraUseCases(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Erreur CameraProvider", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCameraUseCases(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(cameraExecutor, imageProxy -> {
            if (isDetected) {
                imageProxy.close();
                return;
            }

            @SuppressWarnings("UnsafeOptInUsageError")
            android.media.Image mediaImage = imageProxy.getImage();
            if (mediaImage != null) {
                InputImage image = InputImage.fromMediaImage(mediaImage,
                        imageProxy.getImageInfo().getRotationDegrees());

                // 1. Analyse des labels (Améliore la détection contextuelle)
                imageLabeler.process(image).addOnSuccessListener(labels -> {
                    for (ImageLabel label : labels) {
                        String labelText = label.getText();
                        if (labelText.contains("Movie") || labelText.contains("Poster") || labelText.contains("Cinema")) {
                            runOnUiThread(() -> {
                                if (tvScanning != null) tvScanning.setText(R.string.scanner_movie_poster_detected);
                            });
                            break;
                        }
                    }
                });

                // 2. Analyse de texte globale pour une meilleure précision
                textRecognizer.process(image)
                        .addOnSuccessListener(this::processVisionText)
                        .addOnCompleteListener(task -> {
                            if (!isDetected) {
                                // 3. Analyse Barcode (Fallback)
                                barcodeScanner.process(image)
                                        .addOnSuccessListener(barcodes -> {
                                            for (Barcode barcode : barcodes) {
                                                if (handleResult(barcode.getRawValue())) break;
                                            }
                                        })
                                        .addOnCompleteListener(t -> imageProxy.close());
                            } else {
                                imageProxy.close();
                            }
                        });
            } else {
                imageProxy.close();
            }
        });

        try {
            cameraProvider.unbindAll();
            camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
        } catch (Exception e) {
            Log.e(TAG, "Erreur binding CameraX", e);
        }
    }

    /**
     * Analyse le texte détecté pour trouver le meilleur match parmi tous les blocs.
     */
    private void processVisionText(Text visionText) {
        if (isDetected || visionText == null) return;

        MyMovieData bestMovie = null;
        int highestScore = 0;

        // 1. Évaluer le texte complet (utile pour les titres sur plusieurs lignes)
        Match fullMatch = findBestMatchForText(visionText.getText());
        if (fullMatch.score > highestScore) {
            highestScore = fullMatch.score;
            bestMovie = fullMatch.movie;
        }

        // 2. Évaluer chaque bloc individuellement pour plus de précision
        for (Text.TextBlock block : visionText.getTextBlocks()) {
            Match blockMatch = findBestMatchForText(block.getText());
            if (blockMatch.score > highestScore) {
                highestScore = blockMatch.score;
                bestMovie = blockMatch.movie;
            }
        }

        // Seuil de confiance minimal (50) pour éviter les faux positifs
        if (bestMovie != null && highestScore >= 50) {
            navigateToDetails(bestMovie, highestScore);
        }
    }

    private Match findBestMatchForText(String text) {
        if (text == null || text.trim().isEmpty()) return new Match(null, 0);

        String cleanedDetected = text.replace("\n", " ").toLowerCase().replaceAll("[^a-z0-9\\s]", " ").replaceAll("\\s+", " ").trim();
        
        MyMovieData bestMatch = null;
        int maxScore = 0;

        for (MyMovieData movie : MovieRepository.getAllMovies()) {
            int currentScore = calculateScore(cleanedDetected, movie.getMovieName());
            if (currentScore > maxScore) {
                maxScore = currentScore;
                bestMatch = movie;
            }
        }
        return new Match(bestMatch, maxScore);
    }

    private void navigateToDetails(MyMovieData movie, int score) {
        isDetected = true;
        Log.d(TAG, "Film identifié : " + movie.getMovieName() + " (Score: " + score + ")");
        
        vibrate();
        
        runOnUiThread(() -> {
            Intent intent = new Intent(this, MovieDetailsActivity.class);
            intent.putExtra("movie_name", movie.getMovieName());
            intent.putExtra("movie_date", movie.getMovieDate());
            intent.putExtra("movie_image", movie.getMovieImage());
            intent.putExtra("movie_rating", movie.getRating());
            intent.putExtra("movie_genre", movie.getGenre());
            intent.putExtra("movie_description", movie.getDescription());
            intent.putExtra("movie_mood", movie.getMood());
            startActivity(intent);
            finish();
        });
    }

    private static class Match {
        MyMovieData movie;
        int score;
        Match(MyMovieData movie, int score) {
            this.movie = movie;
            this.score = score;
        }
    }

    /**
     * Compare le texte détecté avec la base de données locale.
     * Cette méthode est conservée pour le fallback Barcode.
     */
    private synchronized boolean handleResult(String text) {
        if (isDetected || text == null || text.trim().isEmpty()) return false;
        Match match = findBestMatchForText(text);
        if (match.movie != null && match.score >= 50) {
            navigateToDetails(match.movie, match.score);
            return true;
        }
        return false;
    }

    private int calculateScore(String cleanedDetected, String movieName) {
        String normalizedMovieName = movieName.toLowerCase().replaceAll("[^a-z0-9\\s]", " ").trim();
        if (normalizedMovieName.isEmpty()) return 0;

        String cleaned = " " + cleanedDetected + " ";
        String paddedTitle = " " + normalizedMovieName + " ";

        // 1. Match exact (Score maximal)
        if (cleanedDetected.equals(normalizedMovieName)) {
            return 1000;
        }

        // 2. Contient le titre exact avec bordures
        if (cleaned.contains(paddedTitle)) {
            return normalizedMovieName.length() * 10;
        }

        // 3. Titre partiel (ex: "Avengers" détecté pour "Avengers Endgame")
        if (normalizedMovieName.contains(cleanedDetected) && cleanedDetected.length() > 3) {
            return (cleanedDetected.length() * 100) / normalizedMovieName.length();
        }

        // 4. FUZZY MATCHING (Levenshtein)
        // Utile pour les erreurs d'OCR (ex: 'v' lu comme 'u')
        int distance = getLevenshteinDistance(cleanedDetected, normalizedMovieName);
        // Si la distance est faible par rapport à la longueur du titre
        if (distance <= 2 && normalizedMovieName.length() > 4) {
            return 80;
        }

        // 5. Recherche par mots-clés (pour titres longs)
        String[] words = normalizedMovieName.split("\\s+");
        if (words.length > 1) {
            int wordsFound = 0;
            for (String word : words) {
                if (word.length() > 2 && cleaned.contains(" " + word + " ")) {
                    wordsFound++;
                }
            }
            if ((float) wordsFound / words.length >= 0.6f) {
                return 70;
            }
        }

        return 0;
    }

    private int getLevenshteinDistance(String s1, String s2) {
        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0) costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(100);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Fermeture des ressources
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
        if (textRecognizer != null) {
            textRecognizer.close();
        }
        if (barcodeScanner != null) {
            barcodeScanner.close();
        }
        if (imageLabeler != null) {
            imageLabeler.close();
        }
    }
}
