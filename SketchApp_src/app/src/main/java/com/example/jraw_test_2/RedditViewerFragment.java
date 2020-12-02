package com.example.jraw_test_2;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RedditViewerFragment extends Fragment {

    private static final String TAG = "RedditViewerFragment";

    private RecyclerView mRecyclerView;
    private Adapter mAdapter;
    private ArrayList<Item> mItemList;
    private Bundle itemBundle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        itemBundle = new Bundle();
        mItemList = new ArrayList<>();

        View view = inflater.inflate(R.layout.fragment_reddit_viewer, container, false);

        // setup recycle viewer
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true); // saves memory because size doesn't change
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mRecyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 2));


        // get Bundle passed from MainActivity
        itemBundle = getArguments();

        if (itemBundle != null) {

            // get ArrayList out of the Bundle from MainActivity
            mItemList = itemBundle.getParcelableArrayList("redditItemList");
            mAdapter = new Adapter(getContext(), mItemList);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            Log.d(TAG, "setAdapter:failed");
        }
        return view;
    }
}