package garg.sushant.mymovielist;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import garg.sushant.mymovielist.data.MovieContract;
import garg.sushant.mymovielist.rest.Movie;

/**
 * Created by Sushant on 4/9/2017.
 */

public final class MovieWatched {

    private static int IMG_RESOURCE_WATCHED = R.drawable.ic_favorite_white_24dp;
    private static int IMG_RESOURCE_NOT_WATCHED = R.drawable.ic_favorite_border_white_24dp;

    private static final String TAG = MovieWatched.class.getSimpleName();

    static final String PREF_FILE_NAME = "WatchedMovies";

    static List<Integer> sWatchedMovies;

    private static void loadWatchedMovies(Context context) {
        if (sWatchedMovies == null) {
            sWatchedMovies = new ArrayList<Integer>();
            SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
            synchronized (sharedPreferences) {
                Set<String> keys = sharedPreferences.getAll().keySet();
                for (String key : keys) {
                    sWatchedMovies.add(sharedPreferences.getInt(key, -1));
                }
            }
        }
    }

    private static void saveWatchedMoviePreference(Context context, int movieId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("id-" + movieId, movieId).commit();
    }

    private static void removeWatchedMoviePreference(Context context, int movieId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("id-" + movieId).commit();
    }

    public static List<Integer> getWatchedMovies(Context context) {
        loadWatchedMovies(context);
        return sWatchedMovies;
    }

    public static boolean isWatchedMovie(Context context, int movieId) {
        loadWatchedMovies(context);
        return sWatchedMovies.contains(movieId);
    }

    public static void addWatchedMovie(Context context, int movieId) {
        loadWatchedMovies(context);
        if (!sWatchedMovies.contains(movieId)) {
            sWatchedMovies.add(movieId);
            saveWatchedMoviePreference(context, movieId);
        }
    }

    public static void removeWatchedMovie(Context context, int movieId) {
        loadWatchedMovies(context);
        if (sWatchedMovies.contains(movieId)) {
            sWatchedMovies.remove(sWatchedMovies.indexOf(movieId));
            removeWatchedMoviePreference(context, movieId);
        }
    }

    public static void updateWatched(Context context, boolean isWatched, Movie movie) {
        if (isWatched) {
            addWatchedMovie(context, movie.getId());
            addWatched(context, movie);
        } else {
            removeWatchedMovie(context, movie.getId());
            removeWatched(context, movie);
        }
    }

    public static void addWatched(Context context, Movie movie) {
        Log.d(TAG, "Inserting watched: " + movie.getTitle());

        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
        values.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
        values.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, movie.getOriginalTitle());
        values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
        values.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, movie.getVoteCount());
        values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        values.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, movie.getBackdropPath());
        values.put(MovieContract.MovieEntry.COLUMN_POPULARITY, movie.getPopularity());
        values.put(MovieContract.MovieEntry.COLUMN_WATCHED, 1);
        values.put(MovieContract.MovieEntry.COLUMN_LANGUAGE, movie.getOriginalLanguage());
        values.put(MovieContract.MovieEntry.COLUMN_VIDEO, movie.isVideo());
        values.put(MovieContract.MovieEntry.COLUMN_ADULT, movie.isAdult());

        final AsyncQueryHandler handler = new MovieWatched.AsyncCrudHandler(context.getContentResolver());
        handler.startInsert(2, null, MovieContract.MovieEntry.CONTENT_URI, values);

        // update genres
        insertGenres(context, movie.getId(), movie.getGenreIds());
    }

    protected static void insertGenres(final Context context, int movieId, int[] genreIds) {

        final ContentValues[] allValues = new ContentValues[genreIds.length];
        for (int i=0; i<genreIds.length; i++) {
            ContentValues values = new ContentValues();
            values.put(MovieContract.MovieGenreEntry.COLUMN_MOVIE_ID, movieId);
            values.put(MovieContract.MovieGenreEntry.COLUMN_GENRE_ID, genreIds[i]);
            allValues[i] = values;
        }

        // wtf: no bulkInsert for AsyncQueryHandler
        new Thread(new Runnable() {
            public void run() {
                context.getContentResolver().bulkInsert(MovieContract.MovieGenreEntry.CONTENT_URI, allValues);
            }
        }).start();
    }


    protected static void deleteGenres(Context context, int movidId) {
        final AsyncQueryHandler handler = new MovieFavorites.AsyncCrudHandler(context.getContentResolver());
        handler.startDelete(3, null,
                MovieContract.MovieGenreEntry.CONTENT_URI,
                MovieContract.MovieGenreEntry.WHERE_MOVIE_ID,
                new String[] {""+movidId});
    }

    public static void removeWatched(Context context, Movie movie) {
        Log.d(TAG, "Removing watched: " + movie.getTitle());
        final AsyncQueryHandler handler = new MovieWatched.AsyncCrudHandler(context.getContentResolver());
        handler.startDelete(2, null,
                MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.WHERE_MOVIE_ID,
                new String[]{"" + movie.getId()});
    }


    public static int getImageResourceId(boolean isWatched) {
        if (isWatched) {
            return IMG_RESOURCE_WATCHED;
        }
        return IMG_RESOURCE_NOT_WATCHED;
    }



    static class AsyncCrudHandler extends AsyncQueryHandler {
        public AsyncCrudHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onInsertComplete(int token, Object cookie, Uri uri) {
            super.onInsertComplete(token, cookie, uri);
            Log.d(TAG, "Insert completed for uri: " + uri);
        }

        @Override
        protected void onDeleteComplete(int token, Object cookie, int result) {
            super.onDeleteComplete(token, cookie, result);
            Log.d(TAG, "Delete completed with result: " + result);
        }
    }


}
