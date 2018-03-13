package com.vvahe.aramis2is70;

import java.sql.Time;

public class Line {

    public Integer lineID;      //lineID
    public Chat chat;           //chat object line belongs to
    public User user;           //user that send this line
    public Time timeSend;       //time this line was send
    public String message;      //message from this line

    public Line() {

    }

}
