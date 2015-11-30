package com.example.g3.findmii;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ArrayList<ArrayList<String>> favList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnLocation = (Button)findViewById(R.id.btn_show_map);
        final TextView txtLoc = (TextView) findViewById(R.id.txt_view);
        Button btnFavs = (Button)findViewById(R.id.btn_show_favs);

       // btnLocation.setVisibility(View.INVISIBLE);
        GetLocation loc = new GetLocation(MainActivity.this);

        /*double latitude = loc.getLatitude();
        double longitude = loc.getLongitude();
        String address = loc.getAddressFromLatAndLong(latitude,longitude);
        txtLoc.setText(address);*/

        List<Address>  addresses = loc.getAddressList();

        //txtLoc.setText(addresses.toString());//addresses

        btnLocation.setOnClickListener(new Button.OnClickListener() {

            GetLocation loc = new GetLocation(MainActivity.this);

            @Override
            public void onClick(View v) {

                if(!loc.isNetworkEnabled()){
                    txtLoc.setText("Please enable location detection on your device, then continue");
                    showSettingsAlert("NETWORK");
                }else {
                    //txtLoc.setText(loc.getAddressFromLatAndLong(loc.getLatitude(),loc.getLongitude()));
                   try {

                        //intent to display map activity
                      Intent map = new Intent(MainActivity.this, ShowMapActivity.class);
                       startActivity(map);

                    } catch (Exception e) {
                        // Log any error messages to LogCat using Log.e()
                        Log.e("ShowMapIntent", e.toString());
                    }
                }
            }
        });
        btnFavs.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                ShowMapActivity favs = new ShowMapActivity();
                favList = new ArrayList<>();
                boolean f = favs.isFavourite(MainActivity.this, null);
                if(!favs.isFavourite(MainActivity.this, null)){
                    Toast.makeText(getApplicationContext(),"No Favourites",Toast.LENGTH_LONG);
                }else{
                    favList = favs.getFavourites(MainActivity.this);

                    Intent i = new Intent(MainActivity.this, FavouriteList.class);
                    ArrayList<String> values = new ArrayList<>();
                    for(ArrayList<String> current : favList){
                        String tmp = current.get(3) + "\n" + current.get(2) + "\n" + current.get(0)
                                + ", " + current.get(1) + "\n" + current.get(4);
                        values.add(tmp);
                    }
                    String[] favValues = values.toArray(new String[values.size()]);
                    i.putExtra("favouritelist",favValues);
                    MainActivity.this.startActivity(i);
                }

            }
        });
    }
    public void showSettingsAlert(String provider) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                MainActivity.this);

        alertDialog.setTitle(provider + " SETTINGS");

        alertDialog
                .setMessage(provider + " is not enabled! Want to go to settings menu?");

        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        MainActivity.this.startActivity(intent);
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void displayFavouriteList(ArrayList<ArrayList<String>> favList){
        Intent i = new Intent(MainActivity.this, FavouriteList.class);
        ArrayList<String> values = new ArrayList<>();
        for(ArrayList<String> current : favList){
            String tmp = current.get(4) + "\n" + current.get(3) + "\n " + current.get(1)
                    + ", " + current.get(2) + "\n Average Price: " + current.get(5);
            values.add(tmp);
        }
        String[] favValues = values.toArray(new String[values.size()]);
        i.putExtra("favouritelist",favValues);
        MainActivity.this.startActivity(i);
    }
}

