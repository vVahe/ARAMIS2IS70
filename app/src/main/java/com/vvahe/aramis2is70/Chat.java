package com.vvahe.aramis2is70;

import com.google.android.gms.vision.text.Line;

import java.sql.Time;
import java.util.ArrayList;

public class Chat {

    public String chatID;                                               //chat ID
    public String userID1;                                              //user 1
    public String userID2;                                              //user 2
    public Long timeCreated;                                            //time chat was created
    public ArrayList<String> messagesIDs = new ArrayList<String>();      //all messages send

    /*
        creates new chat object, and gets all data from firebase about this chat
     */
    public Chat(String chatID) {
        this.chatID = chatID;
        getFromDatabase();
    }

    /*
        creates new chat object, and puts it in firebase
     */
    public Chat(String userID1, String userID2){
        this.chatID = getNewChatID();
        this.userID1 = userID1;
        this.userID2 = userID2;
        this.timeCreated = System.currentTimeMillis();
        addToDatabase();
    }

    /*
        adds all data from this class to firebase
     */
    public void addToDatabase(){
        //TODO: make function, adds all info in this class to firebase
    }

    /*
        gets all data from firebase and adds to this class
     */
    public void getFromDatabase(){
        //TODO: make function, get all info from firebase and put it in this class
    }

    /*
        send a message in this chat, user is the one who sends the message and string is the message
     */
    public void sendMessage(String userID, String message){
        //TODO: make function, add message to this class and send it to firebase
    }

    /*
        send a file in this chat, implement later
     */
    public void sendFile(){
        //TODO: make function, maybe we need file class? (Maybe implement later)
    }

    /*
        returns a new possible chat ID
     */
    private String getNewChatID(){
        //TODO: make function, return new possible ID for chat (look up highest chat id in firebase and add 1)


        return "0";
    }
}