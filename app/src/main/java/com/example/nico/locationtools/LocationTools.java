package com.example.nico.locationtools;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

/**
 * Created by Nico on 26/10/2016.
 */

public class LocationTools implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    Context context;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest = new LocationRequest();
    private Location currentLocation;
    private PermsManager permsManager;
    private String[] perms = {android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private LocationRequest locationRequestHighAccuracy = new LocationRequest();;
    private LocationRequest locationRequestBalancedPowerAccuracy = new LocationRequest();
    private boolean isLocationEnabled = false;


    public LocationTools(Context context) {
        this.context = context;
        permsManager = new PermsManager();

        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();

        createLocationRequest();
        checkLocationEnabled();

    }

    public boolean isLocationEnabled() {
        return isLocationEnabled;
    }

    /**
     * check if location services is enabled
     *
     * @return
     */
    public void checkLocationEnabled() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequestHighAccuracy)
                .addLocationRequest(locationRequestBalancedPowerAccuracy);

        PendingResult<LocationSettingsResult> result = LocationServices
                .SettingsApi
                .checkLocationSettings(googleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                if (status.getStatusCode() == LocationSettingsStatusCodes.SUCCESS) {
                    isLocationEnabled = true;
                } else {
                    isLocationEnabled = false;
                }
            }
        });
    }


    public void enableLocation() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequestHighAccuracy)
                .addLocationRequest(locationRequestBalancedPowerAccuracy);

        PendingResult<LocationSettingsResult> result = LocationServices
                .SettingsApi
                .checkLocationSettings(googleApiClient, builder.build());


        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates locationSettingsStatusCodes = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult((Activity)context, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });

    }

    /**
     * create location request
     */
    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (checkPerms()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient, locationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        setCurrentLocation(location);

    }

    public void setCurrentLocation(Location location) {
        currentLocation = location;
    }

    /**
     * get current or last know or default location
     * @return Location
     */
    public Location getLocation() {
        if (isLocationEnabled()){
            if (currentLocation != null) {
                return currentLocation;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest, this);
            if (LocationServices.FusedLocationApi.getLastLocation(googleApiClient) != null) {
                return LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            }
        }
        return new Location("service Provider");
    }

    /**
     * Always need to check permissions...always !
     * @return boolean
     */
    public boolean checkPerms(){
        if (ActivityCompat.checkSelfPermission(context, perms[0]) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, perms[1]) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    public static int getRequestCheckSettings() {
        return REQUEST_CHECK_SETTINGS;
    }
}
