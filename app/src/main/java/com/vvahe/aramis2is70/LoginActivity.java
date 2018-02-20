package com.vvahe.aramis2is70;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText loginFieldEmail;
    private EditText loginFieldPassword;
    private Button btnLogin;
    private Button btnRegisterL;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        loginFieldEmail = (EditText) findViewById(R.id.loginFieldEmail);
        loginFieldPassword = (EditText) findViewById(R.id.loginFieldPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegisterL = (Button) findViewById(R.id.btnRegisterL);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLogin();
            }
        });

        btnRegisterL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /** send user from login page to registration page*/
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });
    }

    private void checkLogin() {

        /** Retrieve text from input fields and pass to variables*/
        String email = loginFieldEmail.getText().toString().trim();
        String password = loginFieldPassword.getText().toString().trim();

        /** check if user left any fields empty if so give Toast message*/
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {

            Toast.makeText(LoginActivity.this, "Please fill in all fields", Toast.LENGTH_LONG).show();

        } else { /** attempt sign in*/

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    /** task stores result whether sign in was successful*/
                    if (task.isSuccessful()) {

                        /** check if user exists, successful sign in is for authenticated users
                         * we also check if the user is present in the database */
                        checkUserExist();

                    } else {

                        Toast.makeText(LoginActivity.this, "Login Error", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    /** checks if user exists in database or not*/
    private void checkUserExist() {

        /** user is signed in so get user_id */
        final String user_id = mAuth.getCurrentUser().getUid();

        /** check if user_id is already present in database*/
        mDatabase.addValueEventListener(new ValueEventListener() {

            /** result is stored in dataSnapshot if user is data base, send user to dashboard*/
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(user_id)) {

                    Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);

                    /** prevents user from going back once logged in, they will have to use logout button*/
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    startActivity(loginIntent);
                } else {
                    Toast.makeText(LoginActivity.this, "User not found, please Register", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}
