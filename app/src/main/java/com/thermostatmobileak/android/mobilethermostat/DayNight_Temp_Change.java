package com.thermostatmobileak.android.mobilethermostat;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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
    protected void onCreate(Bundle savedInstanceState) { // method that gets called when activity is created
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daynight_temp_change);

        //imagebuttons for the day and night temperature respectively
        plus_day = (ImageButton) findViewById(R.id.change_dayPlus);
        minus_day = (ImageButton) findViewById(R.id.change_dayMinus);
        plus_night = (ImageButton) findViewById(R.id.change_nightPlus);
        minus_night = (ImageButton) findViewById(R.id.change_nightMinus);

        // The two circular seekbars (one for day- and the other for the night temperature)
        daySeekbar = (CircularSeekBar) findViewById(R.id.change_daySeekbar);
        nightSeekbar = (CircularSeekBar) findViewById(R.id.change_nightSeekbar);

        // The textviews that display the current day and night temperature respectively
        change_daytemp = (TextView) findViewById(R.id.change_daytemp);
        change_nighttemp = (TextView) findViewById(R.id.change_nighttemp);

        // fetch day and night temp from the server and set everything accoriding to this data
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    day_temp = Double.parseDouble(HeatingSystem.get("dayTemperature")); // get daytemperature
                    change_daytemp.setText(day_temp + ""); // set daytemperature
                    progress_DaySeekbar(); // set day circular seekbar according to this temperature
                    night_temp = Double.parseDouble(HeatingSystem.get("nightTemperature")); // get nighttemperature
                    change_nighttemp.setText(night_temp + ""); // set night temperature
                    progress_NightSeekbar(); // set night circular seekbar according to this temperature
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
                    day_temp = day_temp + 0.1; // increase by 0.1
                    changeDayTemp(); // change the textviews that displays the day temperature
                    progress_DaySeekbar(); // set day circular seekbar according to the new day temperature
                    max_and_min(); // checking of the limits et cetera
                }
                updateDayTemp();
            }
        });

        // Listener for the (image)button that decreases the day-temperature by 0.1
        minus_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (day_temp >= 5) { // minimum is 5 degree celsius
                    // similar to the previous onclickListener, but now for decrasing the temperature
                    day_temp = day_temp - 0.1;
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
                day_temp = (progress + 50) / 10.0; // update day temp according to changes in the circular seekbar
                changeDayTemp(); // change the textview for the day temperature
                max_and_min(); // checking of the limits et cetera
                updateDayTemp(); // send the new day temperature to the server
            }

            @Override  // we need to override this even though we don't need it
            public void onStartTrackingTouch(CircularSeekBar seekBar) {
            }

            @Override // we need to override this even though we don't need it
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
            }
        });

        // Listener for the (image)button that increases the night-temperature by 0.1
        plus_night.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (night_temp <= 30) { // max is 30 degree celsius
                    // Same as the plus_day onclickListener, but now for the night temperature
                    // for more info, see line 67
                    night_temp = night_temp + 0.1;
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
                // Same as the minus_day onclickListener, but now for the night temperature
                // for more info, see line 81
                if (night_temp >= 5) { // minimum is 5 degree celsius
                    night_temp = night_temp - 0.1;
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
                // Same as the daySeekbar onseekbarChangeListener, but now for the night temperature
                // for more info, see line 96
                night_temp = (progress + 50) / 10.0;
                changeNightTemp();
                max_and_min();
                updateNightTemp();
            }

            @Override // we need to override this even though we don't need it
            public void onStartTrackingTouch(CircularSeekBar seekBar) {
            }

            @Override // we need to override this even though we don't need it
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
            }
        });

    }

    // We need this method for the help button in the activity bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // We need this method for the help button in the activity bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // setting up the dialog that pops up when you click on info in the menu
        if (id == R.id.action_info) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle("Day and Night Temperature Info"); // title of the dialog box
            builder.setMessage(R.string.info_daynight); // info text
            builder.setPositiveButton("Go Back", null); // button that goes back to the activity
            builder.show();
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
                change_daytemp.setText(day_temp + ""); // change the textview according to the changes
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
                change_nighttemp.setText(night_temp + ""); // change the textview according to the changes
            }
        });
    }

    // change/set the day temperature on the web service
    public void updateDayTemp(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HeatingSystem.put("dayTemperature", "" + day_temp); // send to the server
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
                    HeatingSystem.put("nightTemperature", "" + night_temp); // send to the server
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
            plus_day.setImageResource(R.drawable.up_fill96_grey); // same image, but then grey
            minus_day.setClickable(true);
        } else if (day_temp == 5) {
            plus_day.setClickable(true);
            minus_day.setClickable(false);
            minus_day.setImageResource(R.drawable.down_fill96_grey); // same image, but then grey
        } else {
            plus_day.setClickable(true);
            plus_day.setImageResource(R.drawable.up_fill96);
            minus_day.setClickable(true);
            minus_day.setImageResource(R.drawable.down_fill96);
        }

        if (night_temp == 30) {
            plus_night.setClickable(false);
            plus_night.setImageResource(R.drawable.up_fill96_grey); // same image, but then grey
            minus_night.setClickable(true);
            minus_night.getBackground().setColorFilter(null);
        } else if (night_temp == 5) {
            plus_night.setClickable(true);
            minus_night.setClickable(false);
            minus_night.setImageResource(R.drawable.down_fill96_grey); // same image, but then grey
        } else {
            plus_night.setClickable(true);
            plus_night.setImageResource(R.drawable.up_fill96);
            minus_night.setClickable(true);
            minus_night.setImageResource(R.drawable.down_fill96);
        }
    }

    /* Reload day & night temperature when the day activity is revisited */
    public void onResume() { // this method gets called when we resume this activity (instead of oncreate)
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
                            change_daytemp.setText(day_temp + ""); //set day temperature textview correct
                            change_nighttemp.setText(night_temp + ""); // set night temperatuer textview correct
                            progress_DaySeekbar(); // set day temperature circular seekbar correct
                            progress_NightSeekbar(); // set night temperature circular seekbar correct
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();
    }

    // Show toast message to notify the users that the changes are saved when this activity is destroyed
    public void onDestroy() {
        super.onDestroy();
        Toast toast_changes_saved = Toast.makeText(getApplicationContext(), "Day and night temperature change(s) are saved", Toast.LENGTH_SHORT);
        toast_changes_saved.show(); // display the toast: length short
    }

}
