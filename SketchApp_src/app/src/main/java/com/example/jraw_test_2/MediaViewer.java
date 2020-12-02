package com.example.jraw_test_2;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.jraw_test_2.R.layout.viewer_media;

public class MediaViewer extends AppCompatActivity {
    private static final String TAG = "MediaViewer";
    private Context mContext = this;
    private FirebaseAuth mAuth;

    private EditText newComment;
    private String commentString;
    private ArrayList<CommentItem> commentList;
    private Bundle bundle = new Bundle();
    private String urlBase = "jrawtest.appspot.com";


    public MediaViewer (){
        super(viewer_media);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(getIntent().hasExtra("image_url")) {
            String imgUrl = getIntent().getStringExtra("image_url");
            commentList = getComments(imgUrl);
            bundle.putString("image_url", imgUrl);
            bundle.putParcelableArrayList("comment_list", commentList);

            getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(false)
                    .add(R.id.f_image, com.example.jraw_test_2.ImageFragment.class, bundle)
                    .commit();


            getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(false)
                    .add(R.id.f_comment, com.example.jraw_test_2.CommentFragment.class, bundle)
                    .commit();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setReorderingAllowed(false)
                            .replace(R.id.f_comment, com.example.jraw_test_2.CommentFragment.class, bundle)
                            .commit();
                }
            }, 1000);


            DatabaseReference ref = FirebaseDatabase
                    .getInstance()
                    .getReference();

            final String[] postKey = new String[1];
            //Check to make sure its a reddit post or a Sketch.
            if(!imgUrl.contains(urlBase)) {
                ref.child("posts")
                        .orderByChild("imageUrl")
                        .equalTo(imgUrl)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                HashMap<?, ?> post = (HashMap<?, ?>) snapshot.getValue();
                                if (post != null) {
                                    for (Object key : post.keySet()) {
                                        postKey[0] = key.toString();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
            }else{
                ref.child("posts")
                        .orderByChild("imageUrl")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot i : snapshot.getChildren()) {
                                    HashMap<?, ?> post = (HashMap<?, ?>) i.getValue();
                                    String imageUUID = post.get("imageUrl").toString();
                                    if (imgUrl.contains(imageUUID)) {
                                        postKey[0] = (String) i.getKey();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
            }

            newComment = (EditText)findViewById(R.id.newComment);
            newComment.setOnKeyListener(new EditText.OnKeyListener(){
                public boolean onKey(View v, int keyCode, KeyEvent event){
                    if(keyCode == 66 && event.getAction() != KeyEvent.ACTION_UP){
                        commentString = String.valueOf(newComment.getText());
                        addComment(commentString, postKey[0]);
                        newComment.getText().clear();
                        commentList = getComments(imgUrl);
                        bundle.putParcelableArrayList("comment_list", commentList);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .setReorderingAllowed(false)
                                        .replace(R.id.f_comment, com.example.jraw_test_2.CommentFragment.class, bundle)
                                        .commit();
                            }
                        }, 1000);
                        return true;
                    }
                    return false;
                }
            });

        }
    }
    private void addComment(String comment, String postKey){
        String currentUser = null;
        FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
        if(mAuth != null){
            currentUser = mAuth.getEmail();
        }
        DatabaseReference ref = FirebaseDatabase
                .getInstance()
                .getReference("posts");

        if (currentUser != null) {
            ref.child(postKey)
                    .child("comments")
                    .push()
                    .setValue(new CommentItem(currentUser, comment))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(mContext, "Comment Added Successfully!", Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(mContext, "Failed to Add Comment!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }else{
            Toast.makeText(mContext, "You must login before commenting!", Toast.LENGTH_LONG).show();
        }
    }

    private ArrayList<CommentItem> getComments(String imageUrl){
        ArrayList<CommentItem> returnList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase
                .getInstance()
                .getReference("posts");
        //If the imageURL contains the urlBase (see above) then it is a Sketch.
        if(imageUrl.contains(urlBase)){
            ref.orderByChild("imageUrl")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot i : snapshot.getChildren()) {
                                HashMap<?, ?> snapHash = (HashMap<?, ?>) i.getValue();
                                String imageUUID = (String) snapHash.get("imageUrl");
                                if(imageUrl.contains(imageUUID)) {
                                    HashMap<?, ?> commentHash = (HashMap<?, ?>) snapHash.get("comments");
                                    if (commentHash != null) {
                                        for (Object key : commentHash.keySet()) {
                                            HashMap<?, ?> comments = (HashMap<?, ?>) commentHash.get(key.toString());
                                            returnList.add(0, new CommentItem(
                                                    comments.get("username").toString(),
                                                    comments.get("commentText").toString()));
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
        } else {
            // It is a reddit file, so just grab the comments under the post.
            ref.orderByChild("imageUrl")
                    .equalTo(imageUrl)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot i : snapshot.getChildren()) {
                                HashMap<?, ?> snapHash = (HashMap<?, ?>) i.getValue();
                                HashMap<?, ?> commentHash = (HashMap<?, ?>) snapHash.get("comments");
                                if (commentHash != null) {
                                    for (Object key : commentHash.keySet()) {
                                        HashMap<?, ?> comments = (HashMap<?, ?>) commentHash.get(key.toString());
                                        returnList.add(0, new CommentItem(
                                                comments.get("username").toString(),
                                                comments.get("commentText").toString()));
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
        }
        return returnList;
    }
}