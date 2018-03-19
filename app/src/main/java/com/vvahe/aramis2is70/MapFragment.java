package com.vvahe.aramis2is70;


import android.*;
import android.content.BroadcastReceiver;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private View mView;

    private DatabaseReference firebaseUser = FirebaseDatabase.getInstance().getReference().child("Users"); //database reference to users
    private DatabaseReference firebaseThisUser; //database reference to this user

    private User userObj; //reference to user Object
    private int radiusSetting; //variable for radius

    private String uID; //currently logged in user ID
    private TextView textX; //for testing only
    private Button testBtn; //for testing only

    private GoogleMap mGoogleMap; //google map Object
    private MapView mMapView;
    private UiSettings mUiSettings;

    private final static int MY_PERMISSIONS = 101; //permission code

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_map, container, false);
        return mView;

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textX = getView().findViewById(R.id.textX);
        testBtn = getView().findViewById(R.id.testBtn);

        /*
            get userID of currently logged in user and create user object
         */
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uID = user.getUid();
            userObj = User.getInstance();
            radiusSetting = userObj.radiusSetting;
            Log.i("TAG", "assigned uID  = " + userObj.userID);
        } else {
            // No user is signed in
        }

        mMapView = mView.findViewById(R.id.map);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }

        //for testing code only
        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserWithinRadius();
                textX.setText(userObj.locationX.toString());
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mUiSettings = mGoogleMap.getUiSettings();

        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);

        enableMyLocation();
    }

    /*
        gets all userID's of users within radius
     */
    private void getUserWithinRadius() {

        firebaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<String[]> nearUsers = new ArrayList<>(); //stores name, locX, locY of nearby users
                String tempFN = ""; //temp string
                Double locX = 0.0;
                Double locY = 0.0;

                //get userID's of nearby users
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.i("TAG", "ds contains  = " + ds);
                    //TODO: log shows alot of emtpy records from database ??? need to figure out why
                    for (DataSnapshot ds2 : ds.getChildren()) {
                        Log.i("TAG", "ds2 contains  = " + ds2);
                        if (ds2.getKey().equals("firstName")) tempFN = ds2.getValue(String.class);
                        if (ds2.getKey().equals("locationX")) locX = ds2.getValue(Double.class);
                        if (ds2.getKey().equals("locationY")) locY = ds2.getValue(Double.class);
//                        Log.i("TAG", "tempFN  = " + tempFN);
//                        Log.i("TAG", "locX  = " + locX);
//                        Log.i("TAG", "locY  = " + locY);

                        //TODO: also get userID en pic

                        //if user is in radius save userID in list
//                        Log.i("TAG", "Radiussetting = " + radiusSetting);
//                        Log.i("TAG", "inRadius? = " + inRadius(locX, locY, radiusSetting));
                        if (inRadius(locX, locY, radiusSetting)) {
                            String[] attributes = {tempFN, locX.toString(), locY.toString()};
//                            Log.i("TAG", "Attributes = " + attributes);
                            nearUsers.add(attributes);
                        }
                    }
                }
//                Log.i("TAG", "List contains  = " + nearUsers);
                createMarkers(nearUsers);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /*
        create google map markers for all nearby users
     */
    private void createMarkers(List<String[]> nearUsers) {
        for (String[] user : nearUsers) {
//            Log.i("TAG", "string user = " + user);
                Marker marker = mGoogleMap.addMarker(
                        new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(user[1]), Double.parseDouble(user[2])))
                            .title(user[0])
                            .snippet("test")
            );
        }
    }

    /*
        returns true if distance between users is within radius, otherwise false
     */
    public boolean inRadius(double lat2, double lon2, int radius) {
            Boolean result = false;
            Double lat1 = userObj.locationY;
            Double lon1 = userObj.locationX;

            //do some quick math
            Double earthRadius = 6378.137; // Radius of earth in KM
            Double dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
            Double dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180;
            Double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                    Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
                            Math.sin(dLon/2) * Math.sin(dLon/2);
            Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
            Double d = earthRadius * c;

//        Log.i("TAG", "distance in meters = " + d);
            //TODO: radius needs to be tweaked to only display users within 1000 meters
            if (d * 1000 < radius) {
                result = true;
            }
        return true;
    }

    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mGoogleMap.setMyLocationEnabled(true);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.INTERNET
                }, MY_PERMISSIONS); /* indicator for the permission, used in onRequestPermissionResult, can be any number */
                return;
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                            android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mGoogleMap.setMyLocationEnabled(true);
                    } else {
                        Toast.makeText(getContext(), "This app requires location permission to be granded", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }


}
