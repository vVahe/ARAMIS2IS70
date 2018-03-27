package com.vvahe.aramis2is70;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {

    public User userObj = User.getInstance();
    private ListView courseList;

    private StorageReference mStorage;  // reference to root firebaseStorage
    private StorageReference picStorage;    // reference to profile picture in storage

    private String[] activeCourses = {"2IPC0", "2IS70"};

    private CircleImageView profilePicture;
    private TextView userNameTxt;
    private TextView studyTxt;
    private Switch availableSwitch;
    private Switch locationSwitch;
    String selectedCourse = "";

    ArrayList<String> courseNames = new ArrayList<>();


    private DatabaseReference allCourses = FirebaseDatabase.getInstance().getReference().child("courses"); //database reference to users

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
        userNameTxt = getView().findViewById(R.id.nameTxt);
        studyTxt = getView().findViewById(R.id.studyTxt);
        profilePicture = getView().findViewById(R.id.profilePic);
        mStorage = FirebaseStorage.getInstance().getReference();
        picStorage = mStorage.child(userObj.userID).child("Profile Picture");

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
                    } else if (ds.getKey().equals("study")) {
                        studyTxt.setText(ds.getValue(String.class));
                    } else if (ds.getKey().equals("selectedCourse")){
                        selectedCourse = ds.getValue(String.class);
                    } else if (ds.getKey().equals("enrolledIn")){
                        courseNames.clear();
                        for(DataSnapshot dsChild : ds.getChildren()) {
                            courseNames.add(dsChild.getValue(String.class));
                        }

                    }
                }
                CourseAdapter courseAdapter = new CourseAdapter();
                courseList.setAdapter(courseAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        courseList = getView().findViewById(R.id.courseList);


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

        setPicture();
    }

    class CourseAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return courseNames.size(); //amount of items
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
            radioBtn.setTag(position);
            final int positionHolder = position;

            //stuff that handles only one button being allowed to be selected
            if (courseNames.get(position).equals(selectedCourse)){
                radioBtn.setChecked(true);
            } else {
                radioBtn.setChecked(false);
            }
            radioBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    notifyDataSetChanged();
                    String courseID = courseNames.get(positionHolder);
                    userObj.setSelectedCourse(courseID);

                }
            });

            final TextView courseNameTxt = view.findViewById(R.id.courseNameTxt);
            Button toListBtn = view.findViewById(R.id.availableListBtn);
            courseNameTxt.setText(courseNames.get(position));

            final int positionTemp = position;

            toListBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent toAvailableList = new Intent(getContext(), AvailableListActivity.class);
                    toAvailableList.putExtra("selected course", courseNames.get(positionTemp));
                    startActivity(toAvailableList);
                }
            });

            return view;
        }
    }

    public  void setPicture() {
        if(picStorage != null){
            picStorage.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    profilePicture.setImageBitmap(bmp);
                }
            });
        }
    }


}


