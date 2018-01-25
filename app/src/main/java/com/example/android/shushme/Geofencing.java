package com.example.android.shushme;

import android.app.PendingIntent;
import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ctyeung on 1/25/18.
 */

public class Geofencing
{
    private List<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;
    private GoogleApiClient mGoogleApiClient;
    private Context mContext;
    public static final long GEOFENCE_TIMEOUT = 24 * 60 * 60 * 1000;
    public static final long GEOFENCE_RADIUS = 30;

    public Geofencing(Context context,
                      GoogleApiClient client)
    {
        mContext = context;
        mGoogleApiClient = client;
        mGeofencePendingIntent = null;
        mGeofenceList = new ArrayList<>();
    }


    public void updateGeofencesList(PlaceBuffer places)
    {
        mGeofenceList = new ArrayList<>();
        if(null==places || 0 == places.getCount())
            return;

        for(Place place : places)
        {
            String placeUID = place.getId();
            double placeLat = place.getLatLng().latitude;
            double placeLng = place.getLatLng().longitude;
            Geofence geofence = new Geofence.Builder()
                    .setRequestId(placeUID)
                    .setExpirationDuration(GEOFENCE_TIMEOUT)
                    .setCircularRegion(placeLat, placeLng, GEOFENCE_RADIUS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();
        }
    }
}
