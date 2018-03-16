package com.vvahe.aramis2is70;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {

    private TextView txtUser;
    private Button btnLogout;
    private String userName;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    /** put code that should run on start up in onViewCreated */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnLogout = (Button) getView().findViewById(R.id.btnLogout);
        txtUser = (TextView) getView().findViewById(R.id.txtName);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();

        /** checks if user is logged in not*/
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                /** FirebaseAuth returns result whether logged in or not
                 * if user is not logged in redirect to login page using intent*/
                if (firebaseAuth.getCurrentUser() == null) {

                    Intent loginIntent = new Intent(getActivity(), LoginActivity.class);

                    /** prevents user from going back once logged in, they will have to use logout button*/
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    startActivity(loginIntent);

                } else { /** user is logged in so display username on screen */

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

                userName = dataSnapshot.child("firstName").getValue().toString();
                txtUser.setText(userName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    /** Logout user*/
    private void logout() {
        mAuth.signOut();

//        Intent logoutIntent = new Intent(, LoginActivity.class);
//
//        /** prevents user from going back once logged in, they will have to use logout button*/
//        logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        startActivity(logoutIntent);
    }

    @Override
    public void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }

}
