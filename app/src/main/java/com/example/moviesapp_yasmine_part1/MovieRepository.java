package com.example.moviesapp_yasmine_part1;

import java.util.ArrayList;
import java.util.List;

public class MovieRepository {
    public static MyMovieData[] getAllMovies() {
        return new MyMovieData[]{
            // --- MOOD: HAPPY (20 films) ---
            new MyMovieData("Avengers: Endgame", "2019", R.drawable.avenger, 8.4f, "Action", "La fin épique de la saga Infinity.", "HAPPY"),
            new MyMovieData("Jumanji: Next Level", "2019", R.drawable.jumanji, 7.0f, "Adventure", "Le jeu a changé, l'aventure continue.", "HAPPY"),
            new MyMovieData("Toy Story 4", "2019", R.drawable.jumanji, 7.7f, "Animation", "Une nouvelle vie pour Woody.", "HAPPY"),
            new MyMovieData("Deadpool", "2016", R.drawable.hulk, 8.0f, "Action", "L'anti-héros le plus drôle de Marvel.", "HAPPY"),
            new MyMovieData("Guardians of the Galaxy", "2014", R.drawable.avenger, 8.0f, "Sci-Fi", "Une bande de marginaux sauve l'univers.", "HAPPY"),
            new MyMovieData("The Mask", "1994", R.drawable.hulk, 6.9f, "Comedy", "Un masque magique transforme un employé.", "HAPPY"),
            new MyMovieData("Spider-Man: Homecoming", "2017", R.drawable.avenger, 7.4f, "Action", "Peter Parker découvre ses pouvoirs.", "HAPPY"),
            new MyMovieData("Shrek", "2001", R.drawable.jumanji, 7.9f, "Animation", "Un ogre devient un héros malgré lui.", "HAPPY"),
            new MyMovieData("Men in Black", "1997", R.drawable.venom, 7.3f, "Sci-Fi", "Ils protègent la terre des aliens.", "HAPPY"),
            new MyMovieData("Back to the Future", "1985", R.drawable.avatar, 8.5f, "Sci-Fi", "Voyage temporel culte.", "HAPPY"),
            new MyMovieData("Ghostbusters", "1984", R.drawable.hulk, 7.8f, "Comedy", "Chasseurs de fantômes à New York.", "HAPPY"),
            new MyMovieData("The Incredibles", "2004", R.drawable.avenger, 8.0f, "Animation", "Une famille de super-héros.", "HAPPY"),
            new MyMovieData("Ice Age", "2002", R.drawable.jumanji, 7.5f, "Animation", "Une aventure préhistorique.", "HAPPY"),
            new MyMovieData("Zootopia", "2016", R.drawable.avatar, 8.0f, "Animation", "Une ville d'animaux moderne.", "HAPPY"),
            new MyMovieData("Despicable Me", "2010", R.drawable.jumanji, 7.6f, "Animation", "Gru veut voler la lune.", "HAPPY"),
            new MyMovieData("The Lego Movie", "2014", R.drawable.avenger, 7.7f, "Animation", "Tout est super génial !", "HAPPY"),
            new MyMovieData("Home Alone", "1990", R.drawable.good_deeds, 7.7f, "Comedy", "Kevin défend sa maison.", "HAPPY"),
            new MyMovieData("Ratatouille", "2007", R.drawable.avatar, 8.1f, "Animation", "Un rat qui sait cuisiner.", "HAPPY"),
            new MyMovieData("Finding Nemo", "2003", R.drawable.avatar, 8.2f, "Animation", "Un père cherche son fils.", "HAPPY"),
            new MyMovieData("Kung Fu Panda", "2008", R.drawable.hulk, 7.6f, "Animation", "Po devient le Guerrier Dragon.", "HAPPY"),

            // --- MOOD: SAD (20 films) ---
            new MyMovieData("Batman: The Dark Knight", "2008", R.drawable.batman, 9.0f, "Action", "Le Joker sème le chaos à Gotham.", "SAD"),
            new MyMovieData("Schindler's List", "1993", R.drawable.good_deeds, 9.0f, "Drama", "Un homme sauve des vies pendant la guerre.", "SAD"),
            new MyMovieData("The Green Mile", "1999", R.drawable.good_deeds, 8.6f, "Drama", "Un miracle dans un couloir de la mort.", "SAD"),
            new MyMovieData("Titanic", "1997", R.drawable.avatar, 7.9f, "Romance", "Une tragédie sur le paquebot insubmersible.", "SAD"),
            new MyMovieData("Logan", "2017", R.drawable.hulk, 8.1f, "Action", "La dernière mission de Wolverine.", "SAD"),
            new MyMovieData("Gladiator", "2000", R.drawable.batman, 8.5f, "Action", "Un général devenu esclave se venge.", "SAD"),
            new MyMovieData("The Lion King", "1994", R.drawable.jumanji, 8.5f, "Animation", "Simba perd son père et doit régner.", "SAD"),
            new MyMovieData("Interstellar", "2014", R.drawable.avatar, 8.7f, "Sci-Fi", "Un voyage émotionnel dans l'espace.", "SAD"),
            new MyMovieData("Joker", "2019", R.drawable.batman, 8.4f, "Drama", "La naissance d'un méchant mythique.", "SAD"),
            new MyMovieData("Saving Private Ryan", "1998", R.drawable.avenger, 8.6f, "Action", "Une mission de sauvetage en pleine guerre.", "SAD"),
            new MyMovieData("Up", "2009", R.drawable.avatar, 8.3f, "Animation", "Une maison volante et une perte triste.", "SAD"),
            new MyMovieData("Hachi: A Dog's Tale", "2009", R.drawable.good_deeds, 8.1f, "Drama", "La fidélité d'un chien déchirante.", "SAD"),
            new MyMovieData("A Star Is Born", "2018", R.drawable.good_deeds, 7.6f, "Drama", "Une romance musicale tragique.", "SAD"),
            new MyMovieData("Braveheart", "1995", R.drawable.batman, 8.3f, "Action", "La liberté de l'Ecosse au prix fort.", "SAD"),
            new MyMovieData("The Pursuit of Happyness", "2006", R.drawable.good_deeds, 8.0f, "Drama", "Un père lutte pour survivre.", "SAD"),
            new MyMovieData("La Vita è Bella", "1997", R.drawable.good_deeds, 8.6f, "Drama", "L'humour face à l'horreur.", "SAD"),
            new MyMovieData("The Pianist", "2002", R.drawable.good_deeds, 8.5f, "Drama", "Un musicien juif dans Varsovie.", "SAD"),
            new MyMovieData("Moonlight", "2016", R.drawable.avatar, 7.4f, "Drama", "Le parcours d'un jeune homme noir.", "SAD"),
            new MyMovieData("Requiem for a Dream", "2000", R.drawable.venom, 8.3f, "Drama", "La spirale destructrice de la drogue.", "SAD"),
            new MyMovieData("Brokeback Mountain", "2005", R.drawable.avatar, 7.7f, "Romance", "Une histoire d'amour interdite.", "SAD"),

            // --- MOOD: STRESSED (20 films) ---
            new MyMovieData("Venom: Let There Be Carnage", "2021", R.drawable.venom, 6.0f, "Action", "Le symbiote face à Carnage.", "STRESSED"),
            new MyMovieData("Hulk", "2003", R.drawable.hulk, 5.6f, "Sci-Fi", "La rage de Bruce Banner.", "STRESSED"),
            new MyMovieData("Mad Max: Fury Road", "2015", R.drawable.hulk, 8.1f, "Action", "Une course-poursuite infernale.", "STRESSED"),
            new MyMovieData("Inception", "2010", R.drawable.avatar, 8.8f, "Sci-Fi", "Le vol de rêves sous tension.", "STRESSED"),
            new MyMovieData("John Wick", "2014", R.drawable.batman, 7.4f, "Action", "Un tueur sort de sa retraite.", "STRESSED"),
            new MyMovieData("Extraction", "2020", R.drawable.avenger, 6.7f, "Action", "Une extraction sous feu nourri.", "STRESSED"),
            new MyMovieData("A Quiet Place", "2018", R.drawable.batman, 7.5f, "Thriller", "Ne faites aucun bruit.", "STRESSED"),
            new MyMovieData("The Conjuring", "2013", R.drawable.batman, 7.5f, "Horror", "Une maison hantée terrifiante.", "STRESSED"),
            new MyMovieData("Saw", "2004", R.drawable.venom, 7.6f, "Horror", "Jouez ou mourrez.", "STRESSED"),
            new MyMovieData("Mission Impossible", "1996", R.drawable.avenger, 7.1f, "Action", "Espionnage et adrénaline.", "STRESSED"),
            new MyMovieData("World War Z", "2013", R.drawable.venom, 7.0f, "Horror", "Zombies à l'échelle mondiale.", "STRESSED"),
            new MyMovieData("The Raid", "2011", R.drawable.hulk, 7.6f, "Action", "Bataille dans un immeuble.", "STRESSED"),
            new MyMovieData("Oldboy", "2003", R.drawable.batman, 8.4f, "Thriller", "Vengeance après 15 ans.", "STRESSED"),
            new MyMovieData("Shutter Island", "2010", R.drawable.batman, 8.2f, "Thriller", "Une enquête psychiatrique folle.", "STRESSED"),
            new MyMovieData("Gravity", "2013", R.drawable.avatar, 7.7f, "Sci-Fi", "Perdus dans l'immensité de l'espace.", "STRESSED"),
            new MyMovieData("Predator", "1987", R.drawable.hulk, 7.8f, "Action", "Traqués par un alien.", "STRESSED"),
            new MyMovieData("Alien", "1979", R.drawable.venom, 8.5f, "Sci-Fi", "Dans l'espace, personne n'entend crier.", "STRESSED"),
            new MyMovieData("The Matrix", "1999", R.drawable.avatar, 8.7f, "Sci-Fi", "La réalité est une simulation.", "STRESSED"),
            new MyMovieData("Fight Club", "1999", R.drawable.batman, 8.8f, "Drama", "La première règle du club...", "STRESSED"),
            new MyMovieData("Unhinged", "2020", R.drawable.hulk, 6.0f, "Thriller", "Rage au volant mortelle.", "STRESSED"),

            // --- MOOD: RELAXED (20 films) ---
            new MyMovieData("Avatar", "2009", R.drawable.avatar, 7.8f, "Sci-Fi", "Pandora et ses paysages.", "RELAXED"),
            new MyMovieData("The Martian", "2015", R.drawable.avatar, 8.0f, "Sci-Fi", "Seul sur Mars, mais optimiste.", "RELAXED"),
            new MyMovieData("Soul", "2020", R.drawable.jumanji, 8.0f, "Animation", "Voyage sur le sens de la vie.", "RELAXED"),
            new MyMovieData("Amélie Poulain", "2001", R.drawable.avatar, 8.3f, "Comedy", "Le destin fabuleux d'Amélie.", "RELAXED"),
            new MyMovieData("The Secret Life of Walter Mitty", "2013", R.drawable.avatar, 7.3f, "Adventure", "S'évader du quotidien.", "RELAXED"),
            new MyMovieData("Chef", "2014", R.drawable.jumanji, 7.3f, "Comedy", "Un food truck et du bonheur.", "RELAXED"),
            new MyMovieData("The Grand Budapest Hotel", "2014", R.drawable.avatar, 8.1f, "Comedy", "Un style visuel apaisant.", "RELAXED"),
            new MyMovieData("Ponyo", "2008", R.drawable.avatar, 7.6f, "Animation", "Une petite sirène de Ghibli.", "RELAXED"),
            new MyMovieData("Midnight in Paris", "2011", R.drawable.avatar, 7.7f, "Romance", "Paris à travers les époques.", "RELAXED"),
            new MyMovieData("Dune", "2021", R.drawable.avatar, 8.0f, "Sci-Fi", "Contemplation désertique.", "RELAXED"),
            new MyMovieData("Forrest Gump", "1994", R.drawable.good_deeds, 8.8f, "Drama", "La vie est une boîte de chocolats.", "RELAXED"),
            new MyMovieData("The Terminal", "2004", R.drawable.avatar, 7.4f, "Drama", "Bloqué dans un aéroport.", "RELAXED"),
            new MyMovieData("Paddington 2", "2017", R.drawable.jumanji, 7.8f, "Comedy", "L'ours le plus poli du monde.", "RELAXED"),
            new MyMovieData("WALL-E", "2008", R.drawable.avatar, 8.4f, "Animation", "Un petit robot nettoyeur.", "RELAXED"),
            new MyMovieData("Little Miss Sunshine", "2006", R.drawable.jumanji, 7.8f, "Comedy", "Un road trip familial décalé.", "RELAXED"),
            new MyMovieData("Spirited Away", "2001", R.drawable.avatar, 8.6f, "Animation", "Le chef d'oeuvre de Miyazaki.", "RELAXED"),
            new MyMovieData("Big Fish", "2003", R.drawable.avatar, 8.0f, "Adventure", "Les contes de fées d'un père.", "RELAXED"),
            new MyMovieData("Inside Out", "2015", R.drawable.jumanji, 8.1f, "Animation", "Voyage dans les émotions.", "RELAXED"),
            new MyMovieData("Her", "2013", R.drawable.avatar, 8.0f, "Sci-Fi", "Amour avec une IA.", "RELAXED"),
            new MyMovieData("Eat Pray Love", "2010", R.drawable.good_deeds, 5.8f, "Romance", "Voyage initiatique.", "RELAXED"),

            // --- MOOD: ROMANTIC (20 films) ---
            new MyMovieData("Good Deeds", "2012", R.drawable.good_deeds, 5.5f, "Romance", "Une rencontre imprévue.", "ROMANTIC"),
            new MyMovieData("The Notebook", "2004", R.drawable.good_deeds, 7.8f, "Romance", "Un amour qui traverse le temps.", "ROMANTIC"),
            new MyMovieData("La La Land", "2016", R.drawable.avatar, 8.0f, "Romance", "Musique et passion à LA.", "ROMANTIC"),
            new MyMovieData("About Time", "2013", R.drawable.avatar, 7.8f, "Romance", "Si on pouvait voyager dans le temps.", "ROMANTIC"),
            new MyMovieData("Pride & Prejudice", "2005", R.drawable.good_deeds, 7.8f, "Romance", "Elizabeth et Mr Darcy.", "ROMANTIC"),
            new MyMovieData("Pretty Woman", "1990", R.drawable.good_deeds, 7.1f, "Romance", "Une histoire d'amour moderne.", "ROMANTIC"),
            new MyMovieData("To All the Boys I've Loved Before", "2018", R.drawable.good_deeds, 7.0f, "Romance", "Des lettres secrètes révélées.", "ROMANTIC"),
            new MyMovieData("The Fault in Our Stars", "2014", R.drawable.good_deeds, 7.7f, "Romance", "Nos étoiles contraires.", "ROMANTIC"),
            new MyMovieData("Before Sunrise", "1995", R.drawable.avatar, 8.1f, "Romance", "Une nuit à Vienne.", "ROMANTIC"),
            new MyMovieData("Me Before You", "2016", R.drawable.good_deeds, 7.4f, "Romance", "Vivre avec intensité.", "ROMANTIC"),
            new MyMovieData("Notting Hill", "1999", R.drawable.good_deeds, 7.2f, "Romance", "Un libraire et une star.", "ROMANTIC"),
            new MyMovieData("Beauty and the Beast", "2017", R.drawable.avatar, 7.1f, "Romance", "Conte de fées en live action.", "ROMANTIC"),
            new MyMovieData("Crazy Rich Asians", "2018", R.drawable.good_deeds, 6.9f, "Romance", "Amour et traditions à Singapour.", "ROMANTIC"),
            new MyMovieData("Twilight", "2008", R.drawable.batman, 5.3f, "Romance", "Un amour de vampire.", "ROMANTIC"),
            new MyMovieData("Fifty Shades of Grey", "2015", R.drawable.batman, 4.1f, "Romance", "Passion et contrôle.", "ROMANTIC"),
            new MyMovieData("Call Me by Your Name", "2017", R.drawable.avatar, 7.8f, "Romance", "Un été en Italie.", "ROMANTIC"),
            new MyMovieData("Ghost", "1990", R.drawable.batman, 7.1f, "Romance", "L'amour au-delà de la mort.", "ROMANTIC"),
            new MyMovieData("P.S. I Love You", "2007", R.drawable.good_deeds, 7.0f, "Romance", "Des lettres pour continuer à vivre.", "ROMANTIC"),
            new MyMovieData("The Vow", "2012", R.drawable.good_deeds, 6.8f, "Romance", "Retomber amoureux.", "ROMANTIC"),
            new MyMovieData("Silver Linings Playbook", "2012", R.drawable.good_deeds, 7.7f, "Romance", "Deux âmes tourmentées.", "ROMANTIC")
        };
    }

