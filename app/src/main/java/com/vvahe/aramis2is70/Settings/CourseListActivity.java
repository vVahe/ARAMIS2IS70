package com.vvahe.aramis2is70.Settings;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vvahe.aramis2is70.MainActivity;
import com.vvahe.aramis2is70.R;
import com.vvahe.aramis2is70.User;

import java.util.List;

public class CourseListActivity extends AppCompatActivity {
    private static ImageButton back;
    private static Button addCourse;
    ArrayAdapter<String> adapter;
    RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private DatabaseReference firebaseUser = FirebaseDatabase.getInstance().getReference().child("Users"); //database reference to users
    private DatabaseReference firebaseThisUser; //database reference to this user

    private User userObj = User.getInstance(); //reference to user Object
    private String uID; //currently logged in user ID


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);

        back = (ImageButton)findViewById(R.id.backButton);  // set view for button
        recyclerView = (RecyclerView) findViewById(R.id.courseListView); // set view for List
        addCourse = (Button)findViewById(R.id.addCourseButton); // set view for other button

        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        back(); // method for the back button
        addCourse();    // method for addCourse button
        getEnrolledCourses(); // method to get all enrolled courses from database
    }

    /**
     * back button to go back to mainActivity
     */
    public void back(){
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(CourseListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    /*
    button to send intent to searchCourseActivity
     */
    public void addCourse(){
        addCourse.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(CourseListActivity.this, searchCourseActivity.class);
                startActivity(intent);
            }
        });
    }

    /*
    gets all courses user is enrolled in
     */
    public void getEnrolledCourses(){

       final List<String> courseList = userObj.enrolledInIDs;
       //courseList = userObj.enrolledInIDs;
       Log.i("tag", "It creates courseList");
       Log.i("tag", courseList.toString());
       mAdapter = new CustomAdapter(courseList);
       recyclerView.setAdapter(mAdapter);


       /*
       adds swipe listener to courses
        */
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            /*
            delete course from enrolled courses on swipe
             */
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                final String course = courseList.get(position);
                Toast.makeText(CourseListActivity.this, "Unenrolled for: " + course, Toast.LENGTH_LONG).show();
                userObj.removeEnrolledCourse(course);
                courseList.remove(course);
                mAdapter.notifyDataSetChanged();
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    /*
    adapter for enrolled course recyclerView
     */
    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder>{

        private List<String> courseList;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mTextView;
            public ViewHolder(TextView v) {
                super(v);
                mTextView = v;
            }
        }

        public CustomAdapter(List<String> courseList) {
            this.courseList = courseList;
        }

        @Override
        public CustomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView v = (TextView) LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            ViewHolder holder = new ViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mTextView.setText(courseList.get(position));
        }

        @Override
        public int getItemCount() {
            return courseList.size();
        }

    }

}
