package com.vvahe.aramis2is70;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by VahePC on 3/27/2018.
 */

public class AvailableListActivity extends AppCompatActivity {

    User userObj = User.getInstance();
    private String uID = userObj.userID; //currently logged in user ID

    private DatabaseReference firebaseUsers = FirebaseDatabase.getInstance().getReference().child("Users"); //database reference to users

    private ListView availableList;
    private ImageButton backBtn;
    private TextView selectedCourse;
    private String selCourse;

    private List<String[]> availableUsers = new ArrayList<>(); //stores info of nearby users

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_list);

        selCourse = getIntent().getExtras().getString("chatID");

        availableList = findViewById(R.id.availableList);
        backBtn = findViewById(R.id.backDashboardBtn);
        selectedCourse = findViewById(R.id.selectedCourse);

        AvailabeListAdapter availabeListAdapter = new AvailabeListAdapter();
        availableList.setAdapter(availabeListAdapter);

        getAvailabeUsers(selCourse);

    }

    public void getAvailabeUsers(final String selCourse) {

        firebaseUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String otherUserID = "";
                String firstName = "";
                String lastName = "";
                String selectedCourse = "";

                availableUsers.clear();
                //get userID's of nearby users
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.i("TAG", "ds = " + ds.getKey());

                    for (DataSnapshot ds2 : ds.getChildren()) {
                        Log.i("TAG", "ds2 = " + ds2);
                        otherUserID = ds.getKey().toString();
                        if (ds2.getKey().equals("firstName")) firstName = (String) ds2.getValue();
                        if (ds2.getKey().equals("lastName")) lastName = (String) ds2.getValue();
                        if (ds2.getKey().equals("selectedCourse")) selectedCourse = (String) ds2.getValue();

                    }
                    //if user is in radius save userID in list
                    if (selectedCourse.equals(selCourse) && (otherUserID != uID)) { //&& (userObj.selectedCourse.equals(selectedCourse))
                        String[] attributes = {otherUserID, firstName, lastName};
                        availableUsers.add(attributes);
                    }
                    Log.i("TAG", "nearbyUsers = " + availableUsers);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private class AvailabeListAdapter extends BaseAdapter {

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

            view = getLayoutInflater().inflate(R.layout.available_list_item, null);




           return view;
        }
    }
}