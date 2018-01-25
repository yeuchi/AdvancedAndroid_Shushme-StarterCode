package com.example.android.shushme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by ctyeung on 1/25/18.
 */

public class GeofenceBroadcastReceiver extends BroadcastReceiver
{
    public static final String TAG = GeofenceBroadcastReceiver.class.getSimpleName();

    public void onReceive(Context context,
                          Intent intent)
    {
        Log.i(TAG, "onReceive called");
    }
}
