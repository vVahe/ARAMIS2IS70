package com.vvahe.aramis2is70;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by VahePC on 3/23/2018.
 */

public class CustomMarker implements GoogleMap.InfoWindowAdapter {

    private final View mapView;
    private Context mapContext;

    public CustomMarker(Context context) {
        mapContext = context;
        mapView = LayoutInflater.from(context).inflate(R.layout.custom_map_marker, null);
    }

    private void renderMarker(Marker marker, View view) {
        CircleImageView markerProfilePic = view.findViewById(R.id.markerProfilePic);
        TextView markerFirstName = view.findViewById(R.id.markerFirstName);
        TextView markerLastName = view.findViewById(R.id.markerLastName);
        TextView markerStudy = view.findViewById(R.id.markerStudy);
        TextView markerMoreInfo = view.findViewById(R.id.markerMoreInfo);
        Button markerBtn = view.findViewById(R.id.markerBtn);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderMarker(marker, mapView);
        return mapView;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderMarker(marker, mapView);
        return mapView;
    }
}
