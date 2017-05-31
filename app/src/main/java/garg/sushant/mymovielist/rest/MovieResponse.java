package garg.sushant.mymovielist.rest;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class MovieResponse extends Movie {

    @SerializedName("genres")
    List<Genre> genres;

    @Override
    public int[] getGenreIds() {
        int[] ids = new int[genres.size()];
        for (int i=0; i<genres.size(); i++) {
            ids[i] = genres.get(i).id;
        }
        return ids;
    }

    /**
     * Extracts the movie from the response (discarding this wrapper type).
     * Sets any fields on the base type (ie, genre_ids).
     */
    public Movie getMovie() {
        this.setGenreIds(getGenreIds());
        return this;
    }

    static class Genre {
        int id;
        String name;
    }
}
