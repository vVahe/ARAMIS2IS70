package com.vvahe.aramis2is70.Chat;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.vvahe.aramis2is70.R;
import com.vvahe.aramis2is70.User;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

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

    private BottomNavigationView bNavView;
    private Menu menu;

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

        //change new message icon back to normal
        bNavView = getActivity().findViewById(R.id.mainNav);
        menu = bNavView.getMenu();
        menu.findItem(R.id.nav_chat).setIcon(R.drawable.ic_message_black_24dp);

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

    /*
    adapter for chat
     */
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

            /*
            set view and elements for listItems
             */
            view = getLayoutInflater().inflate(R.layout.chatoverview_chat_item, null);

            final String chatID = userObj.chatsIDs.get(position);
            if (!(chatID.contains(userObj.userID))){
                throw new IndexOutOfBoundsException("chat does not belong to this user");
            }
            final String otherUserID = chatID.replace(userObj.userID, "");

            final Chat chat = new Chat(otherUserID);

            final View finalView = view;

            final CircleImageView newMessagesGreen  = finalView.findViewById(R.id.numberNewMessagesGreen);
            final TextView newMessages = finalView.findViewById(R.id.numberNewMessages);
            final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) newMessages.getLayoutParams();

            final TextView userName = finalView.findViewById(R.id.userNameTxt); //user name view
            userName.setText(userObj.usernames.get(userObj.chatsIDs.indexOf(chatID)));

            final CircleImageView availableBorder = finalView.findViewById(R.id.chatThumbnailAvailable);
            availableBorder.setVisibility(View.INVISIBLE);
            final CircleImageView picture = finalView.findViewById(R.id.chatThumbnail); //picture view
            loadImageFromStorage(position, picture, otherUserID);

            /*
            handle data from firebase
             */
            firebaseChat.child(userObj.chatsIDs.get(position)).child("messages").orderByChild("timeSend").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    chat.setDataInMessages(dataSnapshot);

                    final TextView lastMsg = finalView.findViewById(R.id.lastMessageTxt); //last message view
                    final RelativeLayout chatInstance = finalView.findViewById(R.id.chatInstance);  //chatinstance view
                    final TextView timeSend = finalView.findViewById(R.id.timeLastSend); //time last message

                    Integer numberOfNewMessages = chat.getNumberOfNewMessages();
                    if (numberOfNewMessages > 0){
                        if (numberOfNewMessages <= 9){
                            newMessages.setText(numberOfNewMessages.toString());
                            try {
                                Resources r = getResources();
                                float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8.5f, r.getDisplayMetrics());
                                params.setMarginStart(Math.round(px));
                                newMessages.setLayoutParams(params);
                            } catch (IllegalStateException e){}
                        } else if (numberOfNewMessages > 9){
                            newMessages.setText("9+");
                            try {
                                Resources r = getResources();
                                float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5.5f, r.getDisplayMetrics());
                                params.setMarginStart(Math.round(px));
                                newMessages.setLayoutParams(params);
                            } catch (IllegalStateException e){}
                        }
                        newMessagesGreen.setVisibility(View.VISIBLE);
                        newMessages.setVisibility(View.VISIBLE);

                    } else{
                        newMessages.setVisibility(View.INVISIBLE);
                        newMessages.setText("0");
                        newMessagesGreen.setVisibility(View.INVISIBLE);
                        newMessages.setLayoutParams(params);
                    }

                    firebaseOtherUser.child(otherUserID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Boolean available = ((available = dataSnapshot.child("available").getValue(Boolean.class)) != null) ? available : false;
                            String firstName= ((firstName = dataSnapshot.child("firstName").getValue(String.class)) != null) ? firstName : "";
                            String middleName = ((middleName = dataSnapshot.child("middleName").getValue(String.class)) != null) ? middleName : "";
                            String lastName = ((lastName = dataSnapshot.child("lastName").getValue(String.class)) != null) ? lastName : "";

                            if (middleName.equals("")){
                                String displayName = firstName.substring(0, 1).toUpperCase()+firstName.substring(1)+" "+lastName.substring(0, 1).toUpperCase()+lastName.substring(1);
                                userObj.usernames.set(userObj.chatsIDs.indexOf(chatID), displayName);
                                userName.setText(displayName);
                            } else {
                                String displayName = firstName.substring(0, 1).toUpperCase()+firstName.substring(1)+" "+middleName+" "+lastName.substring(0, 1).toUpperCase()+lastName.substring(1);
                                userObj.usernames.set(userObj.chatsIDs.indexOf(chatID), displayName);
                                userName.setText(displayName);
                            }

                            if (available){
                                availableBorder.setVisibility(View.VISIBLE);
                            } else {
                                availableBorder.setVisibility(View.INVISIBLE);
                            }

                            if (chat.messages.size() == 0) {
                                lastMsg.setText("");
                                timeSend.setText("");
                            } else {
                                Message lastMessageObject = chat.messages.get(chat.messages.size()-1);
                                String lastMessage = "";
                                timeSend.setText(lastMessageObject.getDate());

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

                    chatList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                            //delte this chat
                            userObj.deleteChat(chatID);

                            firebaseOtherUser.child(otherUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    //delete chat data if other user also has deleted this chat
                                    ArrayList<String> chatsIDs = new ArrayList<>();
                                    for(DataSnapshot dsChild : dataSnapshot.child("chats").getChildren()) {
                                        chatsIDs.add(dsChild.getValue(String.class));
                                    }
                                    if (!(chatsIDs.contains(chatID))){
                                        firebaseChat.child(chatID).setValue(null);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            notifyDataSetChanged();
                            return true;
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return view;
        }

        private String saveToInternalStorage(Bitmap bitmapImage, String otherUserID){

            try {
                ContextWrapper cw = new ContextWrapper(getContext());
                // path to /data/data/yourapp/app_data/imageDir
                File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                // Create imageDir
                File mypath=new File(directory,otherUserID+".bmp");

                FileOutputStream fos = null;
                fos = new FileOutputStream(mypath);
                // Use the compress method on the BitMap object to write image to the OutputStream
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);

                try {
                    fos.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                }

                return directory.getAbsolutePath();
            } catch (Exception e) {
                //e.printStackTrace();
            }
            return "";
        }

        /*
        Load image from storage if it is stored there
         */
        private void loadImageFromStorage(Integer position, CircleImageView view, String otherUserID) {
            try {
                File f=new File(userObj.pathToProfilePics.get(position), otherUserID+".bmp");
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                view.setImageBitmap(b);
            } catch (FileNotFoundException | IndexOutOfBoundsException e) {
                getImage(position, view, otherUserID);
            }
        }

        /*
        gets image from firebase storage
         */
        private void getImage(final Integer position, final CircleImageView view, final String otherUserID){
            StorageReference picStorage = FirebaseStorage.getInstance().getReference().child(otherUserID).child("Profile Picture");
            picStorage.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    view.setImageBitmap(bmp);
                    String path = saveToInternalStorage(bmp, otherUserID);
                    userObj.pathToProfilePics.add(position, path);
                }
            });
        }
    }
}
