package com.vvahe.aramis2is70;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public User userObj = User.getInstance();

    private BottomNavigationView bNavView;
    private FrameLayout frameLayout;
    private Button logoutBtn;
    private static final String TAG = "MainActivity";

    private DashboardFragment dashboardFragment;
    private MapFragment mapFragment;
    private ChatOverviewFragment chatOverviewFragment;
    private SettingsFragment settingsFragment;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("TAG", "get user is done");
        mAuth = FirebaseAuth.getInstance();

        bNavView = findViewById(R.id.mainNav);
        frameLayout = findViewById(R.id.mainFrame);


        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) { //not logged in send to login page

            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginIntent);

        } else { //get current user and get the user object using userID

            userObj.getData(currentUser.getUid());
            userObj.context = getApplicationContext();
        }

        dashboardFragment = new DashboardFragment();
        mapFragment = new MapFragment();
        chatOverviewFragment = new ChatOverviewFragment();
        settingsFragment = new SettingsFragment();

        startGPS_Service();

        /* set first fragment to be displayed on startup */
        setFragment(dashboardFragment);


        /* handles switches between fragment when bottomNav is used */
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

    /* handles fragement transactions aka switching between fragments*/
    private void setFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainFrame, fragment);
        fragmentTransaction.commit();

    }

    /* start GPS service for user location */
    private void startGPS_Service() {

        if (!runtime_permissions()) {
            Log.wtf("permission:", "not executed");
            Intent i =new Intent(getApplicationContext(),GPS_Service.class);
            startService(i);
        }
    }

    /* checks for logged in users */
    @Override
    public void onStart() {
        super.onStart();

//        FirebaseUser currentUser = mAuth.getCurrentUser();
//
//        if (currentUser == null) { //not logged in send to login page
//
//            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
//            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(loginIntent);
//
//        } else { //get current user and get the user object using userID
//
//            userObj.getData(currentUser.getUid());
//        }
    }

    /* user location permission stuff */
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

    /* some more user location permission stuff */
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


}
