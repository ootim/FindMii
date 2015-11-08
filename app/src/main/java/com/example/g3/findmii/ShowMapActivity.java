package com.example.g3.findmii;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;
import java.util.ArrayList;

public class ShowMapActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    double latitude, longitude;

    // heatmap
    HeatmapTileProvider mHeatMapProvider;
    TileOverlay mHeatMapTileOverlay;
    private ArrayList<WeightedLatLng> hmapData = new ArrayList<>();
    private ArrayList<LatLng> latlngs = new ArrayList<>();
    private ArrayList<Double> weights = new ArrayList<>();
    final static Float MAX_ZOOM = 15.5f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_map);
        GetLocation loc = new GetLocation(ShowMapActivity.this);
        latitude = loc.getLatitude();
        longitude = loc.getLongitude();


        //@replace with data from server;
        latlngs.add(new LatLng(50.8677065292, -0.0881093842587));
        latlngs.add(new LatLng(latitude, longitude));


        weights.add(1.0);weights.add(2.0);

        //setup the map
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                addHeatMap(getHeatmapData(latlngs, weights));
                //setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        GetLocation loc = new GetLocation(ShowMapActivity.this);
        double latitude = loc.getLatitude();
        double longitude = loc.getLongitude();
        String address = loc.getAddressFromLatAndLong(latitude, longitude);
        String address2 = loc.getAddressFromLatAndLong(50.8677065292, -0.0881093842587);
        String padd = "Flat 66C, East Slope, Refectory Road, Falmer, Brighton,";

        //Marker  mMapMarker = mMap.addMarker(new MarkerOptions().position(loc.getLatLongFromAddress(address))
          //     .title(padd));
        Marker mMapMarker2 = mMap.addMarker(new MarkerOptions().position(new LatLng(50.8677065292, -0.0881093842587
        )).snippet(address2));
        /*mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMapMarker.showInfoWindow();*/

        // Center the map
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), MAX_ZOOM));
    }

    private void addHeatMap(ArrayList<WeightedLatLng> hmapData)
    {



        //if(mHeatMapProvider =null) {

        mHeatMapProvider = new HeatmapTileProvider.Builder().weightedData(hmapData).build();
        mHeatMapProvider.setRadius(100);
        mHeatMapTileOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mHeatMapProvider));
        mHeatMapTileOverlay.clearTileCache();
        addMarkers(latlngs, weights, mMap);
        //mMap.getUiSettings().setZoomGesturesEnabled(false);
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

            @Override
            public void onCameraChange(CameraPosition pos) {
                if (pos.zoom < MAX_ZOOM) {
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(MAX_ZOOM));
                }
            }
        });
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude), MAX_ZOOM));
    }

    public ArrayList<WeightedLatLng> getHeatmapData(ArrayList<LatLng> dataLatLng, ArrayList<Double> latLngWeight) {
        ArrayList<WeightedLatLng> weightedData = new ArrayList<WeightedLatLng>();
        if(dataLatLng.size() != latLngWeight.size()){
            Log.i("HEATMAP_DATA","LatLng size must match weight size");
            return null;
        }
        for(int i=0; i<dataLatLng.size(); i++){
            WeightedLatLng wlatlng = new WeightedLatLng(dataLatLng.get(i),latLngWeight.get(i));
            hmapData.add(wlatlng);
        }
        return hmapData;
    }

    private void addMarkers(ArrayList<LatLng> dataLatLng, ArrayList<Double> avgPrice, GoogleMap mMap){
        if(dataLatLng.size() != avgPrice.size()){
            //create an exception here!
            Log.i("HEATMAP_DATA","LatLng size must match weight size");
        }
        for(int i=0; i<dataLatLng.size(); i++){
            Marker mMapMarker = mMap.addMarker(new MarkerOptions().position(dataLatLng.get(i)).title("Average price: " + avgPrice.get(i).toString()));
        }
    }
}
