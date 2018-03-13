package com.vvahe.aramis2is70;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {
    private static SeekBar seekBar;
    private static TextView textView;
    private static Spinner spinner;
    private static ImageButton profile;
    private static ImageButton courses;

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
        spinner = (Spinner)view.findViewById(R.id.map_filter_spinner);
        profile = (ImageButton)view.findViewById(R.id.profileSettingsButton);
        courses = (ImageButton)view.findViewById(R.id.courseSettingsButton);

        // methods for data handeling of seekBar, Spinner and buttons
        seekBar();
        spinner();
        profileSettings();
        return view;
    }

    public void profileSettings(){
        profile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    public void spinner() { // sets the Array of strings from resources as options for spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.filter_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

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
