package com.vvahe.aramis2is70;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class User {

    //single user object
    private static User user = new User();

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in

            } else {
                userID = mAuth.getCurrentUser().getUid();
            }
        }
    };

    public String userID;                                                   //User ID
    public String email;                                                    //email address from user
    public String firstName;                                                //firstname from user
    public String middleName;                                               //middlename from user
    public String lastName;                                                 //lastname from user
    public String study;                                                    //study from user
    public Integer year;                                                    //studyyear from user
    public Double locationX = 51.4472632;                                   //X location from user
    public Double locationY = 5.4845778;                                    //Y location from user
    public Integer radiusSetting = 1000;                                    //search radius from user
    public Boolean locationShow = true;                                     //user shows location
    public Boolean available = true;                                        //is user available
    public Boolean chatNotifications = true;                                //want to get chat notifications
    public ArrayList<String> enrolledInIDs = new ArrayList<String>();       //array with IDs from courses enrolled in (for firebase)
    public ArrayList<String> activeCoursesIDs = new ArrayList<String>();    //array with IDs from active courses (for firebase)
    public ArrayList<String> chatsIDs = new ArrayList<String>();            //array with IDS from chat from user (for firebase)

    //reference to users node in database
    private DatabaseReference usersNode = FirebaseDatabase.getInstance().getReference().child("Users");
    //reference to the currently logged in user
    private DatabaseReference currentUser = usersNode.child(userID).getRef();

    /*
        Empty constructor for referencing other classes
     */
    private User () {}

    public static User getInstance() {
        if (user == null) {
            user = new User();
        }
        return user;
    }

    /*
        update firebase with data of this class
     */
    public void addToDatabase(){
        currentUser.child("email").setValue(this.email);
        currentUser.child("firstName").setValue(this.firstName);
        currentUser.child("middleName").setValue(this.middleName);
        currentUser.child("lastName").setValue(this.lastName);
        currentUser.child("study").setValue(this.study);
        currentUser.child("year").setValue(this.year);
        currentUser.child("locationX").setValue(this.locationX);
        currentUser.child("locationY").setValue(this.locationY);
        currentUser.child("radiusSetting").setValue(this.radiusSetting);
        currentUser.child("locationShow").setValue(this.locationShow);
        currentUser.child("available").setValue(this.available);
        currentUser.child("chatNotifications").setValue(this.chatNotifications);
        currentUser.child("enrolledIn").setValue(this.enrolledInIDs);
        currentUser.child("activeCourses").setValue(this.activeCoursesIDs);
        currentUser.child("chats").setValue(this.chatsIDs);
    }

    /*
        gets all data from a user from firebase
     */
    private void setDataInClass(DataSnapshot dataSnapshot){
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            this.email              = (String) ds.child("email").getValue();
            this.firstName          = (String) ds.child("firstName").getValue();
            this.middleName         = (String) ds.child("middleName").getValue();
            this.lastName           = (String) ds.child("lastName").getValue();
            this.study              = (String) ds.child("study").getValue();
            this.year               = (Integer) ds.child("year").getValue();
            this.locationX          = (Double) ds.child("locationX").getValue();
            this.locationY          = (Double) ds.child("locationY").getValue();
            this.radiusSetting      = (Integer) ds.child("radiusSetting").getValue();
            this.locationShow       = (Boolean) ds.child("locationShow").getValue();
            this.available          = (Boolean) ds.child("available").getValue();
            this.chatNotifications  = (Boolean) ds.child("chatNotifications").getValue();
            this.enrolledInIDs      = (ArrayList<String>) ds.child("enrolledIn").getValue();
            this.activeCoursesIDs   = (ArrayList<String>) ds.child("activeCourses").getValue();
            this.chatsIDs           = (ArrayList<String>) ds.child("chats").getValue();
        }
    }

    public void setUserInfo(String userID) {
        currentUser.getRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setDataInClass(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    };

    /*
        create a chat with a user, in this class and in database
     */
    public void createChat(String otherUserID){
        Chat chat = new Chat(this.userID, otherUserID);
        chatsIDs.add(chat.chatID);
        currentUser.child("chats").setValue(this.chatsIDs);
    }

    /*
        delete a chat, in this class and in database
     */
    public void deleteChat(String chatID) {
        chatsIDs.remove(chatID);
        currentUser.child("chats").setValue(this.chatsIDs);
    }

    /*
        adds a course, in this class and in database
     */
    public void addEnrolledCourse(String courseCode){
        enrolledInIDs.add(courseCode);
        currentUser.child("enrolledIn").setValue(this.enrolledInIDs);
    }

    /*
        removes a course, in this class and in database
     */
    public void removeEnrolledCourse(String courseCode){
        enrolledInIDs.remove(courseCode);
        currentUser.child("enrolledIn").setValue(this.enrolledInIDs);
    }

    /*
        adds a course as active, in this class and in database
     */
    public void addActiveCourse(String courseCode){
        activeCoursesIDs.add(courseCode);
        currentUser.child("activeCourses").setValue(this.activeCoursesIDs);
    }

    /*
        removes a course as active, in this class and in database
     */
    public void removeActiveCourse(String courseCode){
        activeCoursesIDs.remove(courseCode);
        currentUser.child("activeCourses").setValue(this.activeCoursesIDs);
    }

    /*
        returns true if this user is in range, false if not in range
     */
    public boolean isInRange(double locationX, double locationY, int radius){
        double lowBoundX = locationX - radius;
        double upBoundX = locationX + radius;
        double lowBoundY = locationY - radius;
        double upBoundY = locationY + radius;
        return ((this.locationX > lowBoundX) && (this.locationX < upBoundX) && (this.locationY < lowBoundY) && (this.locationY < upBoundY));
    }
}