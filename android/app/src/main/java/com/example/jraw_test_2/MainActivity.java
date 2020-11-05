package com.example.jraw_test_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dean.jraw.pagination.DefaultPaginator;

import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    // TODO: canvas page
    // TODO: profile page

    // RedditViewer
    // TODO: enlarge card and offer comment section with upvote/downvote

    // LOGIN
    // TODO: have login screen go to another screen with registering/logging in
    // TODO: separate out login and registration?
    // TODO: skip button?

    private static final String TAG = "MainActivity";

    public ArrayList<Item> mItemList;
    public Bundle itemBundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mItemList = new ArrayList<>();
        itemBundle = new Bundle();

        // populate mItemList with posts from reddit using JRAW
        new MainActivity.MyTask().execute();

        setContentView(R.layout.activity_main);

        // used for changing fragments with bottomNav selection
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // set default fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new LoginFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_browser:
                            selectedFragment = new RedditViewerFragment();
                            // send mItemlist to RedditViewerFragment
                            itemBundle.putParcelableArrayList("redditItemList", mItemList);
                            selectedFragment.setArguments(itemBundle);
                            break;
                        case R.id.nav_canvas:
                            // TODO: implement canvas
                            selectedFragment = new PaintFragment();
                            break;
                        case R.id.nav_profile:
                            // TODO: create profile and switch LoginFragment() with ProfileFragment()
                            selectedFragment = new LoginFragment();
                            break;
                    }

                    // sets fragment based on case
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();

                    // true means that we want to select the clicked item
                    // if false, the fragment would still be displayed, but not selected
                    return true;
                }
            };

    private class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            Log.d(TAG, "starting MyTask AsyncTask");
            // JRAW client setup
            UserAgent userAgent = new UserAgent("android", "github.com/lewkyb", "v1", "lookingfordriver");
            Credentials credentials = Credentials.userlessApp("uKb1rMTs_heYQA", UUID.randomUUID());
            NetworkAdapter networkAdapter = new OkHttpNetworkAdapter(userAgent);
            RedditClient redditClient = OAuthHelper.automatic(networkAdapter, credentials);

            // TODO: allow for user to choose subreddits
            DefaultPaginator<Submission> earthPorn = redditClient.subreddits("aww", "spaceporn").posts().build();

            Listing<Submission> submissions = earthPorn.next();
            for (Submission s : submissions) {

                // TODO: avoid pulling gif or video
                if (!s.isSelfPost() && s.getUrl().contains("jpg")) {

                    String imageUrl = s.getUrl();       // URL
                    System.out.println(imageUrl);
                    String postTitle = s.getTitle();    // Post Title
                    int likeCount = s.getScore();       // Upvotes - Downvotes = Score

                    // add data to Item object
                    mItemList.add(new Item(imageUrl, postTitle, likeCount));
                }
            }
            return null;
        }
    }
}
