package com.mobio.analytics.client.geofence;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mobio.analytics.client.utility.LogMobio;

public class GeofenceRepository {
    private Context context;
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;

    public GeofenceRepository(Context context) {
        this.context = context;
        geofencingClient = LocationServices.getGeofencingClient(context);
        geofenceHelper = new GeofenceHelper(context);
    }

    public void addGeofence(LatLng latLng, float radius, String id) {
        Geofence geofence = geofenceHelper.getGeofence(id, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        LogMobio.logD("GeofenceRepository","onSuccess: Geofence Added...");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage=geofenceHelper.getErrorString(e);
                        LogMobio.logD("GeofenceRepository","onFailure "+errorMessage);
                    }
                });
    }
}
