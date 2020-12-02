package com.example.jraw_test_2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;




/*

    Purpose: get list and display to recyclerview with Glide

    see also: https://stackoverflow.com/questions/3674951/whats-the-role-of-adapters-in-android

 */
public class Adapter extends RecyclerView.Adapter <Adapter.ViewHolder> {
    private static final String TAG = "Adapter";
    private String urlBase = "jrawtest.appspot.com";
    private Context mContext;
    private ArrayList<com.example.jraw_test_2.Item> mItemList;

    public Adapter(Context context, ArrayList <com.example.jraw_test_2.Item> list) {
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
        com.example.jraw_test_2.Item currentItem = mItemList.get(position);

        final String[] imageUrl = {currentItem.getImageUrl()};
        //Check if the imageURL contains the urlBase if it does, then get the downloadable link.
        // And fit it into the imageView
        if(imageUrl[0].contains(urlBase)){
            StorageReference storeRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl[0]);
            storeRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Wait for URI response from API then fit it into the ImageView
                    imageUrl[0] = uri.toString();
                    Glide.with(mContext)
                            .load(imageUrl[0])
                            .centerCrop()
                            .into(holder.mImageView);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
        }else{
            // use Glide to pull and show image on card
            try {
                Glide.with(mContext)
                        .load(imageUrl[0])
                        .centerCrop()
                        .into(holder.mImageView);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        holder.parentLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(mContext, com.example.jraw_test_2.MediaViewer.class);
                intent.putExtra("image_url", imageUrl[0]);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageView;
        public RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.image_view);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }


    }

}
