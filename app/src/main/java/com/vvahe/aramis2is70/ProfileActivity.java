package com.vvahe.aramis2is70;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private static ImageButton back;
    private static EditText firstName;
    private static EditText middleName;
    private static EditText lastName;
    private static EditText study;
    private static EditText email;
    private static EditText year;
    private User userObj = User.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        back = (ImageButton)findViewById(R.id.backButton);
        firstName = (EditText)findViewById(R.id.profileFieldFirstname) ;
        middleName = (EditText)findViewById (R.id.profileFieldMiddlename);
        lastName = (EditText)findViewById (R.id.profileFieldLastname);
        email = (EditText)findViewById (R.id.profileFieldEmail);
        study = (EditText)findViewById (R.id.profileFieldStudy);
        year = (EditText)findViewById (R.id.profileFieldYear);

        userObj.firebaseThisUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if (ds.getKey().equals("firstName")){
                        Integer cursor = firstName.getSelectionStart();
                        firstName.setText(ds.getValue(String.class));
                        firstName.setSelection(cursor);
                    } else if (ds.getKey().equals("middleName")){
                        Integer cursor = middleName.getSelectionStart();
                        middleName.setText(ds.getValue(String.class));
                        middleName.setSelection(cursor);
                    } else if (ds.getKey().equals("lastName")){
                        Integer cursor = lastName.getSelectionStart();
                        lastName.setText(ds.getValue(String.class));
                        lastName.setSelection(cursor);
                    } else if (ds.getKey().equals("study")){
                        Integer cursor = study.getSelectionStart();
                        study.setText(ds.getValue(String.class));
                        study.setSelection(cursor);
                    } else if (ds.getKey().equals("email")){
                        Integer cursor = email.getSelectionStart();
                        email.setText(ds.getValue(String.class));
                        email.setSelection(cursor);
                    } else if (ds.getKey().equals("year")){
                        Integer cursor = year.getSelectionStart();
                        year.setText(ds.getValue(Integer.class).toString());
                        year.setSelection(cursor);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        firstName();
        middleName();
        lastName();
        study();
        email();
        year();
        back();
    }

    public void firstName(){
        firstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                userObj.setFirstName(s.toString());
            }
        });
    }

    public void middleName(){
        middleName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                userObj.setMiddleName(s.toString());
            }
        });
    }

    public void lastName(){
        lastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                userObj.setLastName(s.toString());
            }
        });
    }

    public void study(){
        study.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                userObj.setStudy(s.toString());
            }
        });
    }

    public void year(){
        year.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                userObj.setYear(Integer.parseInt(s.toString()));
            }
        });
    }

    public void email(){
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                userObj.setEmail(s.toString());
            }
        });
    }

    public void back(){
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
