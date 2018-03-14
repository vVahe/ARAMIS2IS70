package com.vvahe.aramis2is70;

import android.content.Intent;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class User {

    public String userID;                       //User ID
    public String email;                        //email address from user
    public String firstName;                    //firstname from user
    public String middleName;                   //middlename from user
    public String lastName;                     //lastname from user
    public String study;                        //study from user
    public Integer year;
    public Double locationX = 51.4472632;       //X location from user
    public Double locationY = 5.4845778;        //Y location from user
    public Integer radiusSetting = 1000;        //search radius from user
    public Boolean locationShow = true;         //user shows location
    public Boolean available = true;            //is user available
    public Boolean chatNotifications = true;    //want to get chat notifications
    public String[] enrolledInIDs = {};         //array with IDs from courses enrolled in (for firebase)
    public Course[] enrolledIn = {};            //array with courses enrolled in
    public String[] activeCoursesIDs = {};      //array with IDs from active courses (for firebase)
    public Course[] activeCourses = {};         //array with active courses
    public String[] chatsIDs = {};                 //array with IDS from chat from user (for firebase)
    public Chat[] chats = {};                   //array with chats from user

    private DatabaseReference firebaseUser = FirebaseDatabase.getInstance().getReference().child("Users");
    private DatabaseReference firebaseThisUser;

    //get existing user
    public User(String userID) {
        firebaseThisUser = firebaseUser.child(userID);
        getFromDatabase();
    }

    User user = new user();

    //for creating new user
    public User(String userID, String email, String firstName, String middleName, String lastName, String study, Integer year){
        firebaseThisUser = firebaseUser.child(userID);
        this.userID = userID;
        this.email = email;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.study = study;
        this.year = year;
        addToDatabase();
    }

    private void addToDatabase(){
        firebaseThisUser.child("email").setValue(this.email);
        firebaseThisUser.child("firstName").setValue(this.firstName);
        firebaseThisUser.child("middleName").setValue(this.middleName);
        firebaseThisUser.child("lastName").setValue(this.lastName);
        firebaseThisUser.child("study").setValue(this.study);
        firebaseThisUser.child("year").setValue(this.year);
        firebaseThisUser.child("locationX").setValue(this.locationX);
        firebaseThisUser.child("locationY").setValue(this.locationY);
        firebaseThisUser.child("radiusSetting").setValue(this.radiusSetting);
        firebaseThisUser.child("locationShow").setValue(this.locationShow);
        firebaseThisUser.child("available").setValue(this.available);
        firebaseThisUser.child("chatNotifications").setValue(this.chatNotifications);
        firebaseThisUser.child("enrolledIn").setValue(this.enrolledInIDs);
        firebaseThisUser.child("activeCourses").setValue(this.activeCoursesIDs);
        firebaseThisUser.child("chats").setValue(this.chatsIDs);
    }

    private void getFromDatabase(){
        firebaseUser.addValueEventListener(new ValueEventListener() {

            /** result is stored in dataSnapshot if user is data base, send user to dashboard*/
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(this.userID)) {

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

        firebaseThisUser.getValue
        firebaseThisUser.child("firstName").setValue(this.firstName);
        firebaseThisUser.child("middleName").setValue(this.middleName);
        firebaseThisUser.child("lastName").setValue(this.lastName);
        firebaseThisUser.child("locationX").setValue(this.locationX);
        firebaseThisUser.child("locationY").setValue(this.locationY);
        firebaseThisUser.child("radiusSetting").setValue(this.radiusSetting);
        firebaseThisUser.child("locationShow").setValue(this.locationShow);
        firebaseThisUser.child("available").setValue(this.available);
        firebaseThisUser.child("chatNotifications").setValue(this.chatNotifications);
        firebaseThisUser.child("enrolledIn").setValue(this.enrolledInIDs);
        firebaseThisUser.child("activeCourses").setValue(this.activeCoursesIDs);
        firebaseThisUser.child("chats").setValue(this.chatsIDs);
    }


    // Get data from Firebase
    public boolean getUserInfo(){


        return true; //return true if succesful
    }

    // Send data to Firebase
    public boolean storeUserInfo(){


        return true; //return true if succesful
    }




}
