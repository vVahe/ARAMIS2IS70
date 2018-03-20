package com.vvahe.aramis2is70;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class searchCourseActivity extends AppCompatActivity {
    ImageButton back;
    SearchView searchView;
    ListView listView;
    ArrayAdapter<String> adapter;

    private DatabaseReference firebaseCourses = FirebaseDatabase.getInstance().getReference().child("courses"); //database reference to users
    private DatabaseReference firebaseThisCourse; //database reference to this course



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_course);

        String[] courseplaceholder = getResources().getStringArray(R.array.courseplaceholder);

        back = (ImageButton)findViewById(R.id.backButton);
        listView = (ListView)findViewById(R.id.courseListView);
        searchView = (SearchView)findViewById(R.id.search_view);

        getAllCourses();
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
    private void getAllCourses() {

        firebaseCourses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final List<String> courseList = new ArrayList();

                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    for (DataSnapshot ds2 : ds.getChildren()) {
                        if (ds2.getKey().equals("fullName")){
                            courseList.add(ds2.getValue(String.class));
                        };
                        }
                    }

                adapter =new ArrayAdapter<String>(
                        searchCourseActivity.this, android.R.layout.simple_list_item_1, courseList);

                listView.setAdapter(adapter);
                }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }






}
