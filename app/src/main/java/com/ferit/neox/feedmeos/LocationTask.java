package com.ferit.neox.feedmeos;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import static android.content.Context.LOCATION_SERVICE;

class LocationTask extends Thread {

    private static final int LOCATION_REQUEST = 500;
    LocationListener locListener;
    LocationManager locManager;
    Location location;
    String locProvider;
    Context context;

    public LocationTask(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        locListener = new SimpleLocationListener();
        locManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        startTracking();
    }

    private void startTracking() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        locProvider = this.locManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        if (Looper.myLooper() == null)
            Looper.prepare();
        locManager.requestSingleUpdate(locProvider, locListener, Looper.myLooper());
    }

    class SimpleLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location mLocation) {
            location = mLocation;
            Log.e("TAGtred", String.valueOf(location.getLatitude()));
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            //locManager.removeUpdates(locListener);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
