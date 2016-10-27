package com.example.nico.locationtools;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

/**
 * Created by Nico on 26/10/2016.
 */

public class PermsManager {


    public boolean checkPerms(String perms, Activity activity){
        if (ContextCompat.checkSelfPermission(activity, perms) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    public void askPerms(String perms, Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(new String[]{perms}, 1);
        }
    }

}
