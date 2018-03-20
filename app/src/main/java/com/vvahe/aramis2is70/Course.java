package com.vvahe.aramis2is70;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Course {

    public String courseCode = "";       //coursecode from course
    public String alias = "";            //alias from course
    public String fullName = "";         //the full name of the course
    public String timeslot = "";         //the timeslot from the course
    public Integer quartile = -1;         //the quartile from the course
    public Integer startYear = 9999;       //starting year of the course
    public Integer endYear = 9999;         //end year of the course

    private Boolean dataReady = false;      // 0.25 ms until data recieved, this will

    private DatabaseReference firebaseCourses = FirebaseDatabase.getInstance().getReference().child("courses"); //database reference to courses
    private DatabaseReference firebaseThisCourse; //database reference to this course

    /*
        creates new course object, and gets data from firebase
     */
    public Course(String courseCode) {
        this.courseCode = courseCode;
        firebaseThisCourse = firebaseCourses.child(courseCode);
        addFirebaseListener();
    }

    public boolean dataReady(){
        return dataReady;
    }

    /*
        gets all data from a course from firebase
     */
    private void setDataInClass(DataSnapshot dataSnapshot){
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            if (ds.getKey().equals("alias")){
                alias = ds.getValue(String.class);
            } else if (ds.getKey().equals("fullName")){
                fullName = ds.getValue(String.class);
            } else if (ds.getKey().equals("timeslot")){
                timeslot = ds.getValue(String.class);
            } else if (ds.getKey().equals("quartile")){
                quartile = ds.getValue(Integer.class);
            } else if (ds.getKey().equals("startYear")){
                startYear = ds.getValue(Integer.class);
            } else if (ds.getKey().equals("endYear")){
                endYear = ds.getValue(Integer.class);
            }
        }
        dataReady = true;
    }

    /*
        adds a firebase listener to this course
     */
    private void addFirebaseListener(){
        firebaseThisCourse.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setDataInClass(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}