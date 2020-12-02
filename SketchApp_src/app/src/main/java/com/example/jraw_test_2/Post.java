package com.example.jraw_test_2;

import java.util.UUID;

public class Post {
    public String imageUrl;
    public String authorUsername;
    public String category;
    public UUID authorUUID;


    public Post(String image){
        imageUrl = image;
    }

    public Post(String image, String author, UUID authorUID, String cate){
        imageUrl = image;
        authorUsername = author;
        authorUUID = authorUID;
        category = cate;

    }

    public String getImageUrl(){
        return imageUrl;
    }
}

