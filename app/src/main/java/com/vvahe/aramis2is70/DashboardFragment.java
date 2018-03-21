package com.vvahe.aramis2is70;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {

    public User userObj = User.getInstance();
    private ListView courseList;

    private String[] activeCourses = {"2IPC0", "2IS70"};

    private TextView userNameTxt;
    private TextView studyTxt;
    private Switch availableSwitch;
    private Switch locationSwitch;
    private int selectedPosition = 0;
    String courseID = "";
    String courseName = "";
    int counter = 0;
    private boolean done = false;
    ArrayList<String> courseIDs = new ArrayList<>();
    ArrayList<String> courseNames = new ArrayList<>();

    private DatabaseReference allCourses = FirebaseDatabase.getInstance().getReference().child("Courses"); //database reference to users

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    /* put code that should run on start up in onViewCreated */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getCourses();

        userNameTxt = getView().findViewById(R.id.nameTxt);
        studyTxt = getView().findViewById(R.id.studyTxt);

        availableSwitch = getView().findViewById(R.id.availableSwitch);
        locationSwitch = getView().findViewById(R.id.locationSwitch);

        userObj.firebaseThisUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if (ds.getKey().equals("available")){
                        availableSwitch.setChecked(ds.getValue(boolean.class));
                    } else if (ds.getKey().equals("locationShow")){
                        locationSwitch.setChecked(ds.getValue(boolean.class));
                    } else if (ds.getKey().equals("firstName")){
                        userNameTxt.setText(ds.getValue(String.class));
                    } else if (ds.getKey().equals("study")){
                        studyTxt.setText(ds.getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        courseList = getView().findViewById(R.id.courseList);
        CourseAdapter courseAdapter = new CourseAdapter();

        if (done == true) {
            courseList.setAdapter(courseAdapter);
        }

        courseList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        availableSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = availableSwitch.isChecked();
                userObj.setAvailable(checked);
            }
        });

        locationSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = locationSwitch.isChecked();
                userObj.setLocationShow(checked);
            }
        });
    }

    class CourseAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return activeCourses.length; //amount of items
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

            view = getLayoutInflater().inflate(R.layout.dashboard_course_item, null);

            RadioButton radioBtn = view.findViewById(R.id.radioButton);

            //stuff that handles only one button being allowed to be selected
            radioBtn.setChecked(position == selectedPosition);
            radioBtn.setTag(position);
            radioBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedPosition = (Integer)view.getTag();
                    notifyDataSetChanged();
                }
            });

            final TextView courseIDTxt = view.findViewById(R.id.courseIDTxt);
            final TextView courseNameTxt = view.findViewById(R.id.courseNameTxt);
            Button toListBtn = view.findViewById(R.id.availableListBtn);

            Log.i("TAG", "position = " + position);

//            courseIDTxt.setText(courseIDs.get(position) + " - ");
//            courseNameTxt.setText(courseNames.get(position));

            return view;
        }
    }

    private void getCourses() {
        allCourses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.i("TAG", "counter = " + counter);
                    Log.i("TAG", "ds = " + ds);
                    Log.i("TAG", "course in class = " + activeCourses[counter]);
                    Log.i("TAG", "course in db = " + ds.getKey());
                    for (DataSnapshot ds2 : ds.getChildren()) {
                        if (activeCourses[counter].equals(ds.getKey())) {

                            if (ds2.getKey().equals("courseCode")) courseIDs.add(ds2.getValue(String.class));
                            if (ds2.getKey().equals("fullName")) courseNames.add(ds2.getValue(String.class));

                        }
                    }
                    Log.i("TAG", "1");
                    counter++;

                }
                counter = 0;
                Log.i("TAG", "2");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        done = true;
    }

}


