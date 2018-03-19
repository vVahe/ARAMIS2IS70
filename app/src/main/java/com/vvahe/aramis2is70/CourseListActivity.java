package com.vvahe.aramis2is70;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class CourseListActivity extends AppCompatActivity {
    private static ImageButton back;
    private static Button addCourse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);

        back = (ImageButton)findViewById(R.id.backButton);
        ListView listView = (ListView)findViewById(R.id.courseListView);
        addCourse = (Button)findViewById(R.id.addCourseButton);

        courseListAdapter courseListAdapter = new courseListAdapter();
        listView.setAdapter(courseListAdapter);

        back();
        addCourse();
    }

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

    public class courseListAdapter extends BaseAdapter{

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
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.enrolled_course_list_item,null);
            TextView coursename = (TextView) convertView.findViewById(R.id.courseName);
            CheckBox active = (CheckBox) convertView.findViewById(R.id.courseActive);


            // coursename.setText(COURSES[position]); --> replace COURSES with the actual array of coursenames
            // active.setActivated(true/false); --> true/false should probably be a boolean array
            return convertView;
        }
    }
}
