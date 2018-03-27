package com.vvahe.aramis2is70;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private View mView;

    private DatabaseReference firebaseUsers = FirebaseDatabase.getInstance().getReference().child("Users"); //database reference to users
    private DatabaseReference firebaseThisUser; //database reference to this user

    private User userObj = User.getInstance(); //reference to user Object
    private int radiusSetting = userObj.radiusSetting; //variable for radius
    private Chat chat;

    private String uID = userObj.userID; //currently logged in user ID
    private Button testBtn; //for testing only

    private GoogleMap mGoogleMap; //google map Object
    private MapView mMapView;
    private UiSettings mUiSettings;

    private final static int MY_PERMISSIONS = 101; //permission code

    private int iterator = 0;

    private List<String[]> nearUsers = new ArrayList<>(); //stores info of nearby users

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

        mMapView = mView.findViewById(R.id.map);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }

        getUserWithinRadius();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        nearUsers.clear();
        mGoogleMap.clear();
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

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(userObj.locationX, userObj.locationY))      // Sets the center of the map to location user
                .zoom(16)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        //start new chat once infoWindow is clicked
        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String[] temp = (String[]) marker.getTag();
                chat = new Chat(temp[0]);
                String chatID = chat.getChatID(temp[0]);
                Intent toChatInstance = new Intent(getContext(), ChatInstanceActivity.class);
                toChatInstance.putExtra("chatID", chatID);
                startActivity(toChatInstance);
            }
        });
    }

    /*
        gets all userID's of users within radius
     */
    private void getUserWithinRadius() {
        firebaseUsers.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String otherUserID = "";
//                int otherUserPic = 0;
                String study = "";
                String firstName = "";
                String lastName = "";
                String selectedCourse = "";
                Double locX = 0.0;
                Double locY = 0.0;
                nearUsers.clear();
                //get userID's of nearby users
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.i("TAG", "ds = " + ds.getKey());

                    //TODO: log shows alot of emtpy records from database ??? need to figure out why
                    for (DataSnapshot ds2 : ds.getChildren()) {
                        Log.i("TAG", "ds2 = " + ds2);
                        otherUserID = ds.getKey().toString();
                        if (ds2.getKey().equals("firstName")) firstName = (String) ds2.getValue();
                        if (ds2.getKey().equals("lastName")) lastName = (String) ds2.getValue();
                        if (ds2.getKey().equals("locationX")) locX = (Double) ds2.getValue();
                        if (ds2.getKey().equals("locationY")) locY = (Double) ds2.getValue();
                        if (ds2.getKey().equals("study")) study = (String) ds2.getValue();
                        if (ds2.getKey().equals("selectedCourse")) study = (String) ds2.getValue();

                        //TODO: also get userID en pic


                    }
                    //if user is in radius save userID in list
                    if (inRadius(locX, locY, radiusSetting) && (otherUserID != uID)) { //&& (userObj.selectedCourse.equals(selectedCourse))
                        String[] attributes = {otherUserID, firstName, lastName, locX.toString(), locY.toString(), study, selectedCourse};
                        nearUsers.add(attributes);
                    }
                    Log.i("TAG", "nearbyUsers = " + nearUsers);
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
    private void createMarkers(List<String[]> nearUsers) {
        mGoogleMap.clear();
        for (String[] user : nearUsers) {
            Marker marker = mGoogleMap.addMarker(
                    new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(user[3]), Double.parseDouble(user[4])))
                            .title(user[1])
                            .snippet(user[5] + " | " + user[6])
            );
            marker.setTag(user); //set the user array as tag of the marker
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
