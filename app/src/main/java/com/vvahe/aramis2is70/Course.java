package com.vvahe.aramis2is70;

import java.time.Year;

public class Course {

    public String courseCode;       //coursecode from course
    public String alias;            //alias from course
    public String fullName;         //the full name of the course
    public String timeslot;         //the timeslot from the course
    public String quartile;         //the quartile from the course
    public Integer startYear;       //starting year of the course
    public Integer endYear;         //end year of the course

    /*
        creates new course object, and gets data from firebase
     */
    public Course(String courseCode) {
        this.courseCode = courseCode;
        getCourseInfo();
    }

    /*
        gets data from database and puts it in this class
     */
    private void getCourseInfo(){
        //TODO: make function, get from firebase and put it in this class
    }

}