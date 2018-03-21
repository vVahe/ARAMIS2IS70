package com.vvahe.aramis2is70;

import android.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class User {

    private static User mainUser = new User();

    public String userID = "";                                              //User ID
    public String email = "";                                               //email address from user
    public String firstName = "";                                           //firstname from user
    public String middleName = "";                                          //middlename from user
    public String lastName = "";                                            //lastname from user
    public String study = "";                                               //study from user
    public Integer year = 9999;                                             //studyyear from user
    public Double locationX = 51.4472632;                                   //X location from user
    public Double locationY = 5.4845778;                                    //Y location from user
    public Integer radiusSetting = 1000;                                    //search radius from user
    public Boolean locationShow = true;                                     //user shows location
    public Boolean available = true;                                        //is user available
    public Boolean chatNotifications = true;                                //want to get chat notifications
    public String selectedCourse = "";                                      //the selected course
    public ArrayList<String> enrolledInIDs = new ArrayList<String>();       //array with IDs from courses enrolled in (for firebase)
    public ArrayList<String> chatsIDs = new ArrayList<String>();            //array with IDS from chat from user (for firebase)

    public boolean userCreated = false;

    private DatabaseReference firebaseUser = FirebaseDatabase.getInstance().getReference().child("Users"); //database reference to users
    private DatabaseReference firebaseThisUser; //database reference to this user


    private User() { }

    /* Static 'instance' method */
    public static User getInstance( ) {
        return mainUser;
    }

    /*
        creates a new user class and gets data from firebase about the user
     */
    public void getData(String userID) {
        firebaseThisUser = firebaseUser.child(userID);
        this.userID = userID;
        addFirebaseListener();
        userCreated = true;
    }

    /*
        creates a new user class and a new user in the database
     */
    public void register(String userID, String email, String firstName, String middleName, String lastName, String study, Integer year){
        firebaseThisUser = firebaseUser.child(userID);
        this.userID = userID;
        this.email = email;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.study = study;
        this.year = year;
        userCreated = true;
        addToDatabase();
        addFirebaseListener();
    }

    /*
        create a chat with a user, in this class and in database
     */
    public Chat openChat(String otherUserID){
        Chat chat = new Chat(otherUserID);
        chatsIDs.add(chat.chatID);
        firebaseThisUser.child("chats").setValue(this.chatsIDs);
        return chat;
    }

    /*
        delete a chat, in this class and in database
     */
    public void deleteChat(String chatID) {
        chatsIDs.remove(chatID);
        firebaseThisUser.child("chats").setValue(this.chatsIDs);
    }

    /*
        adds a course, in this class and in database
     */
    public void addEnrolledCourse(String courseCode){
        enrolledInIDs.add(courseCode);
        firebaseThisUser.child("enrolledIn").setValue(this.enrolledInIDs);
    }

    /*
        removes a course, in this class and in database
     */
    public void removeEnrolledCourse(String courseCode){
        enrolledInIDs.remove(courseCode);
        firebaseThisUser.child("enrolledIn").setValue(this.enrolledInIDs);
    }

    /*
        set a course as active
     */
    public void setSelectedCourse(String courseCode){
        selectedCourse = courseCode;
        firebaseThisUser.child("selectedCourse").setValue(selectedCourse);
    }

    /*
        unset the selected course
     */
    public void unsetSelectedCourse(){
        selectedCourse = "";
        firebaseThisUser.child("selectedCourse").setValue(selectedCourse);
    }


    /*
        output data from class to log, only for testing purposes
     */
    public void outputToLog(){
        Log.wtf("email", email);
        Log.wtf("firstName", firstName);
        Log.wtf("middleName", middleName);
        Log.wtf("lastName", lastName);
        Log.wtf("study", study);
        Log.wtf("year", year.toString());
        Log.wtf("locationX", locationX.toString());
        Log.wtf("locationY", locationY.toString());
        Log.wtf("radiusSetting", radiusSetting.toString());
        Log.wtf("locationShow", locationShow.toString());
        Log.wtf("available", available.toString());
        Log.wtf("chatNotifications", chatNotifications.toString());
        Log.wtf("enrolledInIDs", enrolledInIDs.toString());
        Log.wtf("selectedCourse", selectedCourse.toString());
        Log.wtf("chatsIDs", chatsIDs.toString());
    }


    /*
        update firebase with data of this class
     */
    public void addToDatabase(){
        if (userCreated == false){
            return;
        }
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
        firebaseThisUser.child("selectedCourse").setValue(this.selectedCourse);
        firebaseThisUser.child("chats").setValue(this.chatsIDs);
    }

    /*
        gets all data from a user from firebase
     */
    private void setDataInClass(DataSnapshot dataSnapshot){
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            if (ds.getKey().equals("email")){
                email = ds.getValue(String.class);
            } else if (ds.getKey().equals("firstName")){
                firstName = ds.getValue(String.class);
            } else if (ds.getKey().equals("middleName")){
                middleName = ds.getValue(String.class);
            } else if (ds.getKey().equals("lastName")){
                lastName = ds.getValue(String.class);
            } else if (ds.getKey().equals("study")){
                study = ds.getValue(String.class);
            } else if (ds.getKey().equals("year")){
                year = ds.getValue(Integer.class);
            } else if (ds.getKey().equals("locationX")){
                locationX = ds.getValue(Double.class);
            } else if (ds.getKey().equals("locationY")){
                locationY = ds.getValue(Double.class);
            } else if (ds.getKey().equals("radiusSetting")){
                radiusSetting = ds.getValue(Integer.class);
            } else if (ds.getKey().equals("locationShow")){
                locationShow = ds.getValue(Boolean.class);
            } else if (ds.getKey().equals("available")){
                available = ds.getValue(Boolean.class);
            } else if (ds.getKey().equals("chatNotifications")){
                chatNotifications = ds.getValue(Boolean.class);
            } else if (ds.getKey().equals("selectedCourse")){
                selectedCourse = ds.getValue(String.class);
            } else if (ds.getKey().equals("enrolledIn")){
                enrolledInIDs.clear();
                for(DataSnapshot dsChild : ds.getChildren()) {
                    enrolledInIDs.clear();
                    enrolledInIDs.add(dsChild.getValue(String.class));
                }
            } else if (ds.getKey().equals("chats")){
                chatsIDs.clear();
                for(DataSnapshot dsChild : ds.getChildren()) {
                    chatsIDs.add(dsChild.getValue(String.class));
                }
            }
        }
    }

    /*
        add the value event listener
     */
    private void addFirebaseListener(){
        firebaseThisUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setDataInClass(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}