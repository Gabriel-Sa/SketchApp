package com.example.sketchApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.nav_bar);
        bottomNav.setSelectedItemId(R.id.nav_h);

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_d:
                        startActivity(new Intent(getApplicationContext(), Drawboard.class));
                        overridePendingTransition(0,0);
                        return true;


                    case R.id.nav_h:
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