    public static List<MyMovieData> getRecommendations(String genre, String mood, String currentMovieName) {
        List<MyMovieData> recommendations = new ArrayList<>();
        // Priority 1: Same Genre AND Same Mood
        for (MyMovieData movie : getAllMovies()) {
            if (movie.getGenre().equals(genre) && movie.getMood().equalsIgnoreCase(mood) && !movie.getMovieName().equals(currentMovieName)) {
                recommendations.add(movie);
                if (recommendations.size() >= 5) return recommendations;
            }
        }
        // Priority 2: Just Same Mood
        for (MyMovieData movie : getAllMovies()) {
            if (movie.getMood().equalsIgnoreCase(mood) && !movie.getMovieName().equals(currentMovieName) && !recommendations.contains(movie)) {
                recommendations.add(movie);
                if (recommendations.size() >= 5) return recommendations;
            }
        }
        // Priority 3: Just Same Genre
        for (MyMovieData movie : getAllMovies()) {
            if (movie.getGenre().equals(genre) && !movie.getMovieName().equals(currentMovieName) && !recommendations.contains(movie)) {
                recommendations.add(movie);
                if (recommendations.size() >= 5) return recommendations;
            }
        }
        return recommendations;
    }

    public static List<MyMovieData> getMoviesByMood(String mood) {
        List<MyMovieData> filteredMovies = new ArrayList<>();
        for (MyMovieData movie : getAllMovies()) {
            if (movie.getMood().equalsIgnoreCase(mood)) {
                filteredMovies.add(movie);
            }
        }
        return filteredMovies;
    }
}
