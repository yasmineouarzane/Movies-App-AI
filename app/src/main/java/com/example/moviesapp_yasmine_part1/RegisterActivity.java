package com.example.moviesapp_yasmine_part1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private TextInputEditText etFullName, etEmail, etPassword;
    private Button btnRegister;
    private TextView tvLogin;
    private ImageView ivProfilePicture;
    private FloatingActionButton fabAddPhoto;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Edge-to-edge
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
        
        setContentView(R.layout.activity_register);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        fabAddPhoto = findViewById(R.id.fabAddPhoto);

        // Apply Insets for transparent system bars
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fabAddPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE);
        });

        btnRegister.setOnClickListener(v -> {
            String name = etFullName.getText().toString();
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            } else {
                // Sauvegarde des données utilisateur
                android.content.SharedPreferences prefs = getSharedPreferences("MoviesApp_Prefs", android.content.Context.MODE_PRIVATE);
                android.content.SharedPreferences.Editor editor = prefs.edit();
                editor.putString("user_name", name);
                editor.putString("user_email", email);
                editor.putString("user_password", password);
                if (selectedImageUri != null) {
                    editor.putString("user_image", selectedImageUri.toString());
                }
                editor.apply();

                Toast.makeText(this, "Compte créé pour " + name, Toast.LENGTH_SHORT).show();
                finish(); // Retour au login
            }
        });

        tvLogin.setOnClickListener(v -> {
            finish(); // Retour au login
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            ivProfilePicture.setImageURI(selectedImageUri);
        }
    }
}
