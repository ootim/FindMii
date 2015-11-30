package com.example.g3.findmii;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ShowMapActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    double latitude, longitude;

    // heatmap
    HeatmapTileProvider mHeatMapProvider;
    TileOverlay mHeatMapTileOverlay;
    private ArrayList<WeightedLatLng> hmapData = new ArrayList<>();
    private ArrayList<LatLng> latlngs = new ArrayList<>();
    private ArrayList<Double> weights = new ArrayList<>();
    final static Float MAX_ZOOM = 16.0f;
    ArrayList<Marker> hMapMarkers = new ArrayList<>();
    ArrayList<ArrayList<String>> favs;


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


        weights.add(1.0);
        weights.add(2.0);

        //setup the map
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){
        if(e.getAction()==MotionEvent.ACTION_DOWN){
            return true;
        }
        return false;
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

    private void addHeatMap(ArrayList<WeightedLatLng> hmapData) {
        mMap.clear();
        mHeatMapProvider = new HeatmapTileProvider.Builder().weightedData(hmapData).build();
        mHeatMapProvider.setRadius(100);
        mHeatMapTileOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mHeatMapProvider));
        mHeatMapTileOverlay.clearTileCache();
        //final ArrayList<WeightedLatLng> mapData = hmapData;
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

            @Override
            public void onCameraChange(CameraPosition pos) {
                /*if (pos.zoom < MAX_ZOOM) {
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(MAX_ZOOM));
                }*/
                if (pos.zoom > MAX_ZOOM) {
                    addMarkers(latlngs, weights, mMap);
                } else {
                    removeHeatMapMarkers();
                    mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Here you are")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                }
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                showFavouriteDialog(marker);
                //Toast.makeText(ShowMapActivity.this,String.valueOf(marker.getPosition().latitude),Toast.LENGTH_LONG).show();
                return false; // shows default as well
            }
        });

        //mMap.getUiSettings().setZoomGesturesEnabled(false);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), MAX_ZOOM));
    }

    public void showFavouriteDialog(Marker makr){
       final Marker marker = makr;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                ShowMapActivity.this);

        alertDialog.setTitle("Favourites");

        alertDialog
                .setMessage("Add to Favourite location?");

        alertDialog.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String postcode = getAddressFromLatAndLong(marker.getPosition().latitude, marker.getPosition().longitude, "postcode");
                        String address = getAddressFromLatAndLong(marker.getPosition().latitude, marker.getPosition().longitude, "address");
                        // save to favourites
                        if (isFavourite(ShowMapActivity.this, postcode)) {
                            Toast.makeText(ShowMapActivity.this, "Already a Favourite!", Toast.LENGTH_LONG).show();
                        } else {
                            if (addToFavourites(marker.getPosition().latitude, marker.getPosition().longitude, postcode, address,
                                    marker.getTitle(), "createdat")) {
                                Toast.makeText(ShowMapActivity.this, "Added to favourites", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(ShowMapActivity.this, "Sorry, could not add to favourites", Toast.LENGTH_LONG).show();
                            }
                        }
                        //;
                    }
                });

        alertDialog.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        //setFavourites();
                    }
                });
        alertDialog.show();
    }
    public ArrayList<WeightedLatLng> getHeatmapData(ArrayList<LatLng> dataLatLng, ArrayList<Double> latLngWeight) {
        ArrayList<WeightedLatLng> weightedData = new ArrayList<WeightedLatLng>();
        if (dataLatLng.size() != latLngWeight.size()) {
            Log.i("HEATMAP_DATA", "LatLng size must match weight size");
            return null;
        }
        for (int i = 0; i < dataLatLng.size(); i++) {
            WeightedLatLng wlatlng = new WeightedLatLng(dataLatLng.get(i), latLngWeight.get(i));
            hmapData.add(wlatlng);
        }
        return hmapData;
    }

    private void addMarkers(ArrayList<LatLng> dataLatLng, ArrayList<Double> avgPrice, GoogleMap mMap) {
        if (dataLatLng.size() != avgPrice.size()) {
            //create an exception here!
            Log.i("HEATMAP_DATA", "LatLng size must match weight size");
        }
        for (int i = 0; i < dataLatLng.size(); i++) {
            Marker mMapMarker = mMap.addMarker(new MarkerOptions().position(dataLatLng.get(i)).title("Average price: " + avgPrice.get(i).toString()));
            //hMapMarkers = new ArrayList<Marker>();
            hMapMarkers.add(mMapMarker);
        }
    }

    private void removeHeatMapMarkers() {

        if (hMapMarkers.size() > 0) {
            for (int i = 0; i < hMapMarkers.size(); i++) {
                hMapMarkers.get(i).remove();
            }
        }
    }
    boolean addToFavourites(Double latitude, Double longitude, String postcode, String address, String avgprice, String createdate){
        FavouriteReaderDbHelper mDbHelper = new FavouriteReaderDbHelper(getApplicationContext());
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FavouriteDBSchema.FavouriteSchema.COLUMN_NAME_LATITUDE, latitude);
        values.put(FavouriteDBSchema.FavouriteSchema.COLUMN_NAME_LONGITUDE, longitude);
        values.put(FavouriteDBSchema.FavouriteSchema.COLUMN_NAME_POSTCODE, postcode);
        values.put(FavouriteDBSchema.FavouriteSchema.COLUMN_NAME_ADDRESS, address);
        values.put(FavouriteDBSchema.FavouriteSchema.COLUMN_NAME_AVG_PRICE, avgprice);
        //values.put(FavouriteDBSchema.FavouriteSchema.COLUMN_NAME_CREATED_AT, createdate);

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                FavouriteDBSchema.FavouriteSchema.TABLE_NAME,
                FavouriteDBSchema.FavouriteSchema.COLUMN_NAME_POSTCODE,
                values);
        if(newRowId>0.0){
            return true;
        }else {
            return false;
        }
    }

    void setFavourites(Context c){
        FavouriteReaderDbHelper mDbHelper = new FavouriteReaderDbHelper(c);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + FavouriteDBSchema.FavouriteSchema.TABLE_NAME;
        Cursor results = db.rawQuery(query, null);
        favs = new ArrayList<>();

        if (results.moveToFirst()) {
            while (results.isAfterLast() == false) {
                ArrayList<String> fav = new ArrayList<>();
                fav.add(results.getString(results.getColumnIndexOrThrow(FavouriteDBSchema.FavouriteSchema.COLUMN_NAME_LATITUDE)));
                fav.add(results.getString(results.getColumnIndexOrThrow(FavouriteDBSchema.FavouriteSchema.COLUMN_NAME_LONGITUDE)));
                fav.add(results.getString(results.getColumnIndexOrThrow(FavouriteDBSchema.FavouriteSchema.COLUMN_NAME_POSTCODE)));
                fav.add(results.getString(results.getColumnIndexOrThrow(FavouriteDBSchema.FavouriteSchema.COLUMN_NAME_ADDRESS)));
                fav.add(results.getString(results.getColumnIndexOrThrow(FavouriteDBSchema.FavouriteSchema.COLUMN_NAME_AVG_PRICE)));
                favs.add(fav);
                results.moveToNext();
            }
        }

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
           /* String[] projection = {
                    FavouriteDBSchema.FavouriteSchema.COLUMN_NAME_POSTCODE,
                    FavouriteDBSchema.FavouriteSchema.COLUMN_NAME_LATITUDE,
                    FavouriteDBSchema.FavouriteSchema.COLUMN_NAME_LONGITUDE,
                    FavouriteDBSchema.FavouriteSchema.COLUMN_NAME_ADDRESS,
                    FavouriteDBSchema.FavouriteSchema.COLUMN_NAME_AVG_PRICE,
                    FavouriteDBSchema.FavouriteSchema.COLUMN_NAME_CREATED_AT,
            };

            // How you want the results sorted in the resulting Cursor
            String sortOrder =
                    FavouriteDBSchema.FavouriteSchema.COLUMN_NAME_CREATED_AT + " DESC";
            Cursor results = db.query(
                    FavouriteDBSchema.FavouriteSchema.TABLE_NAME,  // The table to query
                    projection,                               // The columns to return
                    null,                                // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );
        */
            //results.moveToFirst();
            //double latitude = results.getDouble(results.getColumnIndexOrThrow(FavouriteDBSchema.FavouriteSchema.COLUMN_NAME_LATITUDE));
            //String address = results.getString(results.getColumnIndexOrThrow(FavouriteDBSchema.FavouriteSchema.COLUMN_NAME_AVG_PRICE));
       // Toast.makeText(this,String.valueOf(favs.get(1).size()),Toast.LENGTH_LONG).show();
            results.close();
        }

    boolean isFavourite(Context c, String postcode){
        try {
            FavouriteReaderDbHelper mDbHelper = new FavouriteReaderDbHelper(c);
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            String query;
            if (postcode != null) {
                query = "SELECT * FROM " + FavouriteDBSchema.FavouriteSchema.TABLE_NAME + " WHERE " +
                        FavouriteDBSchema.FavouriteSchema.COLUMN_NAME_POSTCODE + " = '" + postcode + "'";
            } else {
                query = "SELECT * FROM " + FavouriteDBSchema.FavouriteSchema.TABLE_NAME;
            }
            Cursor results = db.rawQuery(query, null);

            if (results.getCount() <= 0) {
                results.close();
                return false;
            }
            results.close();
            return true;
        }catch(SQLiteException e){
            return false;
        }

    }
    int getNumberOfFavourites(){
        return favs.size();
    }
    ArrayList<ArrayList<String>> getFavourites(Context c){
        setFavourites(c);
        return favs;
    }
    //return the address from latitude and longitude
    public String getAddressFromLatAndLong(double latitude, double longitude,String type) {
        String postCode=null;
        /*try {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 3);
            if (addresses.size() > 0) {
                postCode = addresses.get(0).getPostalCode();
                address = addresses.get(0).getThoroughfare();
            }
        } catch (IOException e) {
            Log.e("Geocoder", e.getMessage());
        }*/
        StringBuilder address = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                for (int i = 0; i<addresses.size(); i++) {
                    Address addr = addresses.get(i);
                    address.append(addr.getAddressLine(i));
                    address.append(", ");
                }
            }
        } catch (IOException e) {
            Log.e("Geocoder", e.getMessage());
        }

        if(type=="postcode"){
            return postCode;
        }
        return address.toString();
    }
}
