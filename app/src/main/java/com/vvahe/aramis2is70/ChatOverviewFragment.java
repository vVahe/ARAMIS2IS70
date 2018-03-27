package com.vvahe.aramis2is70;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ChatOverviewFragment extends Fragment {

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

        final ChatAdapter chatAdapter = new ChatAdapter();
        chatList.setAdapter(chatAdapter);

        userObj.firebaseThisUser.child("chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
            if (!(chatID.contains(userObj.userID))){
                throw new IndexOutOfBoundsException("chat does not belong to this user");
            }
            final String otherUserID = chatID.replace(userObj.userID, "");

            final Chat chat = new Chat(otherUserID);

            final View finalView = view;

            final CircleImageView picture = finalView.findViewById(R.id.chatThumbnail); //picture view
            StorageReference picStorage = FirebaseStorage.getInstance().getReference().child(otherUserID).child("Profile Picture");
            picStorage.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    picture.setImageBitmap(bmp);
                }
            });



            firebaseChat.child(userObj.chatsIDs.get(position)).child("messages").orderByChild("timeSend").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    chat.setDataInMessages(dataSnapshot);

                    final TextView lastMsg = finalView.findViewById(R.id.lastMessageTxt); //last message view
                    final TextView userName = finalView.findViewById(R.id.userNameTxt); //user name view
                    final RelativeLayout chatInstance = finalView.findViewById(R.id.chatInstance);  //chatinstance view

                    firebaseOtherUser.child(otherUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String firstName= ((firstName = dataSnapshot.child("firstName").getValue(String.class)) != null) ? firstName : "";
                            String middleName = ((middleName = dataSnapshot.child("middleName").getValue(String.class)) != null) ? middleName : "";
                            String lastName = ((lastName = dataSnapshot.child("lastName").getValue(String.class)) != null) ? lastName : "";
                            if (middleName.equals("")){
                                userName.setText(firstName.substring(0, 1).toUpperCase()+firstName.substring(1)+" "+lastName.substring(0, 1).toUpperCase()+lastName.substring(1));
                            } else {
                                userName.setText(firstName.substring(0, 1).toUpperCase()+firstName.substring(1)+" "+middleName+" "+lastName.substring(0, 1).toUpperCase()+lastName.substring(1));
                            }

                            if (chat.messages.size() == 0) {
                                lastMsg.setText("");
                            } else {
                                Message lastMessageObject = chat.messages.get(chat.messages.size()-1);
                                String lastMessage = "";
                                if (lastMessageObject.userID == userObj.userID){
                                    lastMessage = userObj.firstName+":  "+lastMessageObject.message;
                                } else {
                                    lastMessage = firstName+": "+lastMessageObject.message;
                                }
                                lastMsg.setText(lastMessage);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    chatInstance.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent toChatIntent = new Intent(getContext(), ChatInstanceActivity.class);
                            toChatIntent.putExtra("chatID", chatID); //send extra info with intent, chatID in this case
                            startActivity(toChatIntent);
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
