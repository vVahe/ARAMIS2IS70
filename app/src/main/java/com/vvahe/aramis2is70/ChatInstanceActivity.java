package com.vvahe.aramis2is70;


import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatInstanceActivity extends AppCompatActivity {

    private User userObj = User.getInstance();
    private Chat chatObj;
    private String chatID;
    private String otherUserID;

    private DatabaseReference firebaseOtherUser = FirebaseDatabase.getInstance().getReference().child("Users");
    private DatabaseReference firebaseChat = FirebaseDatabase.getInstance().getReference().child("chats");
    private DatabaseReference chatRef; //reference to a single chat

    private EditText messageField;
    private TextView otherUserName;
    private Button sendMessageBtn;
    private ImageButton backBtn;
    private CircleImageView otherUserImage;
    private ListView messageList;
    private View finalView; //view in getView listAdapter

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_instance);

        otherUserName = findViewById(R.id.otherUserName);
        sendMessageBtn = findViewById(R.id.sendMessageBtn);
        messageField = findViewById(R.id.messageField);
        backBtn = findViewById(R.id.backChatBtn);
        messageList = findViewById(R.id.messageList);
        messageList.setDivider(null);
        otherUserImage = findViewById(R.id.otherUserImage);

        chatID = getIntent().getExtras().getString("chatID"); //get chat ID
        chatRef = firebaseChat.child(chatID); //database reference to this chat
        otherUserID = chatID.replace(userObj.userID, ""); //get otherUserId (= chatID - userID)
        otherUserName.setText(userObj.usernames.get(userObj.chatsIDs.indexOf(chatID)));
        chatObj = new Chat(otherUserID); //Create chat object
        final MessageAdapter[] messageAdapter = new MessageAdapter[1]; //Create messageAdapter

        /* Display profile image from other user */
        loadImageFromStorage(otherUserImage, otherUserID);

         /* Display info from other user */
        firebaseOtherUser.child(otherUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String firstName = ((firstName = dataSnapshot.child("firstName").getValue(String.class)) != null) ? firstName : "";
                String middleName = ((middleName = dataSnapshot.child("middleName").getValue(String.class)) != null) ? middleName : "";
                String lastName = ((lastName = dataSnapshot.child("lastName").getValue(String.class)) != null) ? lastName : "";
                if (middleName.equals("")) {
                    String username = firstName.substring(0, 1).toUpperCase() + firstName.substring(1) + " " + lastName.substring(0, 1).toUpperCase() + lastName.substring(1);
                    userObj.usernames.add(userObj.chatsIDs.indexOf(chatID), username);
                    otherUserName.setText(username);
                } else {
                    String username = firstName.substring(0, 1).toUpperCase() + firstName.substring(1) + " " + middleName + " " + lastName.substring(0, 1).toUpperCase() + lastName.substring(1);
                    userObj.usernames.add(userObj.chatsIDs.indexOf(chatID), username);
                    otherUserName.setText(username);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        /* Get messages from chat and display them, also update when new messages are send */
        chatRef.child("messages").orderByChild("timeSend").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatObj.setDataInMessages(dataSnapshot);
                messageAdapter[0] = new MessageAdapter();
                messageList.setAdapter(messageAdapter[0]);
                messageList.setSelection(messageAdapter[0].getCount() - 1);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        /* When someone presses the send message button */
        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageTemp = messageField.getText().toString(); //get message string
                chatObj.sendMessage(messageTemp); //send message
                messageField.setText(""); //clear input field
            }
        });

        /* When someone presses the back button */
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toChatIntent = new Intent(ChatInstanceActivity.this, MainActivity.class);
                startActivity(toChatIntent);
            }
        });

    }

    private String saveToInternalStorage(Bitmap bitmapImage, String otherUserID){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,otherUserID+".bmp");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            //e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private void loadImageFromStorage(CircleImageView view, String otherUserID) {
        try {
            File f=new File(userObj.pathToProfilePics.get(userObj.chatsIDs.indexOf(chatID)), otherUserID+".bmp");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            view.setImageBitmap(b);
        } catch (FileNotFoundException | IndexOutOfBoundsException e) {
            getImage(view, otherUserID);
        }
    }

    private void getImage(final CircleImageView view, final String otherUserID) {
        StorageReference picStorage = FirebaseStorage.getInstance().getReference().child(otherUserID).child("Profile Picture");
        picStorage.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                view.setImageBitmap(bmp);
                String path = saveToInternalStorage(bmp, otherUserID);
                userObj.pathToProfilePics.add(userObj.chatsIDs.indexOf(chatID), path);
            }
        });
    }

    private class MessageAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            Log.wtf("size", String.valueOf(chatObj.messages.size()));
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
            String messageTimeStamp;

            messageTimeStamp = chatObj.messages.get(position).getTime();
            sender = chatObj.messages.get(position).userID;
            messageString = chatObj.messages.get(position).message;

            if (sender.equals(userObj.userID)) {
                finalView = getLayoutInflater().inflate(R.layout.message_you, null);
                TextView singleMessage = finalView.findViewById(R.id.singleMessageTxt);
                TextView time = finalView.findViewById(R.id.timeSend);
                singleMessage.setText(messageString);
                time.setText(messageTimeStamp);
            } else {
                finalView = getLayoutInflater().inflate(R.layout.message_other, null);
                TextView singleMessage = finalView.findViewById(R.id.singleMessageTxt);
                TextView time = finalView.findViewById(R.id.timeSend);
                singleMessage.setText(messageString);
                time.setText(messageTimeStamp);
            }

            return finalView;
        }


    }
}
