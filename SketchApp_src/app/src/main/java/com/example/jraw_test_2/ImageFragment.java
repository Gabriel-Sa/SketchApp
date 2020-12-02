package com.example.jraw_test_2;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.stfalcon.imageviewer.StfalconImageViewer;
import com.stfalcon.imageviewer.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;


public class ImageFragment extends Fragment {

    private ImageView imageView;

    public ImageFragment(){
        super(R.layout.fragment_image);

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        String imageUrl = requireArguments().getString("image_url");

        addImage(view, imageUrl);


    }

    private void addImage(View view, String imgUrl){
        imageView = view.findViewById(R.id.viewer_image);

        Glide.with(getContext())
                .load(imgUrl)
                .centerCrop()
                .into(imageView);

        imageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                List<String> imgUrls = new ArrayList<>();
                imgUrls.add(imgUrl);
                new StfalconImageViewer.Builder<>(getContext(), imgUrls, new ImageLoader<String>(){
                    @Override
                    public void loadImage(ImageView imageView, String image){
                        Glide.with(getContext())
                                .load(image)
                                .into(imageView);
                    }
                }).show();
            }
        });

    }




}
