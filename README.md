# MoviesApp 🎬
**Assistant cinématographique intelligent avec IA et Cloud**

Plateforme mobile d'exploration et de recommandation de films basée sur l'IA.
Built with Java 11 · Android SDK 36 · Supabase · Room · ML Kit

![Java](https://img.shields.io/badge/Java-11-orange?style=for-the-badge&logo=java)
![Android](https://img.shields.io/badge/Android-36-green?style=for-the-badge&logo=android)
![Supabase](https://img.shields.io/badge/Supabase-Cloud-blueviolet?style=for-the-badge&logo=supabase)
![Room](https://img.shields.io/badge/Room-SQLite-orange?style=for-the-badge&logo=sqlite)
![ML Kit](https://img.shields.io/badge/ML_Kit-Vision-blue?style=for-the-badge&logo=google)
![Google Maps](https://img.shields.io/badge/Google_Maps-Location-red?style=for-the-badge&logo=googlemaps)

## 📌 Table des matières
* [À propos](#à-propos)
* [Fonctionnalités](#fonctionnalités)
* [Stack technique](#stack-technique)
* [Architecture](#architecture)
* [Installation locale](#installation-locale)
* [Auteur](#auteur)

## 📖 À propos
**MoviesApp** est une application Android innovante qui transforme la recherche de films en une expérience interactive. Grâce à l'intégration d'un assistant vocal intelligent et d'une analyse d'humeur, l'application permet aux utilisateurs de découvrir des contenus cinématographiques de manière naturelle et personnalisée.

L'application utilise **Supabase** pour la gestion des utilisateurs dans le cloud et **Room (SQLite)** pour la persistance locale des favoris, garantissant une expérience fluide même hors-ligne.

## ✨ Fonctionnalités

### 🔐 Utilisateurs & Auth
* Inscription et Connexion sécurisées via **Supabase**.
* Écran de démarrage (Splash) animé.
* Gestion du profil (Nom d'utilisateur, Image de profil).
* Persistance des préférences via SharedPreferences.

### 🎙️ Assistant IA (Vocal)
* Interaction bidirectionnelle en Français.
* **Speech-To-Text** : Commande vocale pour chercher un film ou exprimer une humeur.
* **Text-To-Speech** : L'assistant répond oralement à l'utilisateur.
* **Mood Analysis** : Détection automatique des émotions (Triste, Heureux, etc.).
* **Mood Journey** : Voyage cinématographique guidé selon l'état émotionnel.

### 🍿 Exploration de films
* Liste des films tendances et catalogue complet.
* Recherche textuelle en temps réel.
* Filtres avancés par catégories (Chips Material Design 3).
* Fiche détaillée avec **Shared Element Transitions** (animations fluides).
* Suggestion aléatoire intelligente.

### 🗺️ Services Avancés
* **Localisation & Maps** : Accueil personnalisé (Ville, Pays) avec détection automatique de la position.
* **Recherche de Séances Exactes** : Système d'Intent intelligent ouvrant Google Maps sur les cinémas diffusant **exactement** le film recherché (incluant les horaires des séances).
* **Scanner QR/Affiche** : Reconnaissance visuelle via **Google ML Kit** pour identifier un film.
* **Favoris** : Gestion complète synchronisée entre **Room (Local)** et **Supabase (Cloud)**.

## 🛠 Stack technique

| Couche | Technologie | Version |
| :--- | :--- | :--- |
| **Langage** | Java | 11 (LTS) |
| **Android SDK** | Target SDK | 36 (Android 15) |
| **UI Framework** | Material Design 3 | — |
| **Base de données Cloud** | Supabase (PostgreSQL) | — |
| **Base de données Locale** | Room (SQLite) | 2.6.1 |
| **IA (Audio)** | Android Speech Framework | — |
| **Vision (Scanner)** | Google ML Kit | 17.0.3 |
| **Localisation** | Google Play Services (Maps & Location) | 21.2.0 |
| **Réseau** | OkHttp | — |
| **Build Tool** | Gradle (Kotlin DSL) | 8.4 |

## 🏗 Architecture

```text
com.example.moviesapp_yasmine_part1/
├── 📁 database/          # Configuration Room/SQLite (AppDatabase, Dao, Entity)
├── 📁 activities/        # Activités (Main, Login, Register, Details...)
├── 📁 adapters/          # MyMovieAdapter (Rendu et filtrage)
├── 📁 repository/        # MovieRepository (Source de données)
├── 📁 manager/           # SupabaseManager (Auth et Cloud API)
├── 📁 models/            # MyMovieData (POJO)
└── 📄 MainActivity.java  # Point d'entrée principal
```

## 🚀 Installation locale

1. **Cloner le projet**
   ```bash
   git clone https://github.com/votre-repo/MoviesApp.git
   ```
2. **Ouvrir dans Android Studio**
   * Utilisez Android Studio Jellyfish ou plus récent.
3. **Configuration des Clés API**
   * Dans `AndroidManifest.xml`, remplacez `YOUR_GOOGLE_MAPS_API_KEY_HERE` par votre clé Google Maps.
   * Dans `SupabaseManager.java`, vérifiez que les credentials Supabase sont corrects.
4. **Lancer l'application**
   * Compilez et lancez sur un émulateur (avec Play Services) ou un appareil physique.

## 👤 Auteur
Développé avec ❤️ par **Yasmine OUARZANE**
