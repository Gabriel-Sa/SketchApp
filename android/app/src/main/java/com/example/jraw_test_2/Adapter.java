package com.example.jraw_test_2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.squareup.picasso.Picasso;


/*

    Purpose: get list and display to recyclerview with picasso

    see also: https://stackoverflow.com/questions/3674951/whats-the-role-of-adapters-in-android

 */
public class Adapter extends RecyclerView.Adapter <Adapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Item> mItemList;

    public Adapter(Context context, ArrayList <Item> list) {
        mContext = context;
        mItemList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item currentItem = mItemList.get(position);

        String imageUrl = currentItem.getImageUrl();
        String postTitle = currentItem.getTitle();
        int likeCount = currentItem.getLikeCount();

        // use picasso to pull and show image on card
        Picasso.with(mContext).load(imageUrl).fit().centerInside().into(holder.mImageView);

        // change what appears on card
        holder.mTextViewTitle.setText(postTitle);
        holder.mTextViewLikes.setText("Likes: " + likeCount);
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageView;
        public TextView mTextViewTitle;
        public TextView mTextViewLikes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.image_view);
            mTextViewTitle = itemView.findViewById(R.id.text_view_title);
            mTextViewLikes = itemView.findViewById(R.id.text_view_likes);
        }
    }
}
