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
import android.widget.TextView;
import android.widget.Toast;


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



    }

    public void myClickMethod(View v) {
        Toast.makeText(getActivity(), "to settings", Toast.LENGTH_LONG).show();

    }


}
