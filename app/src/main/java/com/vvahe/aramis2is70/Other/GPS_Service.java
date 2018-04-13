package com.vvahe.aramis2is70.Other;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import com.vvahe.aramis2is70.User;

/**
 * Created by VahePC on 3/17/2018.
 */

public class GPS_Service extends Service{

    private User userObj = User.getInstance();
    private String uID;

    private LocationListener listener;
    private LocationManager locationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {

//        /*
//            get userID of currently logged in user and create user object
//         */
//        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user != null) {
//            uID = user.getUid();
//            userObj.getData(uID);
//        } else {
//            //TODO: don't think this else statement can even be reached
//        }

        /*
            get user location and puts in database
         */
        listener = new LocationListener() {


            @Override
            public void onLocationChanged(Location location) {
                User userObject = User.getInstance();
                if (userObject.userCreated) {
                    userObject.setLocation(location.getLongitude(), location.getLatitude());
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            /*
                if location permission are disabled send to settings
             */
            @Override
            public void onProviderDisabled(String provider) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };
        Log.i("tag", "listener" + listener);
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        /*
            updates location every 3 seconds
         */
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,listener);

    }
}
