package com.vvahe.aramis2is70;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ChatOverviewFragment extends Fragment {

    private Button test;
//    private Chat chat = new Chat();
    private User userObj = User.getInstance();
    private ListView chatList;
    private DatabaseReference firebaseChat = FirebaseDatabase.getInstance().getReference().child("chats");
    private DatabaseReference firebaseOtherUser = FirebaseDatabase.getInstance().getReference().child("Users");

    public ChatOverviewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatoverview, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chatList = getView().findViewById(R.id.chatList);
        test = getView().findViewById(R.id.testChatBtn);

        ChatAdapter chatAdapter = new ChatAdapter();
        chatList.setAdapter(chatAdapter);

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    class ChatAdapter extends BaseAdapter {
        String[] idArray = new String[getCount()];

        @Override
        public int getCount() {
            return userObj.chatsIDs.size();
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

            view = getLayoutInflater().inflate(R.layout.chatoverview_chat_item, null);

            final String chatID = userObj.chatsIDs.get(position);
            final String otherUserID = chatID.replace(userObj.userID, "");

            final Chat chat = new Chat(otherUserID);

            final View finalView = view;

            firebaseChat.child(userObj.chatsIDs.get(position)).child("messages").orderByChild("timeSend").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    chat.setDataInMessages(dataSnapshot);

                    final Message lastMessage = chat.messages.get(chat.messages.size()-1); //The last send message
                    final TextView lastMsg = finalView.findViewById(R.id.lastMessageTxt); //last message view
                    final TextView userName = finalView.findViewById(R.id.userNameTxt); //user name view
                    final RelativeLayout chatInstance = finalView.findViewById(R.id.chatInstance);  //chatinstance view
                    lastMsg.setText(lastMessage.message);

                    firebaseOtherUser.child(otherUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            lastMsg.setText(lastMessage.message);
                            String firstName = dataSnapshot.child("firstName").getValue(String.class);
                            String middleName = dataSnapshot.child("middleName").getValue(String.class);
                            String lastName = dataSnapshot.child("lastName").getValue(String.class);
                            if (middleName.equals("")){
                                userName.setText(firstName.substring(0, 1).toUpperCase()+firstName.substring(1)+" "+lastName.substring(0, 1).toUpperCase()+lastName.substring(1));
                            } else {
                                userName.setText(firstName.substring(0, 1).toUpperCase()+firstName.substring(1)+" "+middleName+" "+lastName.substring(0, 1).toUpperCase()+lastName.substring(1));
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    chatInstance.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i("TAG",  "click");
                            //TODO: open chat
                            Chat chat = userObj.openChat("A17KadUBoiX01gHnEBz6lHwLMv82");
                            chat.sendMessage("testMessage");
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return view;
        }
    }
}
