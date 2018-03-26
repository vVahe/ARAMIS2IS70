package com.vvahe.aramis2is70;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CourseListActivity extends AppCompatActivity {
    private static ImageButton back;
    private static Button addCourse;
    ArrayAdapter<String> adapter;
    ListView listView;

    private DatabaseReference firebaseUser = FirebaseDatabase.getInstance().getReference().child("Users"); //database reference to users
    private DatabaseReference firebaseThisUser; //database reference to this user

    private User userObj = User.getInstance(); //reference to user Object
    private String uID; //currently logged in user ID


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);

        back = (ImageButton)findViewById(R.id.backButton);  // set view for button
        listView = (ListView)findViewById(R.id.courseListView); // set view for List
        addCourse = (Button)findViewById(R.id.addCourseButton); // set view for other button

        back(); // method for the back button
        addCourse();    // method for addCourse button
        getEnrolledCourses(); // method to get all enrolled courses from database
    }

    /**
     * 
     */
    public void back(){
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(CourseListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void addCourse(){
        addCourse.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(CourseListActivity.this, searchCourseActivity.class);
                startActivity(intent);
            }
        });
    }

    public void getEnrolledCourses(){

       final List<String> courseList = userObj.enrolledInIDs;
       //courseList = userObj.enrolledInIDs;
       Log.i("tag", "It creates courseList");
       Log.i("tag", courseList.toString());
       adapter =new ArrayAdapter<String>(
               CourseListActivity.this, android.R.layout.simple_list_item_1, courseList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String course = courseList.get(position);
                Toast.makeText(CourseListActivity.this, "Unenrolled for: " + course, Toast.LENGTH_LONG).show();
                userObj.removeEnrolledCourse(courseList.get(position));
                getEnrolledCourses(); // update list of enrolled courses
                Log.i("tag", "usercourses"+ userObj.enrolledInIDs);
            }
        });
    }

}
