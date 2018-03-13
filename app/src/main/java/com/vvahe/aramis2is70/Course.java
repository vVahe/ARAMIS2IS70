package com.vvahe.aramis2is70;

import java.time.Year;

public class Course {

    public String courseCode;       //coursecode from course
    public String alias;            //alias from course
    public String fullName;         //the full name of the course
    public String timeslot;         //the timeslot from the course
    public String quartile;         //the quartile from the course
    public Year startYear;          //starting year of the course
    public Year endYear;            //end year of the course

    public Course() {

    }

    // Get data from Firebase
    public boolean getCourseInfo(){


        return true; //if succesful return true
    }

}