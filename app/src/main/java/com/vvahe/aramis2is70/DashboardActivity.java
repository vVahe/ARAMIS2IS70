package com.vvahe.aramis2is70;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class DashboardActivity extends AppCompatActivity {

    private TextView txtHome;
    private TextView txtUser;
    private Button btnLogout;
    private String userName;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        txtHome = (TextView) findViewById(R.id.txtHome);
        btnLogout = (Button) findViewById(R.id.btnLogout);

        mAuth = FirebaseAuth.getInstance();
        /** checks if user is logged in not*/
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                /** firebaseAuth returns result whether logged in or not
                 * if user is not logged in redirect to Register page using intent*/
                if (firebaseAuth.getCurrentUser() == null) {

                    Intent loginIntent = new Intent(DashboardActivity.this, LoginActivity.class);

                    /** prevents user from going back once logged in, they will have to use logout button*/
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    startActivity(loginIntent);


                }
            }
        };

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        //userName = mAuth.getCurrentUser().getDisplayName().toString();

        //txtUser.setText(userName);


    }

    /** Logout user*/
    private void logout() {
        mAuth.signOut();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }
}
