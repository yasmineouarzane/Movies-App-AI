package com.example.moviesapp_yasmine_part1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MyMovieAdapter extends RecyclerView.Adapter<MyMovieAdapter.ViewHolder> implements Filterable {
    private List<MyMovieData> movieDataList;
    private List<MyMovieData> movieDataListFull;
    private Context context;
    private OnItemClickListener listener;
    private boolean isHorizontal;

    public interface OnItemClickListener {
        void onItemClick(MyMovieData movie, ImageView movieImageView);
    }

    public MyMovieAdapter(MyMovieData[] myMovieData, Context context) {
        this(myMovieData, context, false);
    }

    public MyMovieAdapter(MyMovieData[] myMovieData, Context context, boolean isHorizontal) {
        this.movieDataList = new ArrayList<>();
        for (MyMovieData data : myMovieData) {
            this.movieDataList.add(data);
        }
        this.movieDataListFull = new ArrayList<>(movieDataList);
        this.context = context;
        this.isHorizontal = isHorizontal;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = isHorizontal ? R.layout.movie_card_item : R.layout.movie_item_list;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final MyMovieData movie = movieDataList.get(position);
        holder.textViewName.setText(movie.getMovieName());
        if (!isHorizontal && holder.textViewDate != null) {
            holder.textViewDate.setText(movie.getMovieDate());
            if (holder.ratingBar != null) {
                holder.ratingBar.setRating(movie.getRating() / 2f); // Rating is out of 10, scale to 5
            }
        }
        holder.movieImage.setImageResource(movie.getMovieImage());
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(movie, holder.movieImage);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieDataList.size();
    }

    @Override
    public Filter getFilter() {
        return movieFilter;
    }

    private Filter movieFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<MyMovieData> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(movieDataListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (MyMovieData item : movieDataListFull) {
                    if (item.getMovieName().toLowerCase().contains(filterPattern) || 
                        (item.getGenre() != null && item.getGenre().toLowerCase().contains(filterPattern))) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            movieDataList.clear();
            movieDataList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public void filterByGenre(String genre) {
        List<MyMovieData> filteredList = new ArrayList<>();
        if (genre.equalsIgnoreCase("All")) {
            filteredList.addAll(movieDataListFull);
        } else {
            for (MyMovieData item : movieDataListFull) {
                if (item.getGenre() != null && item.getGenre().equalsIgnoreCase(genre)) {
                    filteredList.add(item);
                }
            }
        }
        movieDataList.clear();
        movieDataList.addAll(filteredList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView movieImage;
        TextView textViewName;
        TextView textViewDate;
        android.widget.RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Support both layouts
            movieImage = itemView.findViewById(isHorizontal ? R.id.cardImageView : R.id.imageview);
            textViewName = itemView.findViewById(isHorizontal ? R.id.cardTitle : R.id.textName);
            if (!isHorizontal) {
                textViewDate = itemView.findViewById(R.id.textdate);
                ratingBar = itemView.findViewById(R.id.ratingBarSmall);
            }
        }
    }
}
