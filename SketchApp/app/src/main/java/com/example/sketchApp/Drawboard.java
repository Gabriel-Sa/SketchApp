package com.example.sketchApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Drawboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawboard);

        BottomNavigationView bottomNav = findViewById(R.id.nav_bar);
        bottomNav.setSelectedItemId(R.id.nav_d);

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_d:
                        return true;


                    case R.id.nav_h:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.nav_a:
                        startActivity(new Intent(getApplicationContext(), UserAccount.class));
                        overridePendingTransition(0,0);
                        return true;

                }
                return false;
            }
        });
    }
}