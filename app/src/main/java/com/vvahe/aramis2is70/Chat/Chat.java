package com.vvahe.aramis2is70.Chat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.vvahe.aramis2is70.User;

import java.util.ArrayList;
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
        mainUser.setChatToTop(chatID);
        setInOtherUser();
    }

    public void setDataInMessages(DataSnapshot dataSnapshot){
        messages.clear();
        for(DataSnapshot chat : dataSnapshot.getChildren()){
            String message = "";
            String userID = "";
            Long timeSend = new Long(0);
            Boolean hasRead = false;
            for(DataSnapshot ds : chat.getChildren()) {
                if (ds.getKey().equals("string")) {
                    message = ds.getValue(String.class);
                } else if (ds.getKey().equals("user")) {
                    userID = ds.getValue(String.class);
                } else if (ds.getKey().equals("timeSend")) {
                    timeSend = ds.getValue(Long.class);
                } else if (ds.getKey().equals("hasRead")){
                    hasRead = ds.getValue(Boolean.class);
                } else {

                }
            }
            Message newMessage = new Message(chat.getKey(), userID, message, timeSend, hasRead, firebaseThisChat.child("messages"));
            messages.add(newMessage);
        }
    }

    public Integer getNumberOfNewMessages(){
        Integer counter = 0;
        for (Message message : messages){
            if (!(message.userID.equals(mainUser.userID)) && !(message.hasRead)){
                counter++;
            }
        }
        return counter;
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
                otherChatsIDs.add(chatID);
                for(DataSnapshot chat : dataSnapshot.getChildren()){
                    if (!(chat.getValue(String.class).equals(chatID))) {
                        otherChatsIDs.add(chat.getValue(String.class));
                    }
                }
                firebaseOtherUser.setValue(otherChatsIDs);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}