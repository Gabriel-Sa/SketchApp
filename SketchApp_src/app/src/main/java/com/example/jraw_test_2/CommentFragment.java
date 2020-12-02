package com.example.jraw_test_2;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CommentFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<CommentItem> commentList = new ArrayList<>();


    public CommentFragment() { super(R.layout.fragment_comments); }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        commentList = requireArguments().getParcelableArrayList("comment_list");
        createCommentView(view, commentList);
    }

    private void createCommentView(View view, ArrayList<CommentItem> commentList){
        //Get Firebase Comments table and add to commentList
        mRecyclerView = view.findViewById(R.id.commentRecycler);
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new CommentAdapter(commentList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }


}