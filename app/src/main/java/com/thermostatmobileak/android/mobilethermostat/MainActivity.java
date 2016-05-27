package com.thermostatmobileak.android.mobilethermostat;

import android.support.v7.app.AppCompatActivity;


import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import org.thermostatapp.util.HeatingSystem;
import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    Button plus_button, minus_button;
    TextView current_temp, desired_temp;
    TextView day_temp_home, night_temp_home;
    double curr_temp, des_temp;
    double day_temp, night_temp;
    SeekBar temp_seekbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        plus_button = (Button) findViewById(R.id.plusbutton);
        minus_button = (Button) findViewById(R.id.minusbutton);
        temp_seekbar = (SeekBar) findViewById(R.id.temp_seekbar);

        HeatingSystem.BASE_ADDRESS = "http://pcwin889.win.tue.nl/2id40-ws/004";

        current_temp = (TextView) findViewById(R.id.current_temp);
        desired_temp = (TextView) findViewById(R.id.target_temp);
        temp_seekbar = (SeekBar) findViewById(R.id.temp_seekbar);

        day_temp_home = (TextView) findViewById(R.id.day_temp_home);
        night_temp_home = (TextView) findViewById(R.id.night_temp_home);

        // Thread to get the values of our heatingsystem for night / day temperatures
         new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    day_temp = Double.parseDouble(HeatingSystem.get("dayTemperature"));
                    night_temp = Double.parseDouble(HeatingSystem.get("nightTemperature"));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            day_temp_home.setText("Day temperature: " + day_temp + " \u2103");
                            night_temp_home.setText("Night temperature: " + night_temp +  " \u2103");
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();
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
