package com.vvahe.aramis2is70;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    User userObj = User.getInstance();

    private BottomNavigationView bNavView;
    private FrameLayout frameLayout;
    private Button logoutBtn;
    private static final String TAG = "MainActivity";

    private DashboardFragment dashboardFragment;
    private MapFragment mapFragment;
    private ChatOverviewFragment chatOverviewFragment;
    private SettingsFragment settingsFragment;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getUserID();
        Log.i("TAG", "get user is done");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();

        bNavView = findViewById(R.id.mainNav);
        frameLayout = findViewById(R.id.mainFrame);
        logoutBtn = findViewById(R.id.logoutBtn);

        dashboardFragment = new DashboardFragment();
        mapFragment = new MapFragment();
        chatOverviewFragment = new ChatOverviewFragment();
        settingsFragment = new SettingsFragment();

        checkUserLogin();

        if (!runtime_permissions()) {
            Intent i =new Intent(getApplicationContext(),GPS_Service.class);
            startService(i);
        }

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
                        setFragment(chatOverviewFragment);
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

    private void getUserID() {
        /*
            get userID of currently logged in user and create user object
         */
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uID = user.getUid();
            userObj.getData(uID);
            Log.i("TAG", "usser f singleton created");
        } else {
            //TODO: don't think this else statement can even be reached
        }
    }

    /*
        user location permission stuff
     */
    private boolean runtime_permissions() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION},100);

            return true;
        }
        return false;
    }

    /*
        some more user location permission stuff
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if( grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Intent i =new Intent(getApplicationContext(),GPS_Service.class);
                startService(i);
            }else {
                runtime_permissions();
            }
        }
    }

    public void logout() {
        Log.i(TAG, "logged out");
        mAuth.signOut();
        Intent logoutIntent = new Intent(MainActivity.this, LoginActivity.class);

        /** prevents user from going back once logged in, they will have to use logout button*/
        logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(logoutIntent);
    }

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
                } else {
                    User mainUser = User.getInstance();
                    mainUser.getData(firebaseAuth.getUid());
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

    /** used for onClicks in dashboard */
    public void toMapFragment(View view) throws InterruptedException {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButton1:
                if (checked)
                    Toast.makeText(this, "Searching other students of Course 1", Toast.LENGTH_SHORT).show();
                break;
            case R.id.radioButton2:
                if (checked)
                    Toast.makeText(this, "Searching other students of Course 2", Toast.LENGTH_SHORT).show();
                break;
            case R.id.radioButton3:
                if (checked)
                    Toast.makeText(this, "Searching other students of Course 3", Toast.LENGTH_SHORT).show();
                break;
            case R.id.radioButton4:
                if (checked)
                    Toast.makeText(this, "Searching other students of Course 4", Toast.LENGTH_SHORT).show();
                break;
        }

        setFragment(mapFragment);
    }

    public void toProfileSettings(View view) {
        Toast.makeText(this, "Change your profile settings", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }


}
