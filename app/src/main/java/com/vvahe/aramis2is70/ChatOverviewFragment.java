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

            firebaseChat.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (ds.getKey().equals(userObj.chatsIDs.get(position))) {
                            Log.i("TAG", "ds = " + ds);
                            Query lastMessage = firebaseChat.child(userObj.chatsIDs.get(position)).orderByKey().limitToLast(1);
                            Log.i("TAG", "Query = " + lastMessage);

                            lastMessage.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Log.i("TAG", "ds2 = " + dataSnapshot);
                                    String message = dataSnapshot.child("string").getValue().toString();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    //Handle possible errors.
                                }
                            });

                        }
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
