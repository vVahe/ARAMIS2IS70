package com.vvahe.aramis2is70;

import com.google.android.gms.vision.text.Line;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Time;
import java.util.ArrayList;
import java.util.UUID;

public class Chat {

    public String chatID;                                               //chat ID
    public String otherUserID;                                          //other user
    public ArrayList<Message> messages = new ArrayList<>();             //all messages send

    private DatabaseReference firebaseChat = FirebaseDatabase.getInstance().getReference().child("chats");
    public DatabaseReference firebaseThisChat;

    private User mainUser = User.getInstance();

    /*
        creates new chat object, and puts it in firebase
     */
    public Chat(String otherUserID){
        this.chatID = getChatID(otherUserID);
        firebaseThisChat = firebaseChat.child(this.chatID);
        this.otherUserID = otherUserID;
    }

    /*
    send a message in this chat
    */
    public void sendMessage(String message){
        Message m = new Message(UUID.randomUUID().toString(), mainUser.userID, message, System.currentTimeMillis(), firebaseThisChat.child("messages"));
        m.send();
        messages.add(m);
    }

    public void setDataInMessages(DataSnapshot dataSnapshot){
        for(DataSnapshot chat : dataSnapshot.getChildren()){
            String message = "";
            String userID = "";
            Long timeSend = new Long(0);
            for(DataSnapshot ds : chat.getChildren()) {
                if (ds.getKey().equals("string")) {
                    message = ds.getValue(String.class);
                } else if (ds.getKey().equals("user")) {
                    userID = ds.getValue(String.class);
                } else if (ds.getKey().equals("timeSend")) {
                    timeSend = ds.getValue(Long.class);
                } else {
                    //what else to store for message
                }
            }
            Message newMessage = new Message(chat.getKey(), userID, message, timeSend, firebaseThisChat.child("messages"));
            messages.add(newMessage);
        }
    }


    /*
       creates the chat ID
    */
    public String getChatID(String otherUserID){
        //chat id is userID1 + userID2 (alphabetic order)
        if (mainUser.userID.compareTo(otherUserID) < 0){
            return mainUser.userID + otherUserID;
        } else {
            return otherUserID + mainUser.userID;
        }
    }
}