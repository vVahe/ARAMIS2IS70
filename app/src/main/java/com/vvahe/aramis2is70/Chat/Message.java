package com.vvahe.aramis2is70.Chat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import android.util.Log;

import java.security.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

public class Message {

    public String messageID;    //messageID
    public String userID;       //user that send this message
    public Map<String, String> timeSend;       //time this message was send
    public Long timeSendLong = new Long(0);
    public String message;      //contents of this message
    public Boolean hasRed;

    public DatabaseReference firebaseThisMessage;

    /*
        creates new message object
     */
    public Message(String messageID, String userID, String message, Map<String, String> timeSend, DatabaseReference firebaseThisChat){
        this.messageID = messageID;
        this.userID = userID;
        this.timeSend = timeSend;
        this.message = message;
        this.firebaseThisMessage = firebaseThisChat.child(messageID);
        this.hasRed = false;
    }

    /*
        creates new message object
     */
    public Message(String messageID, String userID, String message, Long timeSend, Boolean hasRed, DatabaseReference firebaseThisChat){
        this.messageID = messageID;
        this.userID = userID;
        this.timeSendLong = timeSend;
        this.message = message;
        this.firebaseThisMessage = firebaseThisChat.child(messageID);
        this.hasRed = hasRed;
    }

    /*
        sends all info in this class to firebase
     */
    public void send(){
        firebaseThisMessage.child("user").setValue(this.userID);
        firebaseThisMessage.child("timeSend").setValue(this.timeSend);
        firebaseThisMessage.child("string").setValue(this.message);
        firebaseThisMessage.child("hasRed").setValue(this.hasRed);
    }

    public String getTime(){
        Date date = new Date(timeSendLong);
        DateFormat format = new SimpleDateFormat("HH:mm");

        format.setTimeZone(TimeZone.getTimeZone("Europe/Amsterdam"));
        String formatted = format.format(date);

        return formatted;
    }

}
