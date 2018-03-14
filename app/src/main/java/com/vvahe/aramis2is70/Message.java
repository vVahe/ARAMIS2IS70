package com.vvahe.aramis2is70;

import java.sql.Time;

public class Message {

    public String messageID;    //messageID
    public String chatID;       //chatID from chat it belongs to
    public String userID;       //user that send this message
    public Long timeSend;       //time this message was send
    public String message;      //message from this message

    /*
        creates new message object, and gets all data from firebase about this message
     */
    public Message(String messageID, String chatID) {
        this.messageID = messageID;
        this.chatID = chatID;
        getMessageInfo();
    }

    /*
        creates new message object, and sends data to firebase
     */
    public Message(String chatID, String userID, String message){
        this.messageID = getNewMessageID();
        this.chatID = chatID;
        this.userID = userID;
        this.timeSend = System.currentTimeMillis();
        this.message = message;
        sendMessageInfo();
    }

    /*
        gets all message info from firebase and puts it in this class
     */
    private void getMessageInfo(){
        //TODO: gets info about a specific message from firebase and puts it in this class

    }

    /*
        sends all info in this class to firebase
     */
    private void sendMessageInfo(){
        //TODO: make function, send message info to firebase

    }

    /*
        returns a new possible message ID
     */
    private String getNewMessageID(){
        //TODO: make function, return new possible ID for message (look up highest message id in firebase and add 1)


        return "0";
    }

}
