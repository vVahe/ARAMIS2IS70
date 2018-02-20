package com.vvahe.aramis2is70;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bNavView;
    private FrameLayout frameLayout;

    private DashboardFragment dashboardFragment;
    private MapFragment mapFragment;
    private ChatFragment chatFragment;
    private SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bNavView = findViewById(R.id.mainNav);
        frameLayout = findViewById(R.id.mainFrame);

        dashboardFragment = new DashboardFragment();
        mapFragment = new MapFragment();
        chatFragment = new ChatFragment();
        settingsFragment = new SettingsFragment();

        /** set first fragment to be displayed on startup*/
        setFragment(dashboardFragment);

        /** switches between fragment using BottomMenu using switch statement*/
        bNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                switch(item.getItemId()) {
                    case R.id.nav_home:
                        setFragment(dashboardFragment);
                        return true;

                    case R.id.nav_map:
                        setFragment(mapFragment);
                        return true;

                    case R.id.nav_chat:
                        setFragment(chatFragment);
                        return true;

                    case R.id.nav_settings:
                        setFragment(settingsFragment);
                        return true;

                        default:
                            return false;

                }
            }
        });
    }

    /** switches between fragments */
    private void setFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainFrame, fragment);
        fragmentTransaction.commit();

    }

}
