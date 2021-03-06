package com.vvahe.aramis2is70;

import android.content.ClipData;
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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.vvahe.aramis2is70.Chat.Chat;
import com.vvahe.aramis2is70.Chat.ChatOverviewFragment;
import com.vvahe.aramis2is70.Other.GPS_Service;
import com.vvahe.aramis2is70.Dashboard.DashboardFragment;
import com.vvahe.aramis2is70.Login_Register.LoginActivity;
import com.vvahe.aramis2is70.Map.MapFragment;
import com.vvahe.aramis2is70.Settings.SettingsFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public User userObj = User.getInstance();

    private BottomNavigationView bNavView;
    private Menu menu;
    private FrameLayout frameLayout;
    private Button logoutBtn;
    private MenuItem chatItem;
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
        menu = bNavView.getMenu();

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


        try{
            setListenerChatImage();
        } catch(NullPointerException e){

        }



    }

    /* handles fragement transactions aka switching between fragments*/
    private void setFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainFrame, fragment);
        fragmentTransaction.commit();

    }

    /* Set chat icon to new message icon if there are new messages */
    private void setListenerChatImage(){
        userObj.firebaseThisUser.child("chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Chat> chats = new ArrayList<>();
                for(DataSnapshot dsChild : dataSnapshot.getChildren()) {
                    final String chatID = dsChild.getValue(String.class);
                    String otherUserID = chatID.replace(userObj.userID, ""); //get otherUserId (= chatID - userID)
                    final Chat chat = new Chat(otherUserID);

                    chat.firebaseThisChat.child("messages").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            chat.setDataInMessages(dataSnapshot);
                            if (chat.getNumberOfNewMessages() > 0){
                                menu.findItem(R.id.nav_chat).setIcon(R.drawable.ic_message_black_expl);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
