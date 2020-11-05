package com.example.jraw_test_2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class LoginFragment extends Fragment {

    private static final String TAG = "RegisterUser";

    private FirebaseAuth mAuth;

    private TextInputLayout editTextEmail, editTextPassword;

    private Button registerButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        editTextEmail = view.findViewById(R.id.edit_register_email);
        editTextPassword = view.findViewById(R.id.edit_register_password);

        registerButton = (Button) view.findViewById(R.id.registerUser_button_register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    private void registerUser() {
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

                            // object for storing user info in database
                            User user = new User(email);

                            // store data in database
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                    Toast.makeText(getContext(), "Sign in success!", Toast.LENGTH_LONG).show();
                } else {
                    Log.d(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(getContext(), "Sign in failure!", Toast.LENGTH_LONG).show();
                }
            }
        });
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