package com.example.jraw_test_2;

import android.os.Parcel;
import android.os.Parcelable;

/*

    Purpose: Store information taken from JRAW. Parcelable is the
    preferred method for data transfer in android between activity
    and fragments because it is faster that serializing.

 */

public class Item implements Parcelable {

    private String mImageUrl;
    private String mTitle;
    private int mLikes;

    public Item(String imageUrl, String title, int likes) {
        mImageUrl = imageUrl;
        mTitle = title;
        mLikes = likes;
    }

    protected Item(Parcel in) {
        mImageUrl = in.readString();
        mTitle = in.readString();
        mLikes = in.readInt();
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getLikeCount() {
        return mLikes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mImageUrl);
        dest.writeString(mTitle);
        dest.writeInt(mLikes);
    }
}
