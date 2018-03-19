package com.vvahe.aramis2is70;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class Message {

    public String messageID;    //messageID
    public String chatID;       //chatID from chat it belongs to
    public String userID;       //user that send this message
    public Long timeSend;       //time this message was send
    public String message;      //message from this message

    private FirebaseAuth mAuth;
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("chat");

    /*
        creates new message object, and gets all data from firebase about this message
     */
    public Message(String messageID, String chatID) {
        this.messageID = messageID;
        this.chatID = chatID;
        getMessageInfo();
    }

    /*
        creates new message object
     */
    public Message(String chatID, String userID, String message){
        this.messageID = getNewMessageID();
        this.chatID = chatID;
        this.userID = userID;
        this.timeSend = System.currentTimeMillis();
        this.message = message;
    }

    //Should we remove the Info from the get and send functions?

    /*
        gets all message info from firebase and puts it in this class
     */
    private void getMessageInfo(){
        //TODO: gets info about a specific message from firebase and puts it in this object
        DatabaseReference messageref;
        messageref = ref.child(this.chatID).child("messages").child(this.messageID);

        this.timeSend = Long.parseLong(messageref.child("timestamp").getKey());
        this.message = messageref.child("string").getKey();
        this.userID = messageref.child("user").getKey();
    }

    /*
        sends all info in this class to firebase
     */
    public void sendMessageInfo(){
        //TODO: make function, send message info to firebase
        mAuth = FirebaseAuth.getInstance();
        mAuth.getCurrentUser().getUid();

        //TODO check if this.userID is the same as the current user

        DatabaseReference messageref;
        messageref = ref.child(this.chatID).child("messages").child(this.messageID);

        messageref.child("user").setValue(this.userID);
        messageref.child("content").setValue(this.message);
        messageref.child("timestamp").setValue(this.timeSend);
    }

    /*
        returns a new possible message ID
     */
    private String getNewMessageID(){
        return UUID.randomUUID().toString();
    }

}
