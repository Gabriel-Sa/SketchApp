package com.example.jraw_test_2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class SketchBrowserFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private Adapter mAdapter;
    private final ArrayList<Item> mItemList = new ArrayList<>();
    private String urlBase = "gs://jrawtest.appspot.com";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        //Initialize RecyclerView with grid section to match Browser
        View view = inflater.inflate(R.layout.fragment_sketchbrowser, container,
                false);
        mRecyclerView = view.findViewById(R.id.sketch_browser);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 2));

//        Get ALL drawings from database.

        StorageReference storageRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl(urlBase);

         storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for(StorageReference item : listResult.getItems()){
                    mItemList.add(0, new Item(item.toString()));
                }
                if(!mItemList.isEmpty()){
                    mAdapter = new Adapter(getContext(), mItemList);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }
        });


        return view;
    }
}
