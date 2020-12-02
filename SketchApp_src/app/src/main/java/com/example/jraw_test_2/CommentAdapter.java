package com.example.jraw_test_2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter <CommentAdapter.CommentViewHolder> {

    private ArrayList<CommentItem> mCommentList;

    public static class CommentViewHolder extends RecyclerView.ViewHolder{
        public TextView mCommentUsername;
        public TextView mComment;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            mCommentUsername= itemView.findViewById(R.id.commentUsername);
            mComment = itemView.findViewById(R.id.commentText);
        }
    }

    public CommentAdapter(ArrayList<CommentItem> commentList){
        mCommentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comments, parent, false);
        CommentViewHolder commentVH = new CommentViewHolder(view);

        return commentVH;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        CommentItem current = mCommentList.get(position);

        holder.mCommentUsername.setText(current.getUsername());
        holder.mComment.setText(current.getCommentText());

    }

    @Override
    public int getItemCount() {
        return mCommentList.size();
    }


}
