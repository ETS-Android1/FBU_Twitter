package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.codepath.apps.restclienttemplate.fragments.HomeFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.parceler.Parcels;

public class HomeActivity extends AppCompatActivity {

    public static final String TAG = "HomeActivity";
    public static final int COMPOSE_REQUEST = 15;
    final FragmentManager fragmentManager = getSupportFragmentManager();
    BottomNavigationView bottomNavigationView;
    // Instance of the progress action-view
    static MenuItem miActionProgressItem;
    MenuItem mButton;
    Fragment fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.ic_twitter_bird);
        actionBar.setDisplayUseLogoEnabled(true);

        miActionProgressItem = findViewById(R.id.miActionProgress);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setBackgroundColor(Color.TRANSPARENT);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                fragment = null;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.action_search:
                        fragment = new HomeFragment();
                        break;
                    case R.id.action_notifications:
                        fragment = new HomeFragment();
                        break;
                    case R.id.action_direct_messages:
                        fragment = new HomeFragment();
                        break;
                    default:
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });

        fragment = new HomeFragment();
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        mButton = menu.findItem(R.id.miActionButtons);
        View v = MenuItemCompat.getActionView(mButton);
        Button b = (Button) v.findViewById(R.id.mButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        return true;
    }

    private void logout() {
        TwitterApp.getRestClient(this).clearAccessToken();
        // go to login activity
        Intent i = new Intent(HomeActivity.this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    public static void showProgressBar() {
        // Show progress item
        miActionProgressItem.setVisible(true);
    }

    public static void hideProgressBar() {
        // Hide progress item
        miActionProgressItem.setVisible(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == COMPOSE_REQUEST && resultCode == RESULT_OK) {
            fragment = new HomeFragment();
            Tweet tweet = Parcels.unwrap(getIntent().getParcelableExtra("NEW_TWEET"));
            data.putExtra("NEW_TWEET", Parcels.wrap(tweet));
            fragment.onActivityResult(COMPOSE_REQUEST, RESULT_OK, data);
            Log.d(TAG, "OnActivityResult");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}