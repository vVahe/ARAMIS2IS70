package com.vvahe.aramis2is70.Settings;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vvahe.aramis2is70.R;
import com.vvahe.aramis2is70.User;

import java.util.ArrayList;
import java.util.List;

public class searchCourseActivity extends AppCompatActivity {
    ImageButton back;
    SearchView searchView;
    ListView listView;
    ArrayAdapter<String> adapter;


    private DatabaseReference firebaseCourses = FirebaseDatabase.getInstance().getReference().child("courses"); //database reference to users
    private DatabaseReference firebaseThisCourse; //database reference to this course
    private DatabaseReference firebaseUser = FirebaseDatabase.getInstance().getReference().child("Users"); //database reference to users
    private DatabaseReference firebaseThisUser; //database reference to this user

    private User userObj; //reference to user Object
    private String uID; //currently logged in user ID



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_course);

        back = (ImageButton)findViewById(R.id.backButton);
        listView = (ListView)findViewById(R.id.courseListView);
        searchView = (SearchView)findViewById(R.id.search_view);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uID = user.getUid();
            userObj = User.getInstance();
            Log.i("TAG", "assigned uID  = " + userObj.userID);
        } else {
            // No user is signed in
        }

        dataHandler();
        back();
        search();
    }

    public void search(){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    public void back(){
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(searchCourseActivity.this, CourseListActivity.class);
                startActivity(intent);
            }
        });
    }

    /*
       gets all userID's of users within radius
    */
    private void dataHandler() {

        firebaseCourses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final List<String> courseList = new ArrayList();

                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (!(userObj.enrolledInIDs.contains(ds.getValue(String.class)))){
                        courseList.add(ds.getValue(String.class));
                    }
                }

                adapter =new ArrayAdapter<String>(
                        searchCourseActivity.this, android.R.layout.simple_list_item_1, courseList);

                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String course = courseList.get(position);
                        Toast.makeText(searchCourseActivity.this, "Enrolled for: " + course, Toast.LENGTH_LONG).show();
                        userObj.addEnrolledCourse(courseList.get(position));
                        Log.i("tag", "usercourses"+ userObj.enrolledInIDs);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
