package garg.sushant.mymovielist;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Sushant on 4/24/2017.
 */

public class About extends AppCompatActivity {


    private ImageView mFacebookImageView;

    private ImageView mTwitterImageView;
    private ImageView mInstagramImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("About");
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),NavigationDrawerActivity.class));
            }
        });

        mFacebookImageView = (ImageView) findViewById(R.id.fblink);
        mTwitterImageView = (ImageView) findViewById(R.id.twitterlink);
        mInstagramImageView = (ImageView) findViewById(R.id.instalink);

        mFacebookImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/sushantgarg1989"));
                startActivity(browserIntent);
            }
        });

        mTwitterImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/Sushant20"));
                startActivity(browserIntent);
            }
        });

        mInstagramImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/sushant2010/"));
                startActivity(browserIntent);
            }
        });
    }
}
