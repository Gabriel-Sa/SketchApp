package com.example.jraw_test_2;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
import java.util.HashMap;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    // TODO: profile page

    // TODO: have login screen go to another screen with registering/logging in
    // TODO: separate out login and registration?
    // TODO: skip button?

    private static final String TAG = "MainActivity";

    public ArrayList<Item> mItemList;
    public Bundle itemBundle;
    public BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: onCreate");

        super.onCreate(savedInstanceState);

        // holds data pulled from reddit with JRAW
        mItemList = new ArrayList<>();
        itemBundle = new Bundle(); // used for passing data between fragments

        // populate mItemList with posts from reddit using JRAW
        new MainActivity.MyTask().execute();

        setContentView(R.layout.activity_main);

        // used for changing fragments with bottomNav selection
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // hide browser buttons while MyTask() finishes
        bottomNav.getMenu().findItem(R.id.nav_browser).setVisible(false);
        bottomNav.getMenu().findItem(R.id.nav_sketchBrowser).setVisible(false);

        // set default fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new LoginFragment()).commit();
    }

    // function used to swap between fragments
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
                        case R.id.nav_sketchBrowser:
                            selectedFragment = new SketchBrowserFragment();
                            break;
                        case R.id.nav_canvas:
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

    // AsyncTask has to be used to circumvent "no network activity on main thread" error
    // pulls data from reddit using JRAW and populates mItemList

    private class MyTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference();
            DatabaseReference postRef = ref.child("posts");

            Log.d(TAG, "starting MyTask AsyncTask");

            // JRAW client setup
            UserAgent userAgent = new UserAgent("android", "github.com/lewkyb", "v1", "lookingfordriver");
            Credentials credentials = Credentials.userlessApp("uKb1rMTs_heYQA", UUID.randomUUID());
            NetworkAdapter networkAdapter = new OkHttpNetworkAdapter(userAgent);
            RedditClient redditClient = OAuthHelper.automatic(networkAdapter, credentials);

            // TODO: allow for user to choose subreddits
            DefaultPaginator<Submission> redditImages = redditClient.subreddits("aww", "Eyebleach").posts().build();

            Listing<Submission> submissions = redditImages.next();
            for (Submission s : submissions) {

                // avoid pulling gif or video
                if (!s.isSelfPost() && s.getUrl().contains("jpg")) {

                    String imageUrl = s.getUrl();       // URL;

                    // open database and save the post imgs.
                    postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int exists = 0;
                            for(DataSnapshot i : snapshot.getChildren()){
                                HashMap<?, ?> post = (HashMap<?, ?>) i.getValue();
                                String iUrl = null;
                                if (post != null) {
                                    iUrl = post.get("imageUrl").toString();
                                    if(iUrl.equals(imageUrl)){
                                        exists = 1;
                                        break;
                                    }
                                }
                            }
                            if(exists == 0){
                                postRef.push().setValue(new Post(imageUrl));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            // add data to Item object
            // Get an array of images from database Post table.
            // Get all imageURL keys
            final String[] image = new String[1];
            postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot i : snapshot.getChildren()){
                        HashMap<?, ?> post = (HashMap<?, ?>) i.getValue();
                        if (post != null) {
                            image[0] = (String) post.get("imageUrl");
                            if(image[0].contains("http")) mItemList.add(0, new Item(image[0]));
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // show both browser buttons after MyTask() finishes
            bottomNav.getMenu().findItem(R.id.nav_browser).setVisible(true);
            bottomNav.getMenu().findItem(R.id.nav_sketchBrowser).setVisible(true);

        }
    }


}
