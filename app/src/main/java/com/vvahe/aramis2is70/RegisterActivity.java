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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameField;
    private EditText emailField;
    private EditText passwordField;

    private Button btnRegister;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        /** get reference to root directory of database: https://aramis-2is70.firebaseio.com/
         * .child("Users") adds a child containing users to the root*/
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        nameField = (EditText) findViewById(R.id.nameField);
        emailField = (EditText) findViewById(R.id.emailField);
        passwordField = (EditText) findViewById(R.id.passwordField);

        btnRegister = (Button) findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startRegister();

            }
        });
    }

    private void startRegister() {

        final String name = nameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        /** check if any fields are left empty */
        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {

            Toast.makeText(RegisterActivity.this, "Please fill in all fields", Toast.LENGTH_LONG).show();

        } else {

            /** creates user with given email & password, and check if task was successful*/
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    /** if registration is successful add user info to database*/
                    if (task.isSuccessful()) {

                        /** if registration was successful we can retrieve the unique user_id */
                        String user_id = mAuth.getCurrentUser().getUid();

                        /** when using FirebaseAuthentication to register, an user is added in the
                         * authentication tab, but not yet to the database, so we also create a user
                         * in the data base while registering below*/
                        DatabaseReference current_user_db = mDatabase.child(user_id);

                        current_user_db.child("name").setValue(name);

                        /** send user to dashboard after registration is complete*/
                        Intent dashboardIntent = new Intent(RegisterActivity.this, MainActivity.class);
                        dashboardIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(dashboardIntent);

                    } else { /** if registration is not successful add give error */

                        Toast.makeText(RegisterActivity.this, "Something went wrong :/", Toast.LENGTH_LONG).show();

                        /** TO-DO: -------------------
                         *
                         * if registration fails we can add some code to notify if the user why,
                         * for example, match the "Name" filled in by the user to the user names already
                         * present in the database add give "username taken" warning, same goes for email*/

                    }


                }
            });

        }
    }
}
