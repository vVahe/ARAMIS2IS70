package com.vvahe.aramis2is70;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {

    public User userObj;
    private ListView courseList;

    private TextView userNameTxt;
    private TextView studyTxt;

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

        courseList = getView().findViewById(R.id.courseList);
        CourseAdapter courseAdapter = new CourseAdapter();
        courseList.setAdapter(courseAdapter);

    }

    class CourseAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 3;
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
            TextView courseIDTxt = view.findViewById(R.id.courseIDTxt);
            TextView courseNameTxt = view.findViewById(R.id.courseNameTxt);
            Button toListBtn = view.findViewById(R.id.availableListBtn);

            return view;
        }
    }

}
