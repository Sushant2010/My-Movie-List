package garg.sushant.mymovielist;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import garg.sushant.mymovielist.rest.Movie;

public class MovieDetailActivity extends AppCompatActivity {

    static final String TAG = MovieDetailActivity.class.getSimpleName();

    private static int IMG_RESOURCE_IS_FAVORITE = R.drawable.ic_favorite_white_24dp;
    private static int IMG_RESOURCE_NOT_FAVORITE = R.drawable.ic_favorite_border_white_24dp;
    private static int IMG_RESOURCE_WATCHED = R.drawable.seen;
    private static int IMG_RESOURCE_NOT_WATCHED = R.drawable.notseen;
    private static int IMG_RESOURCE_TOWATCH = R.drawable.minus;
    private static int IMG_RESOURCE_NOT_TOWATCH = R.drawable.plus;

    public static final String KEY_MOVIE = "movie";

    Movie mMovie;

    boolean mIsFavorite = false;

    boolean mIsWatched = false;

    boolean mIsToWatch = false;

    @Bind(R.id.movie_detail_backdrop)
    ImageView mBackdropView;

    @Bind(R.id.fab_favorite)
    FloatingActionButton mFavoriteFab;

    @Bind(R.id.fab_watched)
    FloatingActionButton mWatchedFab;

    @Bind(R.id.fab_towatch)
    FloatingActionButton mToWatchFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mMovie = intent.getParcelableExtra(KEY_MOVIE);

        setStatusBarTransparent();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_MOVIE, mMovie);

        if (savedInstanceState == null ) {
            Fragment fragment = MovieDetailFragment.newInstance(mMovie);
            fragment.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.recycler_container, fragment).commit();
        }

        // bind data to the app bar
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        Picasso.with(this).load(mMovie.getBackdropUrl(screenWidth)).into(mBackdropView);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) this.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(mMovie.getTitle());
        mIsFavorite = MovieFavorites.isFavoriteMovie(this, mMovie.getId());
        mIsWatched = MovieWatched.isWatchedMovie(this, mMovie.getId());
        mIsToWatch = MovieToWatch.isToWatchMovie(this, mMovie.getId());




        // set image if favorite
        mFavoriteFab.setImageResource(getFavImageResourceId(mIsFavorite));

        mWatchedFab.setImageResource(getWatchedImageResourceId(mIsWatched));
        mToWatchFab.setImageResource(getToWatchImageResourceId(mIsToWatch));

        setupTransition();
    }

    public int getFavImageResourceId(boolean isFavorite) {
        if (isFavorite) {
//            Toast.makeText(getApplicationContext(), "Added to Favorites!",
//                    Toast.LENGTH_SHORT).show();
            return IMG_RESOURCE_IS_FAVORITE;
        }
        return IMG_RESOURCE_NOT_FAVORITE;
    }

    public int getWatchedImageResourceId(boolean isWatched) {
        if (isWatched) {
//            Toast.makeText(getApplicationContext(), "Added to Watched!",
//                    Toast.LENGTH_SHORT).show();
            return IMG_RESOURCE_WATCHED;
        }
        return IMG_RESOURCE_NOT_WATCHED;
    }

    public int getToWatchImageResourceId(boolean isToWatch) {
        if (isToWatch) {
//            Toast.makeText(getApplicationContext(), "Added to To-Watch!",
//                    Toast.LENGTH_SHORT).show();
            return IMG_RESOURCE_TOWATCH;
        }


        return IMG_RESOURCE_NOT_TOWATCH;

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupTransition() {
        getWindow().setEnterTransition(new Explode());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarTransparent() {
        getWindow().setStatusBarColor(Color.TRANSPARENT);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // handle back arrow in toolbar:
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onFBClick(View v) {
        switch(v.getId())
        {
            case R.id.fab_favorite:
               toggleFavorite();
                if (mIsFavorite) {
                    Toast.makeText(getApplicationContext(), "Added to Favorites!",
                            Toast.LENGTH_SHORT).show();}
                else {
                    Toast.makeText(getApplicationContext(), "Removed from Favorites!",
                            Toast.LENGTH_SHORT).show();}

                // Code for button 1 click
                break;

            case R.id.fab_watched:
                toggleWatched();
                if (mIsWatched) {
                    Toast.makeText(getApplicationContext(), "Added to Watched!",
                            Toast.LENGTH_SHORT).show();}
                else {
                    Toast.makeText(getApplicationContext(), "Removed from Watched!",
                            Toast.LENGTH_SHORT).show();}
                // Code for button 2 click
                break;


            case R.id.fab_towatch:
                toggleToWatch();
                if (mIsToWatch) {
                    Toast.makeText(getApplicationContext(), "Added to To Watch!",
                            Toast.LENGTH_SHORT).show();}
                else {
                    Toast.makeText(getApplicationContext(), "Removed from To Watch!",
                            Toast.LENGTH_SHORT).show();}
                // Code for button 3 click
                break;


        }
    }


    private void toggleFavorite() {
        mIsFavorite = !mIsFavorite;
        mFavoriteFab.setImageResource(getFavImageResourceId(mIsFavorite));
        MovieFavorites.updateFavorite(this, mIsFavorite, mMovie);
    }


    private void toggleWatched() {
        mIsWatched = !mIsWatched;
        mWatchedFab.setImageResource(getWatchedImageResourceId(mIsWatched));
        MovieWatched.updateWatched(this, mIsWatched, mMovie);
    }


    private void toggleToWatch() {
        mIsToWatch = !mIsToWatch;
        mToWatchFab.setImageResource(getToWatchImageResourceId(mIsToWatch));
        MovieToWatch.updateToWatch(this, mIsToWatch, mMovie);
    }
}
