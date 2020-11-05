package com.example.jraw_test_2;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import java.util.Arrays;
import java.util.UUID;

public class RedditViewerFragment extends Fragment {

    private static final String TAG = "RedditViewerFragment";

    private RecyclerView mRecyclerView;
    private Adapter mAdapter;
    private ArrayList<Item> mItemList;
    private Bundle itemBundle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView");
        itemBundle = new Bundle();
        mItemList = new ArrayList<>();

        View view = inflater.inflate(R.layout.fragment_reddit_viewer, container, false);

        // setup recycle viewer
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true); // saves memory because size doesn't change
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));


        // get Bundle passed from MainActivity
        itemBundle = getArguments();

        if (itemBundle != null) {

            // get ArrayList out of the Bundle from MainActivity
            mItemList = itemBundle.getParcelableArrayList("redditItemList");

            mAdapter = new Adapter(getContext(), mItemList);
            mRecyclerView.setAdapter(mAdapter);
            Log.d(TAG, "setAdapter:success");
        } else {
            Log.d(TAG, "setAdapter:failed");
        }

        return view;
    }

}