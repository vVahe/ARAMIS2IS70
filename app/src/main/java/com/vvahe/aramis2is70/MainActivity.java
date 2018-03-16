package com.vvahe.aramis2is70;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bNavView;
    private FrameLayout frameLayout;
    private Button logoutBtn;
    private static final String TAG = "MainActivity";

    private DashboardFragment dashboardFragment;
    private MapFragment mapFragment;
    private ChatFragment chatFragment;
    private SettingsFragment settingsFragment;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();

        bNavView = findViewById(R.id.mainNav);
        frameLayout = findViewById(R.id.mainFrame);
        logoutBtn = findViewById(R.id.logoutBtn);

        dashboardFragment = new DashboardFragment();
        mapFragment = new MapFragment();
        chatFragment = new ChatFragment();
        settingsFragment = new SettingsFragment();

        user = User.getInstance();
        checkUserLogin();
        getUserData();

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

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

    private void getUserData() {
        user.setUserInfo(mAuth.getCurrentUser().getUid());
    }

    public void logout() {
        Log.i(TAG, "logged out");
        mAuth.signOut();
        Intent logoutIntent = new Intent(MainActivity.this, LoginActivity.class);

        /** prevents user from going back once logged in, they will have to use logout button*/
        logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(logoutIntent);
    };

    private void checkUserLogin() {
        /** checks if user is logged in not*/
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                /** FirebaseAuth returns result whether logged in or not
                 * if user is not logged in redirect to login page using intent*/
                if (firebaseAuth.getCurrentUser() == null) {

                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);

                    /** prevents user from going back once logged in, they will have to use logout button*/
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
            }
        };
    }

    /** switches between fragments */
    private void setFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainFrame, fragment);
        fragmentTransaction.commit();

    }

    @Override
    public void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }

}
