package com.example.moviesapp_yasmine_part1;

import android.util.Log;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;
import java.io.IOException;

public class SupabaseManager {
    private static final String SUPABASE_URL = "https://bmyoptpjreylkegcdohl.supabase.co";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJteW9wdHBqcmV5bGtlZ2Nkb2hsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Nzg5NjE0NTcsImV4cCI6MjA5NDUzNzQ1N30.kZYCeN9vz6zHXGZxz_oYF7RTLCtrdH4z9rn-ksSq5ro";

    private static final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static void addFavoriteToCloud(String email, String title, String genre, float rating, String description, int imageRes) {
        try {
            JSONObject json = new JSONObject();
            json.put("user_email", email);
            json.put("title", title);
            json.put("genre", genre);
            json.put("rating", rating);
            json.put("description", description);
            json.put("image_res", imageRes);

            RequestBody body = RequestBody.create(JSON, json.toString());
            Log.d("Supabase", "Envoi du JSON : " + json.toString());
            Request request = new Request.Builder()
                    .url(SUPABASE_URL + "/rest/v1/favorites")
                    .addHeader("apikey", SUPABASE_KEY)
                    .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Prefer", "return=minimal")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("Supabase", "Erreur réseau : " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        Log.d("Supabase", "Favori synchronisé avec succès !");
                    } else {
                        Log.e("Supabase", "Erreur serveur Supabase (" + response.code() + ") : " + response.body().string());
                    }
                }
            });
        } catch (Exception e) {
            Log.e("Supabase", "Erreur JSON : " + e.getMessage());
        }
    }
}
