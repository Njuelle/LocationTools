package com.example.nico.locationtools;

import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by Nico on 27/10/2016.
 */

abstract class LocationActivity extends AppCompatActivity {

    private PermsManager permsManager = new PermsManager();
    protected LocationTools locationTools;


    /**
     * Need to check permissions and maybe ask user for them
     */
    @Override
    public void onStart() {
        super.onStart();
        if (permsManager.checkPerms(android.Manifest.permission.ACCESS_FINE_LOCATION, this)) {
            initLocation();
        } else {
            permsManager.askPerms(android.Manifest.permission.ACCESS_FINE_LOCATION, this);
        }
    }

    /**
     * Callback method for requesting permissions
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initLocation();
                } else {
                    Toast.makeText(this, "No location for you", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    /**
     * Instantiate locationTools
     */
    public void initLocation() {
        locationTools = new LocationTools(this);
    }

    /**
     * Get location by locationTools object
     * @return Location
     */
    public Location getLocation(){
        return locationTools.getLocation();
    }
}
