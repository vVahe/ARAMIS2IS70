package com.vvahe.aramis2is70.Settings;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.vvahe.aramis2is70.Login_Register.LoginActivity;
import com.vvahe.aramis2is70.R;
import com.vvahe.aramis2is70.Settings.CourseListActivity;
import com.vvahe.aramis2is70.Settings.ProfileActivity;
import com.vvahe.aramis2is70.User;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {
    private static SeekBar seekBar;
    private static TextView textView;
    private static ImageButton profile;
    private static ImageButton courses;
    private static Button logout;
    private static SeekBar radius;
    private static TextView radiusText;

    private User userObj = User.getInstance();

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // set all of the elements
        seekBar = (SeekBar)view.findViewById(R.id.searchradius_seekBar);
        textView = (TextView)view.findViewById(R.id.seekbar_text);
        profile = (ImageButton)view.findViewById(R.id.profileSettingsButton);
        courses = (ImageButton)view.findViewById(R.id.courseSettingsButton);
        logout = (Button)view.findViewById(R.id.settings_btnLogout);
        radius = (SeekBar)view.findViewById(R.id.searchradius_seekBar);
        radiusText = (TextView)view.findViewById(R.id.seekbar_text);


        //get all current data and display
        userObj.firebaseThisUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if (ds.getKey().equals("radiusSetting")){
                        radius.setProgress(ds.getValue(Integer.class));
                        radiusText.setText(ds.getValue(Integer.class)+" meter");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // methods for data handeling of seekBar, Spinner and buttons
        seekBar();
        profileSettings();
        courseSettings();
        logout();
        radius();
        return view;
    }


    /*
    logout current user
     */
    public void logout(){
        logout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                userObj.reset();
                Intent logoutIntent = new Intent(getActivity(), LoginActivity.class);
                /* prevents user from going back once logged in, they will have to use logout button*/
                logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(logoutIntent);
            }
        });
    }

    /*
    set radius based on seekbar value
     */
    public void radius(){
        radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    userObj.setRadius(progress);
                }
                radiusText.setText(progress+" meter");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /*
    intent to courseListActivity
     */
    public void courseSettings(){
        courses.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CourseListActivity.class);
                startActivity(intent);
            }
        });
    }

    // intent to ProfileActivity
    public void profileSettings(){
        profile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    /*
    seekbar listener
     */
    public void seekBar(){  // attaches textview with progress to the thumb of the seekBar
        textView.setText(Integer.toString(seekBar.getProgress()));

        //calculate the x coördinate of the thumb of the seekbar
        double percent = seekBar.getProgress() / (double) seekBar.getMax();
        int offset = seekBar.getThumbOffset();
        int seekWidth = seekBar.getWidth();
        int val = (int) Math.round(percent * (seekWidth - 2 * offset));
        int labelWidth = textView.getWidth();
        textView.setX(offset + seekBar.getX() + val   // set x of textview to that of seekBar thumb
                - Math.round(percent * offset)
                - Math.round(percent * labelWidth/2));

        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override // keep updating x coordinate of textview to that of the seekBar
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        textView.setText(Integer.toString(progress));
                        double percent = progress / (double) seekBar.getMax();
                        int offset = seekBar.getThumbOffset();
                        int seekWidth = seekBar.getWidth();
                        int val = (int) Math.round(percent * (seekWidth - 2 * offset));
                        int labelWidth = textView.getWidth();
                        textView.setX(offset + seekBar.getX() + val
                        - Math.round(percent * offset)
                        - Math.round(percent * labelWidth/2));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );

    }



}
