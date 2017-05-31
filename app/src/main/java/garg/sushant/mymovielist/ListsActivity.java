package garg.sushant.mymovielist;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import garg.sushant.mymovielist.rest.Movie;

public class ListsActivity extends AppCompatActivity implements FavoriteMoviePosterGridFragment.OnMovieSelectedListener, WatchedMoviePosterGridFragment.OnMovieSelectedListener,
                                        ToWatchMoviePosterGridFragment.OnMovieSelectedListener{

    static final String TAG = ListsActivity.class.getSimpleName();

    private static final String KEY_MOVIE = "movie";

    private static final String TAG_DETAIL_FRAGMENT = "fragment_details";

   // boolean mIsFavorite = false;

    boolean mTwoPaneMode = false;

    // remember the selected movie
    Movie mSelectedMovie;

//    @Nullable
//    @Bind(R.id.fab_favorite)
//    FloatingActionButton mFavoriteFab;




    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);


//        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
//        getLayoutInflater().inflate(R.layout.activity_lists, contentFrameLayout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        toolbar.setTitle("Your Lists");

        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),NavigationDrawerActivity.class));
            }
        });


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);




    }



    @Override
    protected void onResume() {
        super.onResume();

        if (mSelectedMovie != null && findViewById(R.id.movie_detail_title) != null) {
            onMovieSelected(mSelectedMovie, false, null);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_MOVIE, mSelectedMovie);
        super.onSaveInstanceState(outState);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lists, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMovieSelected(Movie movie, boolean onClick, View view) {
        mSelectedMovie = movie;
//        mIsFavorite = mSelectedMovie == null ? false : MovieFavorites.isFavoriteMovie(this, mSelectedMovie.getId());

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
  //              mFavoriteFab.setImageResource(MovieFavorites.getImageResourceId(mIsFavorite));
            }

            String title = movie == null ? "" : movie.getTitle();
            TextView titleView = (TextView) findViewById(R.id.movie_detail_title);
            titleView.setText(title);
    //        mFavoriteFab.show();

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

//    @Nullable
  //  @OnClick(R.id.fab_favorite) void onFavoriteClicked() {
    //    toggleFavorite();
    //}

//    private void toggleFavorite() {
//        mIsFavorite = !mIsFavorite;
//        mFavoriteFab.setImageResource(MovieFavorites.getImageResourceId(mIsFavorite));
//        if (mSelectedMovie != null) {
//            MovieFavorites.updateFavorite(this, mIsFavorite, mSelectedMovie);
//        }
//        // refresh after movie unfavorited
//        if (mTwoPaneMode && !mIsFavorite) {
//            FavoriteMoviePosterGridFragment gridFragment = (FavoriteMoviePosterGridFragment) getSupportFragmentManager().findFragmentById(R.id.favorite_fragment_grid);
//            gridFragment.removeMovie(mSelectedMovie);
//        }
//    }
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_lists, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
          switch(position){
              case 0:
                  FavoriteMoviePosterGridFragment fm = new FavoriteMoviePosterGridFragment();
                  return fm;
              case 1:
                  WatchedMoviePosterGridFragment wm = new WatchedMoviePosterGridFragment();
                  return wm;
              case 2:
                  ToWatchMoviePosterGridFragment twm = new ToWatchMoviePosterGridFragment();
                  return twm;
          }
          return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Favourites";
                case 1:
                    return "Watched";
                case 2:
                    return "To Watch";
            }
            return null;
        }
    }
}
