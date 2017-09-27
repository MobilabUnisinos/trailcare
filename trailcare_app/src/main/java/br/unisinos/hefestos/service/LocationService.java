package br.unisinos.hefestos.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Date;

import br.unisinos.hefestos.R;
import br.unisinos.hefestos.model.PTrailModel;
import br.unisinos.hefestos.pojo.PTrail;
import br.unisinos.hefestos.provider.HefestosContract;
import br.unisinos.hefestos.utility.CoordinatesUtility;
import br.unisinos.hefestos.utility.LocationUtility;
import br.unisinos.hefestos.utility.ResourcesUtility;
import br.unisinos.hefestos.utility.Utility;

public class LocationService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String LOG_TAG = LocationService.class.getSimpleName();

    private static final int MIN_TIME_DIFFERENCE_IN_SECONDS = 10;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private static Double mLastLongitude;
    private static Double mLastLatitude;

    private static Long mLastCoordinatesChangeTime;

    public LocationService(String name) {
        super(name);
    }

    public LocationService(){
        super(LocationService.class.getSimpleName());
    };

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(LOG_TAG,"entrou em LocationService");
        if (ResourcesUtility.isNetworkAvailable(this)) {
            buildGoogleApiClient();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(LOG_TAG,"entrou em LocationService, onConnected");
        try {
            mLocationRequest = LocationUtility.createLocationRequest();
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        } catch (Exception e) {
            Log.d(LOG_TAG, getString(R.string.error_localization));
            Log.e(LOG_TAG,e.getMessage(),e);
            LocationUtility.disconnectFromLocationServices(mGoogleApiClient, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(LOG_TAG, "conexao suspensa, erro = " + i);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(LOG_TAG,"entrou em LocationService, onLocationChanged");
        if(canChangeCoordinates(location.getLatitude(),location.getLongitude())) {
            mLastCoordinatesChangeTime = new Date().getTime();
            mLastLatitude = location.getLatitude();
            mLastLongitude = location.getLongitude();

            saveCoordinates();

            Log.d(LOG_TAG,"lat = " + mLastLatitude + ", long = " + mLastLongitude);

            creatPtrail();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "falhou na conexao, erro = " + connectionResult.getErrorCode());

        Toast.makeText(getApplication(), getString(R.string.error_localization), Toast.LENGTH_LONG).show();
        LocationUtility.disconnectFromLocationServices(mGoogleApiClient, this);
    }

    private boolean canChangeCoordinates(Double newLatitude, Double newLongitude){
        if(mLastCoordinatesChangeTime ==null) {
            return true;
        }
        if (mLastLongitude == null || mLastLongitude == null){
            return true;
        }
        if (CoordinatesUtility.distanceBetweenPoints(newLatitude, mLastLatitude, newLongitude, mLastLongitude) > HefestosContract.MIN_DISTANCE_BETWEEN_METERS){
            return true;
        }
        /*
        if(Utility.getTimeDifferenteInSeconds(mLastCoordinatesChangeTime) >= MIN_TIME_DIFFERENCE_IN_SECONDS){
            return true;
        }
        */

        return false;
    }

    private void saveCoordinates(){
        SharedPreferences sharedPreferences = getSharedPreferences(HefestosContract.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putLong(HefestosContract.LAST_LATITUDE, Double.doubleToRawLongBits(mLastLatitude));
        editor.putLong(HefestosContract.LAST_LONGITUDE, Double.doubleToRawLongBits(mLastLongitude));

        editor.apply();
    }

    private void creatPtrail(){
        PTrail pTrail = PTrailModel.insert(mLastLatitude,mLastLongitude,this);

        if (pTrail != null){
            Log.d(LOG_TAG,"inseriu ptrail, id = " + pTrail.getId());
        }
    }
}
