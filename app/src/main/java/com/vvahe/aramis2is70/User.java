package com.vvahe.aramis2is70;

public class User {

    public Integer userID;              //User ID
    public String email;                //email address from user
    public Integer UniID;               //University identifier
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

    public User() {

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