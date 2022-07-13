package com.mobio.analytics.client.geofence;

import static android.util.Log.d;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "GeofenceBroadcastReceiv";
    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent=GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()){
            Log:d(TAG,"onReceive: Error received geofencing event....");
            return;
        }

        List<Geofence> geofenceList= geofencingEvent.getTriggeringGeofences();
        for (Geofence geofence:geofenceList){
            Log.d(TAG,"onReceive: "+geofence.getRequestId()+" "+geofence.toString());
        }
        //Location location=geofencingEvent.getTriggeringLocation();   used to get the location list of triggering event

        int transitionType=geofencingEvent.getGeofenceTransition();
        switch (transitionType){
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Toast.makeText(context,"GEOFENCE_TRANSITION_ENTER",Toast.LENGTH_SHORT).show();
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                Toast.makeText(context,"GEOFENCE_TRANSITION_DWELL",Toast.LENGTH_SHORT).show();
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Toast.makeText(context,"GEOFENCE_TRANSITION_EXIT",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
