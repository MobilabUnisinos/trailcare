package br.unisinos.hefestos.utility;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationUtility {

    private static final String LOG_TAG = LocationUtility.class.getSimpleName();

    public static LocationRequest createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(30 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(10 * 1000); // 10 second, in milliseconds

        return locationRequest;
    }

    public static void disconnectFromLocationServices(GoogleApiClient googleApiClient, LocationListener locationListener) {
        Log.v(LOG_TAG,"vai desconecat do serviço de localização");

        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, locationListener);
            googleApiClient.disconnect();
        }
    }
}
