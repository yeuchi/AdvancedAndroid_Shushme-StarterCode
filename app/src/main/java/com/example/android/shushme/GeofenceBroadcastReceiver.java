package com.example.android.shushme;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

/**
 * Created by ctyeung on 1/25/18.
 */

public class GeofenceBroadcastReceiver extends BroadcastReceiver
{
    public static final String TAG = GeofenceBroadcastReceiver.class.getSimpleName();
    public static final int NOTIFICATION_ID = 1111;

    public void onReceive(Context context,
                          Intent intent)
    {
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        int geofenceTransition = event.getGeofenceTransition();

        Log.i(TAG, "onReceive called");

        switch(geofenceTransition)
        {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                setRingerMode(context, AudioManager.RINGER_MODE_SILENT);
                break;

            case Geofence.GEOFENCE_TRANSITION_EXIT:
                setRingerMode(context, AudioManager.RINGER_MODE_NORMAL);
                break;

            default:
                Log.e(TAG, String.format("Unknown transition : %d", geofenceTransition));
        }
    }

    private void setRingerMode(Context context,
                               int mode)
    {
        NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(android.os.Build.VERSION.SDK_INT < 24 ||
                (android.os.Build.VERSION.SDK_INT >= 24 && !nm.isNotificationPolicyAccessGranted()))
        {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(mode);
        }
    }

    private  void sendNotification(Context context,
                                   int transition)
    {
        int stringId=0;

        switch(transition)
        {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                stringId = R.string.silent_mode_activated;
                break;

            case Geofence.GEOFENCE_TRANSITION_EXIT:
                stringId = R.string.back_to_normal;
                break;

            default:
                return;
        }

        Notification notification = new Notification.Builder(context)
                                        .setSmallIcon(R.drawable.ic_volume_off_white_24dp)
                                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                                                R.drawable.ic_volume_off_white_24dp))
                                        .setContentTitle(context.getString(stringId))
                                        .build();

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }
}
