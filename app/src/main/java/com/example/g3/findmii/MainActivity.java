package com.example.g3.findmii;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnLocation = (Button)findViewById(R.id.btn_show_map);
        final TextView txtLoc = (TextView) findViewById(R.id.txt_view);



        btnLocation.setOnClickListener(new Button.OnClickListener() {

            GetLocation loc = new GetLocation(MainActivity.this);

            @Override
            public void onClick(View v) {

                if(loc.isNetworkEnabled()==false){
                    txtLoc.setText("Please enable location detection on your device, then continue");
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
}
