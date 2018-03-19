package com.vvahe.aramis2is70;

import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    /** put code that should run on start up in onViewCreated */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


        Toast.makeText(getActivity(), "Welcome to your dashboard", Toast.LENGTH_LONG).show();

        ImageView profilePicture = (ImageView) getActivity().findViewById(R.id.profilePicture);
        TextView loggedInText = (TextView) getActivity().findViewById(R.id.loggedInText);
        TextView userName = (TextView) getActivity().findViewById(R.id.userName);
        TextView userMajor = (TextView) getActivity().findViewById(R.id.userMajor);

        TextView selCurCourseTxt = (TextView) getActivity().findViewById(R.id.selCurCourseTxt);
        RadioButton radioButton = (RadioButton) getActivity().findViewById(R.id.radioButton);
        RadioButton radioButton2 = (RadioButton) getActivity().findViewById(R.id.radioButton2);
        RadioButton radioButton3 = (RadioButton) getActivity().findViewById(R.id.radioButton3);
        RadioButton radioButton4 = (RadioButton) getActivity().findViewById(R.id.radioButton4);

        TextView course1 = (TextView) getActivity().findViewById(R.id.course1);
        ImageView course1info = (ImageView) getActivity().findViewById(R.id.course1info);
        TextView course2 = (TextView) getActivity().findViewById(R.id.course1);
        ImageView course2info = (ImageView) getActivity().findViewById(R.id.course2info);
        TextView course3 = (TextView) getActivity().findViewById(R.id.course1);
        ImageView course3info = (ImageView) getActivity().findViewById(R.id.course3info);
        TextView course4 = (TextView) getActivity().findViewById(R.id.course1);
        ImageView course4info = (ImageView) getActivity().findViewById(R.id.course4info);

        TextView privacyTxt = (TextView) getActivity().findViewById(R.id.privacyTxt);
        TextView availabilityTxt = (TextView) getActivity().findViewById(R.id.availabilityTxt);
        Switch availabilitySwitch = (Switch) getActivity().findViewById(R.id.availabilitySwitch);
        TextView mapTxt = (TextView) getActivity().findViewById(R.id.mapTxt);
        Switch mapSwitch = (Switch) getActivity().findViewById(R.id.mapSwitch);

    }

}
