package com.example.sketchApp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

public class loginactivity extends AppCompatActivity {

    android.widget.EditText emailtxt, passwordtxt;
    android.widget.Button loginbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginactivity);

        emailtxt=findViewById(R.id.email);
        passwordtxt=findViewById(R.id.password);
        loginbtn=findViewById(R.id.login);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username=emailtxt.getText().toString().trim();
                String pass=passwordtxt.getText().toString().trim();

                //Database connection needs to happen here

                if(TextUtils.isEmpty(username)) {
                   emailtxt.setError("Please enter an email");
                   return;
                }
                if(TextUtils.isEmpty(pass)){
                    passwordtxt.setError("Please enter a password");
                    return;
                }

                if(pass.length()<8){
                    passwordtxt.setError("Password must be at least 8 characters long");
                    return;
                }

                if(username.equals("sketch@gmail.com")&&pass.equals("123456789"))
                {
                    Toast.makeText(loginactivity.this, "Welcome to SketchApp", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                }
                else
                {
                    Toast.makeText(loginactivity.this, "Email or Password is not correct", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}