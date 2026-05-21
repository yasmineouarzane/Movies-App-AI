package com.example.moviesapp_yasmine_part1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;
    private SharedPreferences sharedPreferences;
    private ImageView ivProfile;
    private TextView tvName, tvEmail;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Edge-to-edge
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
        
        setContentView(R.layout.activity_profile);

        sharedPreferences = getSharedPreferences("MoviesApp_Prefs", Context.MODE_PRIVATE);

        Toolbar toolbar = findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.profile_title));
        }

        // Apply Insets for transparent system bars
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ivProfile = findViewById(R.id.ivProfileDetail);
        tvName = findViewById(R.id.tvProfileName);
        tvEmail = findViewById(R.id.tvProfileEmail);
        MaterialButton btnEdit = findViewById(R.id.btnEditProfile);
        MaterialButton btnLogout = findViewById(R.id.btnLogout);

        loadData();

        // Modifier la photo en cliquant dessus
        ivProfile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE);
        });

        btnEdit.setOnClickListener(v -> showEditNameDialog());

        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Déconnexion")
                    .setMessage("Voulez-vous vraiment vous déconnecter ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Non", null)
                    .show();
        });
    }

    private void loadData() {
        String name = sharedPreferences.getString("user_name", "Utilisateur");
        String email = sharedPreferences.getString("user_email", "email@exemple.com");
        String imageUri = sharedPreferences.getString("user_image", null);

        tvName.setText(name);
        tvEmail.setText(email);
        if (imageUri != null) {
            ivProfile.setImageURI(Uri.parse(imageUri));
        }
    }

    private void showEditNameDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_name, null);
        EditText etNewName = view.findViewById(R.id.etNewName);
        etNewName.setText(tvName.getText().toString());

        new AlertDialog.Builder(this)
                .setTitle("Modifier le nom")
                .setView(view)
                .setPositiveButton("Enregistrer", (dialog, which) -> {
                    String newName = etNewName.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        sharedPreferences.edit().putString("user_name", newName).apply();
                        tvName.setText(newName);
                        Toast.makeText(this, "Nom mis à jour", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            ivProfile.setImageURI(selectedImageUri);
            sharedPreferences.edit().putString("user_image", selectedImageUri.toString()).apply();
            Toast.makeText(this, "Photo mise à jour", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
