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

    public Item(String imageUrl) {
        mImageUrl = imageUrl;
    }

    protected Item(Parcel in) {
        mImageUrl = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mImageUrl);
    }
}
