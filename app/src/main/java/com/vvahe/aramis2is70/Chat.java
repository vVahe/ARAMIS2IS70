package com.vvahe.aramis2is70;

import com.google.android.gms.vision.text.Line;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class Chat {

    public String chatID;                                               //chat ID
    public String otherUserID;                                          //other user
    public ArrayList<Message> messages = new ArrayList<>();             //all messages send

    private DatabaseReference firebaseChat = FirebaseDatabase.getInstance().getReference().child("chats");
    public DatabaseReference firebaseThisChat;

    private DatabaseReference firebaseUser = FirebaseDatabase.getInstance().getReference().child("Users"); //database reference to users
    private DatabaseReference firebaseOtherUser; //database reference to this user
    public ArrayList<String> otherChatsIDs = new ArrayList<String>();

    private User mainUser = User.getInstance();

    /*
        creates new chat object, and puts it in firebase
     */
    public Chat(String otherUserID){
        this.chatID = getChatID(otherUserID);
        firebaseThisChat = firebaseChat.child(this.chatID);
        firebaseOtherUser = firebaseUser.child(otherUserID).child("chats");
        this.otherUserID = otherUserID;
    }

    /*
    send a message in this chat
    */
    public void sendMessage(String message){
        Message m = new Message(UUID.randomUUID().toString(), mainUser.userID, message, ServerValue.TIMESTAMP, firebaseThisChat.child("messages"));
        m.send();
        messages.add(m);
        setInOtherUser();
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

    /*
       if new message is send to chat, put this chat id in the "chats" of the other user in database.
    */
    private void setInOtherUser(){
        firebaseOtherUser.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                otherChatsIDs.clear();

                for(DataSnapshot chat : dataSnapshot.getChildren()){
                    otherChatsIDs.add(chat.getValue(String.class));
                }

                if (!(otherChatsIDs.contains(chatID))){
                    otherChatsIDs.add(chatID);
                }
                firebaseOtherUser.setValue(otherChatsIDs);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}