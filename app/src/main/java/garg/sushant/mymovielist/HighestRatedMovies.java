package garg.sushant.mymovielist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import garg.sushant.mymovielist.rest.Movie;

public class HighestRatedMovies extends AppCompatActivity implements  HighestRatedMoviePosterGridFragment.OnMovieSelectedListener {

    static final String TAG = HighestRatedMovies.class.getSimpleName();

    private static final String KEY_MOVIE = "movie";

    private static final String TAG_DETAIL_FRAGMENT = "fragment_details";

    boolean mIsFavorite = false;

    boolean mIsWatched = false;

    boolean mIsToWatch = false;

    boolean mTwoPaneMode = false;

    // remember the selected movie
    Movie mSelectedMovie;

    @Nullable
    @Bind(R.id.fab_favorite)
    FloatingActionButton mFavoriteFab;

    @Nullable
    @Bind(R.id.fab_watched)
    FloatingActionButton mWatchedFab;

    @Nullable
    @Bind(R.id.fab_towatch)
    FloatingActionButton mToWatchFab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.highestrated_main);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //if (toolbar != null) {
           // setSupportActionBar(toolbar);
            toolbar.setTitle("Top Rated");
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),TrendingMovies.class));
            }
        });

        //getSupportActionBar().setDisplayShowTitleEnabled(true);
            //toolbar.setNavigationIcon(R.mipmap.ic_launcher);
       // }


        if (savedInstanceState != null) {
            mSelectedMovie = savedInstanceState.getParcelable(KEY_MOVIE);
            mIsFavorite = MovieFavorites.isFavoriteMovie(this, mSelectedMovie.getId());
            mIsWatched = MovieWatched.isWatchedMovie(this, mSelectedMovie.getId());
            mIsToWatch = MovieToWatch.isToWatchMovie(this, mSelectedMovie.getId());

        }

        if (findViewById(R.id.content_split) != null) {
            mTwoPaneMode = true;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       // getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings: {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_MOVIE, mSelectedMovie);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();

//        if (getSupportFragmentManager().findFragmentByTag(TAG_DETAIL_FRAGMENT) != null) {
//            MovieDetailFragment fragment = (MovieDetailFragment)
//                  getSupportFragmentManager().findFragmentByTag(TAG_DETAIL_FRAGMENT);
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            transaction.remove(fragment).commit();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mSelectedMovie != null && findViewById(R.id.movie_detail_title) != null) {
            onMovieSelected(mSelectedMovie, false, null);
        }
    }

    @Override
    public void onMovieSelected(Movie movie, boolean onClick, View srcView) {
        //Log.d(TAG, "Show movie details: " + movie.getTitle() + " mTwoPaneMode=" + mTwoPaneMode + " id=" + movie.getId());

        mSelectedMovie = movie;
        mIsFavorite = mSelectedMovie == null ? false : MovieFavorites.isFavoriteMovie(this, mSelectedMovie.getId());
        mIsWatched = mSelectedMovie == null ? false : MovieWatched.isWatchedMovie(this, mSelectedMovie.getId());

        mIsToWatch = mSelectedMovie == null ? false : MovieToWatch.isToWatchMovie(this, mSelectedMovie.getId());


        if (mTwoPaneMode) {
            MovieDetailFragment fragment = (MovieDetailFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_detail);

            if (fragment != null & movie == null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.remove(fragment).commit();
            } else if (fragment == null || fragment.mMovie.getId() != mSelectedMovie.getId()) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(MovieDetailFragment.KEY_MOVIE, movie);
                fragment = MovieDetailFragment.newInstance(movie);
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if (srcView != null) {
                    Log.d(TAG, "Fragment with transition??");
                    transaction.addSharedElement(srcView, getResources().getString(R.string.transition_poster));
                }
                transaction.replace(R.id.fragment_detail, fragment, TAG_DETAIL_FRAGMENT).commit();
                mFavoriteFab.setImageResource(MovieFavorites.getImageResourceId(mIsFavorite));
                mWatchedFab.setImageResource(MovieWatched.getImageResourceId(mIsWatched));
                mToWatchFab.setImageResource(MovieToWatch.getImageResourceId(mIsToWatch));

            }

            String title = movie == null ? "" : movie.getTitle();
            TextView titleView = (TextView) findViewById(R.id.movie_detail_title);
            titleView.setText(title);
            mFavoriteFab.show();
            mWatchedFab.show();
            mToWatchFab.show();

        } else if (onClick) {
            onMovieClicked(movie, true, srcView);
//            Intent intent = new Intent(this, MovieDetailActivity.class);
//            intent.putExtra(MovieDetailActivity.KEY_MOVIE, movie);
//            this.startActivity(intent);
        }
    }

    public void onMovieClicked(Movie movie, boolean onClick, View srcView) {
        Log.d(TAG, "Start Activity with transition??");
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(MovieDetailActivity.KEY_MOVIE, movie);

        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, srcView, getResources().getString(R.string.transition_poster));
        ActivityCompat.startActivity(this, intent, options.toBundle());
        //this.startActivity(intent, options);
    }

    @Nullable
    @OnClick(R.id.fab_favorite) void onFavoriteClicked() {
        toggleFavorite();
    }

    @Nullable
    @OnClick(R.id.fab_watched) void onWatchedClicked(){
        toggleWatched();
    }

    @Nullable
    @OnClick(R.id.fab_towatch) void onToWatchClicked(){
        toggleToWatch();
    }

    private void toggleWatched() {
        mIsWatched = !mIsWatched;
        mWatchedFab.setImageResource(MovieWatched.getImageResourceId(mIsWatched));
        if (mSelectedMovie != null){
            MovieWatched.updateWatched(this, mIsWatched, mSelectedMovie);
        }

        if (mTwoPaneMode && !mIsWatched){
            HighestRatedMoviePosterGridFragment gridFragment = (HighestRatedMoviePosterGridFragment) getSupportFragmentManager().findFragmentById(R.id.highestrated_fragment_grid);
            gridFragment.removeMovie(mSelectedMovie);
        }
    }
    private void toggleFavorite() {
        mIsFavorite = !mIsFavorite;
        mFavoriteFab.setImageResource(MovieFavorites.getImageResourceId(mIsFavorite));
        if (mSelectedMovie != null) {
            MovieFavorites.updateFavorite(this, mIsFavorite, mSelectedMovie);
        }
        // refresh after movie unfavorited
        if (mTwoPaneMode && !mIsFavorite) {
            HighestRatedMoviePosterGridFragment gridFragment = (HighestRatedMoviePosterGridFragment) getSupportFragmentManager().findFragmentById(R.id.highestrated_fragment_grid);
            gridFragment.removeMovie(mSelectedMovie);
        }
    }

    private void toggleToWatch() {
        mIsToWatch = !mIsToWatch;
        mToWatchFab.setImageResource(MovieToWatch.getImageResourceId(mIsToWatch));
        if (mSelectedMovie != null) {
            MovieToWatch.updateToWatch(this, mIsToWatch, mSelectedMovie);
        }
        // refresh after movie unfavorited
        if (mTwoPaneMode && !mIsToWatch) {
            HighestRatedMoviePosterGridFragment gridFragment = (HighestRatedMoviePosterGridFragment) getSupportFragmentManager().findFragmentById(R.id.highestrated_fragment_grid);
            gridFragment.removeMovie(mSelectedMovie);
        }
    }

}
