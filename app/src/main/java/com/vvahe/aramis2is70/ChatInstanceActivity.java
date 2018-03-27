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

import java.text.BreakIterator;
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

        final MessageAdapter[] messageAdapter = new MessageAdapter[1];

        chatRef.child("messages").orderByChild("timeSend").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatObj.setDataInMessages(dataSnapshot);
                messageAdapter[0] = new MessageAdapter();
                messageList.setAdapter(messageAdapter[0]);
                messageList.setSelection(messageAdapter[0].getCount() - 1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        sendMesageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageTemp = messageField.getText().toString();
                chatObj.sendMessage(messageTemp);
                messageField.setText("");
                messageAdapter[0].notifyDataSetChanged();
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
            return chatObj.messages.size();
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

            String sender;
            String messageString;
            //TODO: include timestamp
            //String messageTimeStamp;

            ArrayList<Message> allMessage = chatObj.messages;
            sender = allMessage.get(position).userID;
            messageString = allMessage.get(position).message;

            if (sender.equals(userObj.userID)) {
                finalView = getLayoutInflater().inflate(R.layout.message_you, null);
                TextView singleMessage = finalView.findViewById(R.id.singleMessageTxt);
                singleMessage.setText(messageString);
            } else {
                finalView = getLayoutInflater().inflate(R.layout.message_other, null);
                TextView singleMessage = finalView.findViewById(R.id.singleMessageTxt);
                singleMessage.setText(messageString);
            }

            return finalView;
        }
    }
}
