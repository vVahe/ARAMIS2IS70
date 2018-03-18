package com.vvahe.aramis2is70;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

public class searchCourseActivity extends AppCompatActivity {
    ImageButton back;
    SearchView searchView;
    ListView listView;
    ArrayAdapter<String> adapter;

    String[] courseplaceholder = getResources().getStringArray(R.array.courseplaceholder);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_course);

        back = (ImageButton)findViewById(R.id.backButton);
        listView = (ListView)findViewById(R.id.courseListView);
        searchView = (SearchView)findViewById(R.id.search_view);
        adapter = new ArrayAdapter<String>(
                searchCourseActivity.this,
                android.R.layout.simple_list_item_1,
                courseplaceholder
        );

        listView.setAdapter(adapter);

        back();
        search();
    }

    public void search(){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    public void back(){
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(searchCourseActivity.this, CourseListActivity.class);
                startActivity(intent);
            }
        });
    }



}
