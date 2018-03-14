package com.vvahe.aramis2is70;

import android.provider.ContactsContract;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class User {

    public String userID;              //User ID
    public String email;                //email address from user
    public String firstName;            //firstname from user
    public String middleName;           //middlename from user
    public String lastName;             //lastname from user
    public Double locationX;            //X location from user
    public Double locationY;            //Y location from user
    public Integer radiusSetting;       //search radius from user
    public Boolean locationShow;        //user shows location
    public Boolean available;           //is user available
    public Boolean chatNotifications;   //want to get chat notifications
    public Course[] enrolledIn;         //array with courses enrolled in
    public Course[] activeCourses;      //array with active courses
    public Chat[] chats;                //array with chats from user

    private DatabaseReference firebaseUser = FirebaseDatabase.getInstance().getReference().child("Users");

    //get existing user
    public User(String userID) {

    }

    //for creating new user
    public User(String userID, String email, String firstName, String middleName, String lastName){
        DatabaseReference newUser = firebaseUser.child(userID);
        newUser.child("email").setValue(email);
        newUser.child("firstName").setValue(firstName);
        newUser.child("middleName").setValue(middleName);
        newUser.child("lastName").setValue(lastName);
        newUser.child("locationX").setValue(0);
        newUser.child("locationY").setValue(0);
        newUser.child("radiusSetting").setValue(1000);
        newUser.child("locationShow").setValue(true);
        newUser.child("available").setValue(true);
        newUser.child("chatNotifications").setValue(true);
        newUser.child("enrolledIn").setValue("{}");
        newUser.child("activeCourses").setValue("{}");
        newUser.child("chats").setValue("{}");

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
