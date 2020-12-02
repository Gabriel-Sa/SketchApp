package com.example.jraw_test_2;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoginFragment extends Fragment{

    private static final String TAG = "LoginFragment";

    private FirebaseAuth mAuth;

    private TextInputLayout editTextEmail, editTextPassword;

    private Button registerButton;
    private Button signInButton;

    //These are for the profile!!
    private Toast loginStatus;
    private Button logoutButton;
    private TextView profileEmail;
    private RecyclerView profilePicturesRV;
    private Adapter mAdapter;
    private String urlBase = "gs://jrawtest.appspot.com";
    private final ArrayList<Item> mItemList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "LoginFragment onCreateView()");
        View view;

        if(mAuth.getCurrentUser() != null) {
            Log.d("LOGIN", "Currently logged in as: " + mAuth.getCurrentUser().getEmail());
            view = inflater.inflate(R.layout.profile, container, false);
            profileEmail = (TextView) view.findViewById(R.id.profile_email);
            profileEmail.setText(mAuth.getCurrentUser().getEmail());
            logoutButton = (Button) view.findViewById(R.id.logout_button);
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signOut();

                }
            });
            profilePicturesRV = view.findViewById(R.id.profile_RV);
            profilePicturesRV.setHasFixedSize(true);
            profilePicturesRV.setLayoutManager(new GridLayoutManager(view.getContext(), 2));

            //Get images only from current user.
            DatabaseReference reference = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Users/" + mAuth.getCurrentUser().getUid() + "/imageList");

            //For each image under a user, get a dataSnapshot and download it to memory and put it into an imageview
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot image : snapshot.getChildren()){
                        if(image != null) {
                            String imgAddress = (String) image.getValue();
                            imgAddress = urlBase + '/' + imgAddress;
                            mItemList.add(0, new Item(imgAddress));
                        }
                        if(!mItemList.isEmpty()){
                            mAdapter = new Adapter(getContext(), mItemList);
                            profilePicturesRV.setAdapter(mAdapter);
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        else {

            view = inflater.inflate(R.layout.fragment_login, container, false);

            editTextEmail = view.findViewById(R.id.edit_register_email);
            editTextPassword = view.findViewById(R.id.edit_register_password);

            registerButton = (Button) view.findViewById(R.id.registerUser_button_register);
            signInButton = (Button) view.findViewById(R.id.registerUser_button_signin);
            registerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    registerUser();
                }
            });
            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // using handler and runnable to cause delay while waiting on database communication
                    final Handler handler = new Handler();
                    final Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            Fragment currentFragment = getParentFragmentManager().findFragmentById(R.id.fragment_container);
                            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                            fragmentTransaction.detach(currentFragment);
                            fragmentTransaction.attach(currentFragment);
                            fragmentTransaction.commit();
                        }
                    };

                    signIn();

                    // wait for signIn() to communicate with firebase then refresh the current fragment
                    handler.postDelayed(r, 500);
                }
            });
        }
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "LoginFragment onCreate()");
        mAuth = FirebaseAuth.getInstance(); // start FirebaseAuth
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "LoginFragment onStart()");
        FirebaseUser currentUser = mAuth.getCurrentUser(); // check to see if logged in
        if(mAuth.getCurrentUser() != null)
            Log.d("LOGIN", "Currently logged in as: " + mAuth.getCurrentUser().getEmail());
    }

    private void registerUser() {
        Log.d(TAG, "LoginFragment registerUser()");

        String email = editTextEmail.getEditText().getText().toString().trim();
        String password = editTextPassword.getEditText().getText().toString().trim();

        Log.d(TAG, "registerUser: " + email);

        if (!validateForm()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Firebase createUserWithEmailAndPassword:success");

                            // create UID in database and put user's email into UID as child
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("email")
                                    .setValue(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Firebase createUserWithEmailAndPassword:success");
                                        Toast.makeText(getContext(), "Register success!", Toast.LENGTH_LONG).show();
                                    } else {
                                        Log.d(TAG, "FirebaseDatabase write createUserWithEmailandPassword:failure", task.getException());
                                    }
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void signIn() {

        String email = editTextEmail.getEditText().getText().toString().trim();
        String password = editTextPassword.getEditText().getText().toString().trim();

        Log.d(TAG, "signIn: " + email);

        if (!validateForm()) {
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithEmail:success");
                } else {
                    Log.d(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(getContext(), "Sign in failure!", Toast.LENGTH_LONG).show();
                }
            }
        });

        return;
    }

    private void signOut() {

        //Already signed out
        Toast logoutStatus;
        if (mAuth.getCurrentUser() == null) {
            logoutStatus = Toast.makeText(this.getContext(), "Already logged out.", Toast.LENGTH_SHORT);
            logoutStatus.show();
            return;
        }

        mAuth.signOut();
        logoutStatus = Toast.makeText(this.getContext(), "Successfully logged out.", Toast.LENGTH_SHORT);
        logoutStatus.show();

        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        return;

    }

    private boolean validateForm() {

        Log.d(TAG, "validating form");
        String email = editTextEmail.getEditText().getText().toString().trim();
        String password = editTextPassword.getEditText().getText().toString().trim();

        boolean valid = true;

        if (email.isEmpty()) {
            editTextEmail.setError("Email required");
            editTextEmail.requestFocus();
            valid = false;
        }
        if (password.isEmpty()) {
            editTextPassword.setError("Password required");
            editTextPassword.requestFocus();
            valid = false;
        }
        if (password.length() < 6) {
            editTextPassword.setError("more than 6 characters required");
            editTextPassword.requestFocus();
            valid = false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Provide valid email");
            editTextEmail.requestFocus();
            valid = false;
        }

        return valid;
    }

}