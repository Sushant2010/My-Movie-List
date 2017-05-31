package garg.sushant.mymovielist;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.Bind;
import garg.sushant.mymovielist.rest.Movie;


public class NavigationDrawerActivity extends GoogleSignInActivity
        implements NavigationView.OnNavigationItemSelectedListener, MoviePosterGridFragment.OnMovieSelectedListener,
        HighestRatedMoviePosterGridFragment.OnMovieSelectedListener, UpcomingMoviesPosterGridFragment.OnMovieSelectedListener,
        InTheatresMoviePosterGridFragment.OnMovieSelectedListener, SearchMovieFragment.OnMovieSelectedListener {

    private FragmentManager fragmentManager;

    private ImageView mDisplayImageView;
    private TextView mNameTextView;
    private TextView mEmailTextView;
    ImageView imgBackground;
    NavigationView navigationView;
    private static int RESULT_LOAD_IMG = 1;
    MoviePosterGridFragment.MovieGridRecyclerAdapter md1;
    ArrayList<Movie> movies = new ArrayList<Movie>();
    MoviePosterGridFragment.MovieGridRecyclerAdapter md;
    String imgDecodableString;
    BitmapDrawable drawable;

    private static final String IMAGE_DATA = "image_resource";

    private FirebaseAuth auth;
    private Fragment fragment = null;

    static final String TAG = TrendingMovies.class.getSimpleName();
    private static final String KEY_MOVIE = "movie";
    private static final String TAG_DETAIL_FRAGMENT = "fragment_details";

    boolean mIsFavorite = false;
    boolean mIsWatched = false;
    boolean mIsToWatch = false;
    boolean mTwoPaneMode = false;

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
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Trending");
        setSupportActionBar(toolbar);
        auth = FirebaseAuth.getInstance();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragment = new MoviePosterGridFragment();
        fragmentTransaction.replace(R.id.main_container_wrapper, fragment);
        fragmentTransaction.commit();


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        imgBackground = (ImageView) findViewById(R.id.imageView_display);


        if (savedInstanceState != null) {
            Bitmap tmp = savedInstanceState.getParcelable(IMAGE_DATA);
            if(tmp != null) {
                drawable = new BitmapDrawable(getResources(), tmp);
                imgBackground.setImageDrawable(drawable);
            }
        }

       mDisplayImageView = (ImageView) navigationView.findViewById(R.id.imageView_display);
       mNameTextView = (TextView) navigationView.findViewById(R.id.textView_name);
       mEmailTextView = (TextView) navigationView.findViewById(R.id.textView_email);

        if (auth.getCurrentUser() != null) {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            mNameTextView.setText(user.getDisplayName());
            mEmailTextView.setText(user.getEmail());
        }

        FirebaseDatabase.getInstance().getReference(Constants.USER_KEY).child(mFirebaseUser.getEmail().replace(".",","))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null){
                            Users users = dataSnapshot.getValue(Users.class);
                            Glide.with(NavigationDrawerActivity.this)
                                    .load(users.getPhotUrl())
                                    .into(mDisplayImageView);

                            mNameTextView.setText(users.getUser());
                            mEmailTextView.setText(users.getEmail());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        imgBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadImagefromGallery(view);
            }
        });

    }



    public void loadImagefromGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        if (id == R.id.action_about) {
            Intent i = new Intent(this, About.class);
            startActivity(i);
        }
        if (id == R.id.action_feedback){
            Intent Email = new Intent(Intent.ACTION_SEND);
            Email.setType("text/email");
            Email.putExtra(Intent.EXTRA_EMAIL, new String[] { "sushantgarg1989@gmail.com" });
            Email.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
            Email.putExtra(Intent.EXTRA_TEXT, "Dear ...," + "");
            startActivity(Intent.createChooser(Email, "Send Feedback:"));
            return true;


        }
//        if(id==R.id.action_search)
//        {
//            final SearchView searchView=(SearchView) item.getActionView();
//            if(searchView != null)
//            {
//                searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//                searchView.setOnCloseListener(new SearchView.OnCloseListener(){
//                    @Override
//                    public boolean onClose()
//                    {
//                        return false;
//                    }
//                });
//
//                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//                    @Override
//                    public boolean onQueryTextSubmit(String query) {
//
//
//                        return true;
//                    }
//
//                    @Override
//                    public boolean onQueryTextChange(String text) {
//
//                      // md.getFilter().filter(text);
//                        return false;
//                    }
//                });
           // }
      //  }



        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.trending) {

            navigationView.getMenu().getItem(0).setChecked(true);
            fragment = new MoviePosterGridFragment();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.main_container_wrapper, fragment);
            transaction.commit();
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("Trending");

        } else if (id == R.id.top_rated) {
            navigationView.getMenu().getItem(1).setChecked(true);
            fragment = new HighestRatedMoviePosterGridFragment();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.main_container_wrapper, fragment);
            transaction.commit();
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("Top Rated");



        } else if (id == R.id.upcoming) {
            navigationView.getMenu().getItem(2).setChecked(true);
            fragment = new UpcomingMoviesPosterGridFragment();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.main_container_wrapper, fragment);
            transaction.commit();
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("Upcoming");

        } else if (id == R.id.in_theatres) {
            navigationView.getMenu().getItem(3).setChecked(true);
            Intent i = new Intent(this, InTheatresMovies.class);
            startActivity(i);
        }
//            else if (id == R.id.search_movie) {
//                navigationView.getMenu().getItem(4).setChecked(true);
//            fragment = new SearchMovieFragment();
//            FragmentTransaction transaction = fragmentManager.beginTransaction();
//            transaction.replace(R.id.main_container_wrapper, fragment);
//            transaction.commit();
//            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//            toolbar.setTitle("Search Movie");

        //}
        else if (id == R.id.lists) {
            navigationView.getMenu().getItem(4).setChecked(true);
            Intent i = new Intent(this, ListsActivity.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMovieSelected(Movie movie, boolean onClick, View view) {
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
                if (view != null) {
                    Log.d(TAG, "Fragment with transition??");
                    transaction.addSharedElement(view, getResources().getString(R.string.transition_poster));
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
            onMovieClicked(movie, true, view);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                imgBackground = (ImageView) findViewById(R.id.imageView_display);
                // Set the Image in ImageView after decoding the String
                imgBackground.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));


            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(drawable != null && drawable.getBitmap() != null) {
            outState.putParcelable(IMAGE_DATA, drawable.getBitmap());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }


}
