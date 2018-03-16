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

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    private String firstName;
    private String middleName;
    private String lastName;
    private String study;
    private Integer year;
    private String email;
    private String emailRepeat;
    private String password;
    private String passwordRepeat;
    public Double locationX = 51.4472632;                                   //X location from user
    public Double locationY = 5.4845778;                                    //Y location from user
    public Integer radiusSetting = 1000;                                    //search radius from user
    public Boolean locationShow = true;                                     //user shows location
    public Boolean available = true;                                        //is user available
    public Boolean chatNotifications = true;                                //want to get chat notifications
    public ArrayList<String> enrolledInIDs = new ArrayList<String>();       //array with IDs from courses enrolled in (for firebase)
    public ArrayList<String> activeCoursesIDs = new ArrayList<String>();    //array with IDs from active courses (for firebase)
    public ArrayList<String> chatsIDs = new ArrayList<String>();            //array with IDS from chat from user (for firebase)

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

        firstName = firstNameField.getText().toString().trim();
        middleName = middleNameField.getText().toString().trim();
        lastName = lastNameField.getText().toString().trim();
        study = studyField.getText().toString().trim();
        year = Integer.parseInt(yearField.getText().toString().trim());
        email = emailField.getText().toString().trim();
        emailRepeat = emailRepeatField.getText().toString().trim();
        password = passwordField.getText().toString().trim();
        passwordRepeat = passwordRepeatField.getText().toString().trim();

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

                        createrUserInDB();
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

    private void createrUserInDB() {
        DatabaseReference usersNode = FirebaseDatabase.getInstance().getReference().child("Users");
        usersNode.child("email").setValue(email);
        usersNode.child("firstName").setValue(firstName);
        usersNode.child("middleName").setValue(middleName);
        usersNode.child("lastName").setValue(lastName);
        usersNode.child("study").setValue(study);
        usersNode.child("year").setValue(year);
        usersNode.child("locationX").setValue(locationX);
        usersNode.child("locationY").setValue(locationY);
        usersNode.child("radiusSetting").setValue(radiusSetting);
        usersNode.child("locationShow").setValue(locationShow);
        usersNode.child("available").setValue(available);
        usersNode.child("chatNotifications").setValue(chatNotifications);
        usersNode.child("enrolledIn").setValue(enrolledInIDs);
        usersNode.child("activeCourses").setValue(activeCoursesIDs);
        usersNode.child("chats").setValue(chatsIDs);
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
