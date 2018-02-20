package com.vvahe.aramis2is70;

import android.app.Activity;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashboardActivity extends AppCompatActivity {

    private TextView txtHome;
    private TextView txtUser;
    private Button btnLogout;
    private String userName;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        txtHome = (TextView) findViewById(R.id.txtHome);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        txtUser = (TextView) findViewById(R.id.txtUser);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
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

                } else {

                    getUserName();

                }
            }
        };

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    /** Get username of currently logged in user and set on TextView*/
    private void getUserName() {

        /** get user id of current user*/
        final String user_id = mAuth.getCurrentUser().getUid();


        mDatabase.child(user_id).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userName = dataSnapshot.child("name").getValue().toString();
                txtUser.setText(userName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
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
