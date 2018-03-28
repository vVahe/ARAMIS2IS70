package com.vvahe.aramis2is70;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText firstNameField;
    private EditText middleNameField;
    private EditText lastNameField;
    private EditText studyField;
    private EditText yearField;
    private EditText emailField;
    private EditText emailRepeatField;
    private EditText passwordField;
    private EditText passwordRepeatField;

    private Button btnRegister;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        firstNameField = findViewById(R.id.firstNameField);
        middleNameField = findViewById(R.id.middleNameField);
        lastNameField = findViewById(R.id.lastNameField);
        studyField = findViewById(R.id.studyField);
        yearField = findViewById(R.id.yearField);
        emailField = findViewById(R.id.emailField);
        emailRepeatField = findViewById(R.id.emailRepeatField);
        passwordField = findViewById(R.id.passwordField);
        passwordRepeatField = findViewById(R.id.passwordRepeatField);

        btnRegister = findViewById(R.id.btnRegister);

        /* get reference to root directory of database: https://aramis-2is70.firebaseio.com/
         * .child("Users") adds a child containing users to the root
         * maybe not needed? create these nodes in firebase manually */
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }
        });
    }

    private void startRegister() {

        final String firstName = firstNameField.getText().toString().trim();
        final String middleName = middleNameField.getText().toString().trim();
        final String lastName = lastNameField.getText().toString().trim();
        final String study = studyField.getText().toString().trim();
        final Integer year = Integer.parseInt(yearField.getText().toString().trim());
        final String email = emailField.getText().toString().trim();
        final String emailRepeat = emailRepeatField.getText().toString().trim();
        final String password = passwordField.getText().toString().trim();
        final String passwordRepeat = passwordRepeatField.getText().toString().trim();

        if(checkForEmptyInputFields(firstName, lastName, study, year, email, emailRepeat, password, passwordRepeat)) {
            Toast.makeText(RegisterActivity.this, "Please fill in all fields", Toast.LENGTH_LONG).show();
        } else {

            /* creates user with given email & password, and check if task was successful*/
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    /* if registration is successful add user info to database*/
                    if (task.isSuccessful()) {

                        String uID = mAuth.getCurrentUser().getUid();

                        User user = User.getInstance();
                        user.register(uID, email, firstName, middleName, lastName, study, year);
                        user.context = getApplicationContext();

                        /* if registration was successful we can retrieve the unique user_id */

                        /* send user to dashboard after registration is complete*/
                        Intent dashboardIntent = new Intent(RegisterActivity.this, MainActivity.class);
                        dashboardIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(dashboardIntent);

                    } else { /* if registration is not successful add give error */

                        Toast.makeText(RegisterActivity.this, "Something went wrong :/", Toast.LENGTH_LONG).show();

                        /* TO-DO: -------------------
                         *
                         * if registration fails we can add some code to notify to the user why,
                         * for example, match the "Name" filled in by the user to the user names already
                         * present in the database add give "username taken" warning, same goes for email*/

                    }


                }
            });

        }
    }

    public boolean checkForEmptyInputFields(String firstName, String lastName, String study,
                                            Integer year, String email, String emailRepeat,
                                            String password, String passwordRepeat) {

        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(study) ||
                TextUtils.isEmpty(year.toString()) || TextUtils.isEmpty(email) || TextUtils.isEmpty(emailRepeat) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(passwordRepeat)) {
            return true;
        }
        return false;
    }
}
