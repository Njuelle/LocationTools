package com.example.nico.locationtools;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Nico on 26/10/2016.
 */

public class LocationTools implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    Context context;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location currentLocation;
    private PermsManager permsManager;
    private String[] perms = {android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

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

    }

    /**
     * check if location services is enabled
     *
     * @return boolean
     */
    public boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        }
        return false;
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
     * get last know location or current location or default location
     * @return Location
     */
    public Location getLocation() {
        if (isLocationEnabled()){
            if (currentLocation != null) {
                Log.e("currentLocation", "omg");
                return currentLocation;
            }
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


}
