package com.thermostatmobileak.android.mobilethermostat;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.devadvance.circularseekbar.CircularSeekBar;

import org.thermostatapp.util.HeatingSystem;

/**
 * Created by s158881 on 29-5-2016.
 */
public class DayNight_Temp_Change extends AppCompatActivity {
    double day_temp, night_temp;
    ImageButton plus_day, minus_day, plus_night, minus_night;
    TextView change_daytemp, change_nighttemp;
    CircularSeekBar daySeekbar, nightSeekbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daynight_temp_change);
        plus_day = (ImageButton) findViewById(R.id.change_dayPlus);
        minus_day = (ImageButton) findViewById(R.id.change_dayMinus);
        plus_night = (ImageButton) findViewById(R.id.change_nightPlus);
        minus_night = (ImageButton) findViewById(R.id.change_nightMinus);

        daySeekbar = (CircularSeekBar) findViewById(R.id.change_daySeekbar);

        nightSeekbar = (CircularSeekBar) findViewById(R.id.change_nightSeekbar);


        change_daytemp = (TextView) findViewById(R.id.change_daytemp);
        change_nighttemp = (TextView) findViewById(R.id.change_nighttemp);

        // fetch day and night temp from the server and set everything accoriding to this data
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    day_temp = Double.parseDouble(HeatingSystem.get("dayTemperature"));
                    change_daytemp.setText(day_temp + "");
                    progress_DaySeekbar();
                    night_temp = Double.parseDouble(HeatingSystem.get("nightTemperature"));
                    change_nighttemp.setText(night_temp + "");
                    progress_NightSeekbar();
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();

        // Listener for the (image)button that increases the day-temperature by 0.1
        plus_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (day_temp <= 30) { // max is 30 degree celsius
                    day_temp += 0.1;
                    changeDayTemp();
                    progress_DaySeekbar();
                    max_and_min();
                }
                updateDayTemp();
            }
        });

        // Listener for the (image)button that decreases the day-temperature by 0.1
        minus_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (day_temp >= 5) { // minimum is 5 degree celsius
                    day_temp -= 0.1;
                    changeDayTemp();
                    progress_DaySeekbar();
                    max_and_min();
                }
                updateDayTemp();
            }
        });

        // Listener for the circular seekbar for the changing of the day-temperatur
        daySeekbar.setOnSeekBarChangeListener(new com.thermostatmobileak.android.mobilethermostat.CircleSeekBarListener(){
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
                day_temp = (progress + 50) / 10.0;
                changeDayTemp();
                max_and_min();
                updateDayTemp();
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
            }
        });

        // Listener for the (image)button that increases the night-temperature by 0.1
        plus_night.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (night_temp <= 30) { // max is 30 degree celsius
                    night_temp += 0.1;
                    changeNightTemp();
                    progress_NightSeekbar();
                    max_and_min();
                }
                updateNightTemp();
            }
        });

        // Listener for the (image)button that decreases the night-temperature by 0.1
        minus_night.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (night_temp >= 5) { // minimum is 5 degree celsius
                    night_temp -= 0.1;
                    changeNightTemp();
                    progress_NightSeekbar();
                    max_and_min();
                }
                updateNightTemp();
            }
        });

        // Listener for the circular seekbar for the changing of the night-temperature
        nightSeekbar.setOnSeekBarChangeListener(new com.thermostatmobileak.android.mobilethermostat.CircleSeekBarListener(){
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
                night_temp = (progress + 50) / 10.0;
                changeNightTemp();
                max_and_min();
                updateNightTemp();
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
            }
        });

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

    // change/set the textview for the day temperature
    public void changeDayTemp() {
        day_temp = Math.round(day_temp*10);
        day_temp = day_temp/10;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                change_daytemp.setText(day_temp + "");
            }
        });
    }

    // change/set the textview for the night temperature
    public void changeNightTemp() {
        night_temp = Math.round(night_temp*10);
        night_temp = night_temp/10;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                change_nighttemp.setText(night_temp + "");
            }
        });
    }

    // change/set the day temperature on the web service
    public void updateDayTemp(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HeatingSystem.put("dayTemperature", "" + day_temp);
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();
    }

    // change/set the night temperature on the web service
    public void updateNightTemp(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HeatingSystem.put("nightTemperature", "" + night_temp);
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();
    }

    // update the progress attribute of the seekbar for the day-temperature
    void progress_DaySeekbar(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                daySeekbar.setProgress((int) (day_temp * 10 - 50)); // setProgress only accepts int data-type
            }
        });
    }

    // update the progress attribute of the seekbar for the night-temperature
    void progress_NightSeekbar(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                nightSeekbar.setProgress((int) (night_temp * 10 - 50)); // setProgress only accepts int data-type
            }
        });
    }

    // graying out buttons/making them unclickable and vice versa when max and min temperatures are reached
    public void max_and_min(){
        if (day_temp == 30) {
            plus_day.setClickable(false);
            plus_day.getBackground().setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY);
            minus_day.setClickable(true);
            minus_day.getBackground().setColorFilter(null);
        } else if (day_temp == 5) {
            plus_day.setClickable(true);
            minus_day.setClickable(false);
            minus_day.getBackground().setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY);
        } else {
            plus_day.setClickable(true);
            plus_day.getBackground().setColorFilter(null);
            minus_day.setClickable(true);
            minus_day.getBackground().setColorFilter(null);
        }

        if (night_temp == 30) {
            plus_night.setClickable(false);
            plus_night.getBackground().setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY);
            minus_night.setClickable(true);
            minus_night.getBackground().setColorFilter(null);
        } else if (night_temp == 5) {
            plus_night.setClickable(true);
            minus_night.setClickable(false);
            minus_night.getBackground().setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY);
        } else {
            plus_night.setClickable(true);
            plus_night.getBackground().setColorFilter(null);
            minus_night.setClickable(true);
            minus_night.getBackground().setColorFilter(null);
        }
    }

    /* Reload day & night temperature when the day activity is revisited */
    public void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    day_temp = Double.parseDouble(HeatingSystem.get("dayTemperature"));
                    night_temp = Double.parseDouble(HeatingSystem.get("nightTemperature"));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            change_daytemp.setText(day_temp + "");
                            change_nighttemp.setText(night_temp + "");
                            progress_DaySeekbar();
                            progress_NightSeekbar();
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();
    }
}
