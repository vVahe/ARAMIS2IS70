package com.vvahe.aramis2is70;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatInstanceActivity extends AppCompatActivity {

    private String userID = FirebaseAuth.getInstance().getCurrentUser().toString();
    private ListView messageList;
    private String chatID = getIntent().getExtras().getString("chatID");
    private DatabaseReference firebaseChat = FirebaseDatabase.getInstance().getReference().child("chats");
    private DatabaseReference chat = firebaseChat.child(getIntent().getExtras().getString("chatID"));

    private TextView test;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_instance);

        messageList = findViewById(R.id.messageList);
        MessageAdapter messageAdapter = new MessageAdapter();
        messageList.setAdapter(messageAdapter);

    }

    private class MessageAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 0;
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
        public View getView(int position, View view, ViewGroup parent) {

            chat.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot message : dataSnapshot.getChildren()) {

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
            return view;
        }
    }
}
