package com.example.android.shushme;

/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import android.Manifest;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LoaderManager.LoaderCallbacks<Cursor>
{

    // Constants
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int PLACE_PICKER_REQUEST = 1;


    // Member variables
    private PlaceListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private Activity mainActivity;

    /**
     * Called when the activity is starting
     *
     * @param savedInstanceState The Bundle that contains the data supplied in onSaveInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.places_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new PlaceListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        // TODO (4) Create a GoogleApiClient with the LocationServices API and GEO_DATA_API
        GoogleApiClient client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this,this)
                .build();

        mainActivity = this;
        handleEvents();
    }

    public void handleEvents()
    {
        CheckBox checkbox = (CheckBox)findViewById(R.id.location_permission);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            // TODO (8) Implement onLocationPermissionClicked to handle the CheckBox click event
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked)
            {
                if ( isChecked )
                {
                    // perform logic
                }

            }
        });


        // TODO (9) Implement the Add Place Button click event to show  a toast message with the permission status
        Button button = (Button)findViewById(R.id.add_location);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getApplicationContext();
                if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "You need to enable location permissions", Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(context, "Location Permissions Granted", Toast.LENGTH_LONG).show();

                try
                {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    Intent intent = builder.build(mainActivity);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                }
                catch (Exception ex)
                {
                    Toast.makeText(context, "exception thrown", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data)
    {
        if(requestCode==PLACE_PICKER_REQUEST && resultCode==RESULT_OK)
        {
            Place place = PlacePicker.getPlace(this, data);
            if(null==place)
            {
                Log.i(TAG, "No place selected");
                return;
            }
        }
    }

    // TODO (5) Override onConnected, onConnectionSuspended and onConnectionFailed for GoogleApiClient
    @Override
    public void onConnected(@Nullable Bundle connectionHint)
    {
        Log.i(TAG, "API Client Connection Successful!");
    }

    @Override
    public void onConnectionSuspended(int cause)
    {
        Log.i(TAG, "API Client Connection Suspended!");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result)
    {
        Log.i(TAG, "API Client Connection Failed!");
    }

   /* public void onAddPlaceButtonClicked(View view)
    {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You need to enable location permissions", Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(this, "Location Permissions Granted", Toast.LENGTH_LONG).show();
    }*/

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader,
                               Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    // TODO (7) Override onResume and inside it initialize the location permissions checkbox
    @Override
    public void onResume() {
        super.onResume();

        CheckBox locationPermissions = (CheckBox) findViewById(R.id.location_permission);
        if(ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            locationPermissions.setChecked(false);
        }
        else
        {
            locationPermissions.setChecked(true);
            //locationPermissions.setEnabled(false);
        }
    }


}
