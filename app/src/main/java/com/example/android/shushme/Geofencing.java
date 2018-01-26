package com.example.android.shushme;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ctyeung on 1/25/18.
 */

public class Geofencing implements ResultCallback
{
    private List<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;
    private GoogleApiClient mGoogleApiClient;
    private Context mContext;
    public static final long GEOFENCE_TIMEOUT = 24 * 60 * 60 * 1000;
    public static final long GEOFENCE_RADIUS = 30;
    public static final String TAG = "Geofencing";

    public Geofencing(Context context,
                      GoogleApiClient client)
    {
        mContext = context;
        mGoogleApiClient = client;
        mGeofencePendingIntent = null;
        mGeofenceList = new ArrayList<>();
    }

    public void registerAllGeofences()
    {
        if(null==mGoogleApiClient || !mGoogleApiClient.isConnected() ||
                null==mGeofenceList || 0 == mGeofenceList.size())
            return;

        try
        {
            LocationServices.GeofencingApi.addGeofences(mGoogleApiClient,
                                                        getGeofencingRequest(),
                                                        getGeofencePendingIntent())
                                                        .setResultCallback(this);
        }
        catch (SecurityException ex)
        {
            Log.e(TAG, ex.getMessage());
        }
    }

    public void unRegisterAllGeofences()
    {
        if(null==mGoogleApiClient || !mGoogleApiClient.isConnected())
            return;

        try
        {
            LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient,
                                                            getGeofencePendingIntent())
                                                            .setResultCallback(this);
        }
        catch (SecurityException ex)
        {
            Log.e(TAG, ex.getMessage());
        }
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
            mGeofenceList.add(geofence);
        }
    }

    private GeofencingRequest getGeofencingRequest()
    {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent()
    {
        if(null!=mGeofencePendingIntent)
            return mGeofencePendingIntent;

        Intent intent = new Intent(mContext, GeofenceBroadcastReceiver.class);
        mGeofencePendingIntent = PendingIntent.getBroadcast(mContext,
                                                            0,
                                                            intent,
                                                            PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    @Override
    public void onResult(@NonNull Result result) {
        Log.e(TAG, String.format("Error adding/removing geofence : %s",
                                result.getStatus().toString()));
    }
}
