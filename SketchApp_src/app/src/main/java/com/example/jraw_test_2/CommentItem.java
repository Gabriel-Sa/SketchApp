package com.example.jraw_test_2;


import android.os.Parcel;
import android.os.Parcelable;

public class CommentItem implements Parcelable {
    private String mUsername;
    private String mCommentText;

    public CommentItem(String username, String commentText){
        mUsername = username;
        mCommentText = commentText;
    }

    protected CommentItem(Parcel in){
        mUsername = in.readString();
        mCommentText = in.readString();
    }

    public static final Creator<CommentItem> CREATOR = new Creator<CommentItem>() {
        @Override
        public CommentItem createFromParcel(Parcel in) {
            return new CommentItem(in);
        }

        @Override
        public CommentItem[] newArray(int size) {
            return new CommentItem[size];
        }
    };

    public String getUsername(){
        return mUsername;
    }
    public String getCommentText(){
        return mCommentText;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mUsername);
        dest.writeString(mCommentText);
    }
}
