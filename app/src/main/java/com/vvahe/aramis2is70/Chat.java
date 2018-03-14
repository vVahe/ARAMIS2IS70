package com.vvahe.aramis2is70;

import java.sql.Time;

public class Chat {

    public String chatID;      //chat ID
    public User user1;          //user 1
    public User user2;          //user 2
    public Time timeCreated;    //time chat was created

    public Chat(String chatID) {

    }

    // Get messages from Firebase and return as array of Line objects
    public Line[] getMessages(){
        Line line1 = new Line();
        Line line2 = new Line();
        Line[] messages = {line1, line2};



        return messages; //return array of Line objects
    }

}