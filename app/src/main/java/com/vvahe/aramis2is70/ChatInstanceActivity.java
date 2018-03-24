package com.vvahe.aramis2is70;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatInstanceActivity extends AppCompatActivity {

    private User userObj = User.getInstance();
    private Chat chatObj;

    private EditText messageField;
    private TextView otherUserName;
//    private CircleImageView otherUserImage;
    private Button sendMesageBtn;
    private ImageButton backBtn;

    private String userID;
    private String chatID;
    private String otherUserID;

    private ListView messageList;

    private DatabaseReference firebaseChat = FirebaseDatabase.getInstance().getReference().child("chats");
    private DatabaseReference chatRef; //reference to a single chat

    private View finalView; //view in getView listAdapter

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_instance);

        userID = FirebaseAuth.getInstance().getCurrentUser().toString();
        chatID = getIntent().getExtras().getString("chatID");
        chatRef = firebaseChat.child(chatID);

        otherUserID = chatID.replace(userObj.userID, "");
        chatObj = new Chat(otherUserID);

        otherUserName = findViewById(R.id.otherUserName);
        sendMesageBtn = findViewById(R.id.sendMessageBtn);
        messageField = findViewById(R.id.messageField);
        backBtn = findViewById(R.id.backChatBtn);

        messageList = findViewById(R.id.messageList);
        final MessageAdapter messageAdapter = new MessageAdapter();
        messageList.setAdapter(messageAdapter);

        sendMesageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageTemp = messageField.getText().toString();
                chatObj.sendMessage(messageTemp);
                //TODO: message list not updated yet after message is send
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toChatIntent = new Intent(ChatInstanceActivity.this, MainActivity.class);
                startActivity(toChatIntent);
            }
        });

    }

    private class MessageAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 8; //TODO: not good need other way to get number of message
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {

            finalView = getLayoutInflater().inflate(R.layout.message_you, null);

            chatRef.child("messages").addListenerForSingleValueEvent(new ValueEventListener() {

                String sender;
                String messageString;
//                String messageTimeStamp;
                final TextView singeMessage = finalView.findViewById(R.id.singleMessageTxt);

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                        chatObj.setDataInMessages(dataSnapshot);

                        ArrayList<Message> allMessage = chatObj.messages;

                            sender = allMessage.get(position).userID;
                            messageString = allMessage.get(position).message;
                            //TODO: include timestamp

                            //TODO: trying to change layout based on sender not working yet :(
                            if (sender.equals(userObj.userID)) {
                                finalView = getLayoutInflater().inflate(R.layout.message_other, null);
                            }

                            singeMessage.setText(messageString);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return finalView;
        }
    }
}
