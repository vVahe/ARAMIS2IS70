package com.vvahe.aramis2is70.Dashboard;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.vvahe.aramis2is70.Chat.Chat;
import com.vvahe.aramis2is70.Chat.ChatInstanceActivity;
import com.vvahe.aramis2is70.MainActivity;
import com.vvahe.aramis2is70.R;
import com.vvahe.aramis2is70.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by VahePC on 3/27/2018.
 */

public class AvailableListActivity extends AppCompatActivity {

    User userObj = User.getInstance();
    private String uID = userObj.userID; //currently logged in user ID


    private DatabaseReference firebaseUsers = FirebaseDatabase.getInstance().getReference().child("Users"); //database reference to users

    private ListView availableList;
    private ImageButton back;
    private TextView selectedCourse;
    private String selCourse;

    private List<String[]> availableUsers = new ArrayList<>(); //stores info of nearby users

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_list);

        selCourse = getIntent().getExtras().getString("selected course");

        Log.i("tag1", selCourse);

        availableList = findViewById(R.id.availableList);
        back = findViewById(R.id.backDashboardBtn);
        selectedCourse = findViewById(R.id.selectedCourse);

        selectedCourse.setText(selCourse);
        getAvailabeUsers(selCourse);

        back();

    }

    /*
    button action to go back to previous activity
     */
    public void back(){
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(AvailableListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    /*
    gets all of the available users for the currently selected course from firebase
     */
    public void getAvailabeUsers(final String selCourse) {

        firebaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String otherUserID = "";
                String firstName = "";
                String lastName = "";
                String selectedCourse = "";
                Boolean available = false;

                availableUsers.clear();
                //get userID's of nearby users
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.i("TAG", "ds = " + ds.getKey());
                    otherUserID = ds.getKey();
                    Log.i("TAG3", "ds = " + otherUserID + " and " + userObj.userID);

                    for (DataSnapshot ds2 : ds.getChildren()) {
                        Log.i("TAG", "ds2 = " + ds2);
                        if (ds2.getKey().equals("firstName")) firstName = (String) ds2.getValue();
                        if (ds2.getKey().equals("lastName")) lastName = (String) ds2.getValue();
                        if (ds2.getKey().equals("selectedCourse")) selectedCourse = (String) ds2.getValue();
                        if (ds2.getKey().equals("available")) available = (Boolean) ds2.getValue();

                    }
                    //if user is in radius save userID in list
                    if (selectedCourse.equals(selCourse) && (!otherUserID.equals(uID)) && available) {
                        String[] attributes = {otherUserID, firstName, lastName};
                        Log.i("TAG5", "ds = " + otherUserID + " and " + userObj.userID);
                        availableUsers.add(attributes);
                    }
                    Log.i("TAG", "nearbyUsers = " + availableUsers);
                }
                /*
                set adapter to add all available users to listview
                 */
                AvailabeListAdapter availabeListAdapter = new AvailabeListAdapter();
                availableList.setAdapter(availabeListAdapter);
                availableList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Chat chat = userObj.openChat(availableUsers.get(position)[0]);
                        Intent toChatInstance = new Intent(AvailableListActivity.this, ChatInstanceActivity.class);
                        toChatInstance.putExtra("chatID", chat.chatID);
                        startActivity(toChatInstance);

                    }
                });
                Log.i("TAG", "setAdapter");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /*
    adapter for available list view
     */
    private class AvailabeListAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return availableUsers.size();
        }

        @Override
        public Object getItem(int position) {
            return availableUsers.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            Log.i("TAG2", availableUsers.get(position)[1] );
            String otherUserID = availableUsers.get(position)[0];
            /*
            set view for listItems
             */

            view = getLayoutInflater().inflate(R.layout.available_list_item, null);

            final View finalView = view;

            final CircleImageView chatThumbnail = finalView.findViewById(R.id.chatThumbnail);
            final TextView name = finalView.findViewById(R.id.ALNameTxt);
            final TextView btn = finalView.findViewById(R.id.sendMessageBtn);

            loadImageFromStorage(position, chatThumbnail, otherUserID);

            final String[] otherUser = availableUsers.get(position);
            Log.i("TAG1", availableUsers.get(position)[1] );


            name.setText(otherUser[1] + " " + otherUser[2]);

           return view;
        }
    }

    /*
    save images to internal storage
     */
    private String saveToInternalStorage(Bitmap bitmapImage, String otherUserID){

        try {
            ContextWrapper cw = new ContextWrapper(AvailableListActivity.this);
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
    load images from internal storage if they are saved there
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
    gets images from firebase storage
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
