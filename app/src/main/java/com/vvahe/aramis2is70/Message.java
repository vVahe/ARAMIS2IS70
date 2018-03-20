package com.vvahe.aramis2is70;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class Message {

    public String messageID;    //messageID
    public String userID;       //user that send this message
    public Long timeSend;       //time this message was send
    public String message;      //message from this message

    private FirebaseAuth mAuth;
    private DatabaseReference firebaseThisChat = FirebaseDatabase.getInstance().getReference().child("chats");
    private DatabaseReference firebaseThisMessage;

    /*
        creates new message object
     */
    public Message(String messageID, String userID, String message, Long timeSend, DatabaseReference firebaseThisChat){
        this.messageID = messageID;
        this.userID = userID;
        this.timeSend = timeSend;
        this.message = message;
        this.firebaseThisMessage = firebaseThisChat.child(messageID);
    }

    /*
        sends all info in this class to firebase
     */
    public void send(){
        firebaseThisMessage.child("user").setValue(this.userID);
        firebaseThisMessage.child("timeSend").setValue(this.timeSend);
        firebaseThisMessage.child("string").setValue(this.message);
    }

}
