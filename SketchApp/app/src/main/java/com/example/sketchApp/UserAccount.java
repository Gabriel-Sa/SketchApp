package com.example.sketchApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserAccount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        BottomNavigationView bottomNav = findViewById(R.id.nav_bar);
        bottomNav.setSelectedItemId(R.id.nav_a);

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_d:
                        startActivity(new Intent(getApplicationContext(), Drawboard.class));
                        overridePendingTransition(0,0);
                        return true;


                    case R.id.nav_h:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.nav_a:
                        return true;

                }
                return false;
            }
        });
    }
}