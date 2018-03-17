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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;


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
            userObj = new User(uID);
            Log.i("TAG", "before button " + userObj.locationX.toString());
        } else {
            // No user is signed in
        }

        mMapView = mView.findViewById(R.id.map);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }

        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserWithinRadius();
                textX.setText(userObj.locationX.toString());
                Log.i("TAG", "after button " + userObj.locationX.toString());
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

        addMarkers();
    }

    private void addMarkers() {
        /*
            get all users from in search radius and makes markers of them on the map
         */
        radiusSetting = userObj.radiusSetting;
        getUserWithinRadius();

    }

    /*
        gets all userID's of users within radius
     */
    private void getUserWithinRadius() {

        firebaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String firstName= "";
                Double locX = 0.0;
                Double locY = 0.0;
                ArrayList<String> nearUsers = new ArrayList<>();

                //get userID's of nearby users
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    for (DataSnapshot ds2 : ds.getChildren()) {
                        String tempFN = "";
                        if (ds2.getKey().equals("firstName")) tempFN = ds2.getValue(String.class);
                        if (ds2.getKey().equals("locationX")) locX = ds2.getValue(Double.class);
                        if (ds2.getKey().equals("locationY")) locY = ds2.getValue(Double.class);

                        //TODO: also get userID en pic

                        //if user is in radius save userID in list
                        if (inRadius(locX, locY, radiusSetting)) {
                            nearUsers.add(tempFN);
                        }
                    }
                }

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
    private void createMarkers(ArrayList<String> nearUsers) {
        for (String user : nearUsers) {
            //TODO: code for making markers which displays username
        }
    }

    /*
        returns true if this user is in range, false if not in range
     */
    public boolean inRadius(double locationX, double locationY, int radius){
        double lowBoundX = locationX - radius;
        double upBoundX = locationX + radius;
        double lowBoundY = locationY - radius;
        double upBoundY = locationY + radius;
        return ((userObj.locationX > lowBoundX) && (userObj.locationX < upBoundX) && (userObj.locationY < lowBoundY) && (userObj.locationY < upBoundY));
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
