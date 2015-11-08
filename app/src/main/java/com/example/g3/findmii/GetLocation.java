package com.example.g3.findmii;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by Timileyin on 09/10/2015.
 */
public class GetLocation extends Service implements LocationListener {

    private final Context aContext;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute


    // Declaring a Location Manager
    protected LocationManager locationManager;

    public GetLocation(Context context){
        this.aContext = context;
        this.getNetworkStatus();
    }

    private void getNetworkStatus()
    {
        try {
            locationManager = (LocationManager) aContext.getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch (Exception e) {
            Log.e("NetworkStatus", e.getMessage());
        }
    }
    public boolean isNetworkEnabled(){
        this.getNetworkStatus();
        if (!isGPSEnabled && !isNetworkEnabled) {
            return false;
        }
        return true;


    }

    public Location getGPSCoordinates() {
        try {
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    try {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    Log.d("Network", "Network");
                    if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        }
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }catch(SecurityException se){
                        se.printStackTrace();
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        try {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            Log.d("GPS Enabled", "GPS Enabled");
                            if (locationManager != null) {
                                location = locationManager
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        }catch (SecurityException se){
                            se.printStackTrace();
                        }
                    }
                }

        } catch (Exception e) {
            Log.e("LocationManager", e.getMessage());
        }
        return location;
    }

    //return latitude
    public double getLatitude(){
        return this.getGPSCoordinates().getLatitude();
    }

    //return longitude
    public double getLongitude(){
        return this.getGPSCoordinates().getLongitude();
    }

    //return the address from latitude and longitude
    public String getAddressFromLatAndLong(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(aContext, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 15);
            if (addresses.size() > 0) {
                result.append(addresses.size());
                result.append("\n");
                for(int j=0;j<addresses.size();j++){
                    Address address = addresses.get(j);
                    //result.append(address.getAddressLine(j));
                    //result.append(", ");
                    //result.append(address.getAddressLine(j));
                    for(int i=0;i<address.getMaxAddressLineIndex();i++) {
                        result.append(address.getAddressLine(i));
                        result.append(address.getLatitude());
                        //result.append("\n");
                    }
                    result.append("\n------------------");
               }
            }
        } catch (IOException e) {
            Log.e("Geocoder", e.getMessage());
        }

        return result.toString();
    }
    public List<Address> getAddressList()
    {
        //address = "66c, east slope, refectory road, famler, brighton";
        Geocoder geocoder = new Geocoder(aContext,Locale.getDefault());
        try {
             List<Address> add = geocoder.getFromLocation(getLatitude(),getLongitude(),5);
            return add;

        } catch (IOException e) {
            Log.i("FROM_LOCATION_NAME","err");
        }
        return null;
    }


    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}
