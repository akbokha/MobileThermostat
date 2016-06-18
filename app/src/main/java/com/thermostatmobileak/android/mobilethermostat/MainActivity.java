package com.thermostatmobileak.android.mobilethermostat;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;


import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.thermostatapp.util.HeatingSystem;

import java.net.ConnectException;

import com.devadvance.circularseekbar.CircularSeekBar;
import com.devadvance.circularseekbar.CircularSeekBar.OnCircularSeekBarChangeListener;


public class MainActivity extends AppCompatActivity  {

    ImageButton plus_button, minus_button;
    Button change_button, weekprogramButton;
    TextView current_temp, desired_temp;
    TextView day_temp_home, night_temp_home;
    double curr_temp, des_temp;
    double day_temp, night_temp;
    CircularSeekBar temp_seekbar;
    ImageView flame_drawable;
    CheckBox weekProgram;
    TextView weekProgramState, text_checkbox_state;
    boolean weekProgramEnabled, permanent_wp_mode = true;
    ImageView info_button, connection_logo, thermo_icon_home;
    TextView home_screen_connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // image button for the "up" and "down" button on the home screen
        plus_button = (ImageButton) findViewById(R.id.plusbutton);
        minus_button = (ImageButton) findViewById(R.id.minusbutton);

        // this is the address of the web service that (simulates) the thermostat
        HeatingSystem.BASE_ADDRESS = "http://wwwis.win.tue.nl/2id40-ws/004";

        current_temp = (TextView) findViewById(R.id.current_temp);
        desired_temp = (TextView) findViewById(R.id.target_temp);

        // the beautiful circular seekbar on the homescreen to change the desired temperature
        temp_seekbar = (CircularSeekBar) findViewById(R.id.temp_seekbar);

        day_temp_home = (TextView) findViewById(R.id.day_temp_home);
        night_temp_home = (TextView) findViewById(R.id.night_temp_home);

        // flame to notify the user when the temperature is increasing
        flame_drawable = (ImageView) findViewById(R.id.flame);

        // checkbox and texview for the weekprogram
        weekProgram = (CheckBox) findViewById(R.id.WeekProgram_checkbox);
        weekProgramState = (TextView) findViewById(R.id.text_checkbox_weekprogram);
        text_checkbox_state = (TextView) findViewById(R.id.text_checkbox_weekprogram);

        // button that opens a dialog box about the weekprogram
        info_button = (ImageView) findViewById(R.id.info_button);

        // buttons that go to the two other activities of the app
        change_button = (Button) findViewById(R.id.change_button);
        weekprogramButton = (Button) findViewById(R.id.week_program_button);

        // textveiw and two images that notify the connectivity state of the app
        home_screen_connection = (TextView)findViewById(R.id.home_connection);
        connection_logo = (ImageView) findViewById(R.id.connection_logo);
        thermo_icon_home = (ImageView) findViewById(R.id.thermostat_icon_home);

        // This is so to notify the user that there is no internet connection and that the app can therefore not be used
        if(!checkConnectivity()) {
            Toast disconnected = Toast.makeText(getApplicationContext(), "In order to use this app you need an internet connection", Toast.LENGTH_LONG);
            disconnected.show(); // notify the user that you need an internet connection to use the app

            // update the connectivity state on the homescreen (thought text, colours and disconnect symbol)
            home_screen_connection.setText("Thermostat is disconnected");
            home_screen_connection.setTextColor(Color.rgb(198,0,0)); // make the text red
            connection_logo.setImageResource(R.drawable.ic_sync_disabled_black_24dp); // disconnect symbol

            plus_button.setClickable(false); // make the plus botton unclickable
            plus_button.setImageResource(R.drawable.up_fill96_grey); // make the button grey
            minus_button.setClickable(false); // idem dito fot the minus button
            minus_button.setImageResource(R.drawable.down_fill96_grey);
            change_button.setClickable(false); // idem dito for the change button that is an intent to anothe actiivy
            change_button.getBackground().setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY);
            weekprogramButton.setClickable(false); // idem for this button
            weekprogramButton.getBackground().setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY);
            weekProgram.setClickable(false); // idem for this button
            weekProgram.getBackground().setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY);


            // wait a bit so that the user can see the interface and can read the previous toast message
            new CountDownTimer(6000, 1000) {
                public void onTick(long millisUntilFinished) {
                }
                public void onFinish() {
                    Toast quitprogram = Toast.makeText(getApplicationContext(), "Application is quitted. Restart when internet connection is (re)established", Toast.LENGTH_LONG);
                    quitprogram.show(); // show the quitted program toastmessage
                    finish(); // close application
                }
            }.start();

        }

        // Thread to get the values of our heatingsystem for night / day temperatures
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    day_temp = Double.parseDouble(HeatingSystem.get("dayTemperature")); // get the day temperature
                    night_temp = Double.parseDouble(HeatingSystem.get("nightTemperature")); // get the night temperature
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            day_temp_home.setText(day_temp + " \u2103"); // \u2103 is the code for the celsius degree symbol
                            night_temp_home.setText(night_temp + " \u2103");
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Update target temperature
                    des_temp = Double.parseDouble(HeatingSystem.get("currentTemperature")); // set des temp to current temperature
                    DesiredTempUpdate(); // update the desired temperature according
                    // get the day and night temperatures of the server
                    day_temp = Double.parseDouble(HeatingSystem.get("dayTemperature"));
                    night_temp = Double.parseDouble(HeatingSystem.get("nightTemperature"));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() { // update the textview of the day and night temperature
                            day_temp_home.setText(day_temp + " \u2103"); // \u2103 is the code for the celsius degree symbol
                            night_temp_home.setText(night_temp + " \u2103");
                        }
                    });

                    /* setImageAlpha gives us the option to change the opacity of an image
                           if we set it to 0, the image is not visible. if we set it to 255 the image
                           is viewable as it should be, we set it to 40 so that you can see that image
                           of the flame with an high opacity. If the temperatue is then increased we set it to 255
                           (burning flame is fully viewable) so that the users knows that the temperature is increasing
                         */
                    if (Double.parseDouble(HeatingSystem.get("currentTemperature")) < Double.parseDouble(HeatingSystem.get("targetTemperature"))) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                flame_drawable.setImageAlpha(255); // image of the flame fully visible (minumum opacity)
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                flame_drawable.setImageAlpha(40); // image of the flame partially visible (high opacity)
                            }
                        });
                    }
                    // We cehck or uncheck the checkbox of the week program mode on the home screen
                    if (HeatingSystem.get("weekProgramState").equals("off")) { // check if the weekprogram is disabled on  the server
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                weekProgram.setChecked(false);
                                weekProgramState.setText("Week program is enabled."); // update the textview that notifies the user
                            }
                        });

                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                weekProgram.setChecked(true);
                                weekProgramState.setText("Week program is disabled."); // update the textview that notifies the user
                            }
                        });

                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            current_temp.setText(des_temp + " \u2103"); // \u2103 is the code for the celsius degree symbol
                            temp_seekbar.setProgress((int) (des_temp * 10 - 50)); // update the seekbar
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();

        new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        curr_temp = Double.parseDouble(HeatingSystem.get("currentTemperature")); // get the current temperature of the server
                        Double compare_des_temp = Double.parseDouble(HeatingSystem.get("targettemperature")); // check if desired temperatue is the same on the server
                        weekProgramEnabled = (HeatingSystem.getVacationMode()); // get the mode of the weekprogram of the server

                        // if the two temperatuee are not the same, we update the desired temperature
                        if (compare_des_temp != des_temp) {
                            des_temp = compare_des_temp; // update desired temperature
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    temp_seekbar.setProgress((int) (des_temp * 10 - 50)); // make sure it is visible
                                }
                            });
                        }
                        DesiredTempUpdate(); // update the deisred temperatuee

                        /* setImageAlpha gives us the option to change the opacity of an image
                           if we set it to 0, the image is not visible. if we set it to 255 the image
                           is viewable as it should be, we set it to 40 so that you can see that image
                           of the flame with an high opacity. If the temperatue is then increased we set it to 255
                           (burning flame is fully viewable) so that the users knows that the temperature is increasing
                         */
                        if (Double.parseDouble(HeatingSystem.get("currentTemperature")) < Double.parseDouble(HeatingSystem.get("targetTemperature"))) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    flame_drawable.setImageAlpha(255); // set the opacity to minumum when the current temperature is increasing
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    flame_drawable.setImageAlpha(40); // set the opacity to high if the current temperature is not increasing
                                }
                            });
                        }
                        // check if we should check or uncheck the checkbox of the weekprogram
                        if (permanent_wp_mode) {
                            if (HeatingSystem.get("weekProgramState").equals("off")) { // check it on the server
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        weekProgram.setChecked(false); // uncheck checkbox of the weekprogram
                                        text_checkbox_state.setText("Week program is disabled."); // update the textview
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        weekProgram.setChecked(true); // check the checkbox of the weekprogram
                                        text_checkbox_state.setText("Week program is enabled."); // update the textview
                                    }
                                });
                            }
                        } else {
                            permanent_wp_mode = true;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                current_temp.setText("" + curr_temp + " \u2103");
                            }
                        }); // \u2103 is the code for the celsius degree symbol
                    }
                } catch (InterruptedException e) {
                    System.err.println("Error from getdata " + e);
                    e.printStackTrace();
                } catch (ConnectException e) {
                    System.err.println("Error from getdata " + e);
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }.start();



        // Listener for the info image  button on the homescreen for the weekprogram (state) info
        info_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(v.getContext(), R.style.AppCompatAlertDialogStyle); // style for the dialog
                builder.setTitle("Week Program Info"); // title of the dialog
                builder.setMessage(R.string.info_home_weekprogram); // the info text is under the string file (find the string by id)
                builder.setPositiveButton("Go Back", null); // button that goes back to the activity
                builder.show(); // show the dialog that has been built

            }
        });



        // Listener for the "up" button that increases the desired temperature
        plus_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//increase temperature via button
                if (des_temp <= 30) { // maximum temperature is 5 degree celsius
                    des_temp = des_temp + 0.1; // increase desired temperature by 0.1
                    flame_drawable.setImageAlpha(255); // notify the user that the current temperature is increasing (burning flame image)
                    DesiredTempUpdate(); // update the desired temperature
                    temp_seekbar.setProgress((int) (des_temp * 10 - 50)); // make sure that the seekbar moves along with the changes
                    max_and_min(); // check the temperature limits et cetera
                }
                SendWebCurrTemperature(); // update the current temperature on the server to the changes
            }
        });

        // Listener for the "down" button that decreases the desired temperature
        minus_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (des_temp >= 5) { // minimum temperature is 5 degree celsius
                    des_temp = des_temp - 0.1; // decrease desired temperature by 0.1
                    DesiredTempUpdate(); // update the desired temperature
                    temp_seekbar.setProgress((int) (des_temp * 10 - 50)); // make sure that the seekbar moves along with the changes
                    max_and_min(); // check the temperature limits et cetera
                }
                SendWebCurrTemperature(); // update the current temperature on the server to the changes
            }
        });


        // intent to new activity (weekOverview activity) + listener for weekOverview button on home sreen
        weekprogramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToWeekProgram = new Intent(v.getContext(), WeekOverview.class);
                startActivity(goToWeekProgram);
            }
        });

        // Listener for the checkbutton for the weekprogram
        weekProgram.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                permanent_wp_mode = false;
                if (!isChecked) { // check if it is not checked
                    weekProgramState_Disable(); // if not, weekprogram should be diabled

                } else {
                    weekProgramState_Enable(); // if it is, weekprogram should be enabled
                }
            }
        });


        // intent to new activity (change day/night temperature acitvity) + listener for change button on home sreen
        change_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), DayNight_Temp_Change.class));
            }
        });

        // Listener for the circular seekbar for the changing of the desired temperature
        temp_seekbar.setOnSeekBarChangeListener(new com.thermostatmobileak.android.mobilethermostat.CircleSeekBarListener(){
        @Override
        public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
            des_temp = (progress + 50) / 10.0;
            DesiredTempUpdate(); // update desire temperature
            max_and_min();  // check if we are at the limits et cetera
            SendWebCurrTemperature(); // update the current temperature on the web server
            if (des_temp > curr_temp) {
                flame_drawable.setImageAlpha(255); // max is 255 (image is not transparent at all), min is 0 (image is hidden)
            } // if the the seekbar increases the temperature, make sure the user gets notified by the flame burning image

        }  // we need to override this method, even though we do not use it
            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {
            }

            // we need to override this method, even though we do not use it
            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
            }
        });

    }

    // make use of the weekprogram in the thermostat
    void weekProgramState_Enable() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HeatingSystem.put("weekProgramState", "on"); // update the status of the weekprogram on the web service
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            text_checkbox_state.setText("Week program is enabled."); // update the textview that notifies the user
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Do not make use of the weekprogram in the thermostat (desired temperature is permanent)
    void weekProgramState_Disable() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HeatingSystem.put("weekProgramState", "off"); // update the status of the weekprogram on the web service
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            text_checkbox_state.setText("Week program is disabled."); // update the textview that notifies the user
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                    e.printStackTrace();
                }
            }
        }).start();
    }



    // the current temperature of the thermostat becomes the desired temperature (seekbar or minus/plus button changes this)
    void SendWebCurrTemperature() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HeatingSystem.put("currentTemperature", "" + des_temp); // update the current temperature to the (new) desired temperature
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /* Updating the desired (target) temperature */
    void DesiredTempUpdate() {
        des_temp = Math.round(des_temp * 10);
        des_temp = des_temp / 10;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                desired_temp.setText(des_temp + "");
            }
        }); // update the textview for the desired temperature
    }


    // graying out buttons/making them unclickable and vice versa when max and min temperatures are reached
    public void max_and_min() {
            if (des_temp == 30) { // temperature is 30 degrees (maximum), plus button is unclickable and greyed out
                plus_button.setClickable(false);
                plus_button.setImageResource(R.drawable.up_fill96_grey); // same image, but then grey
                minus_button.setClickable(true);
            } else if (des_temp == 5) { // temperature is  degrees (minimum), minus button is unclickable and greyed out
                minus_button.setClickable(false);
                minus_button.setImageResource(R.drawable.down_fill96_grey); // same image, but then grey
                plus_button.setClickable(true);
            } else { // temperature is neither 5 or 30 degrees, so the buttons are clickable and not greyed out
                minus_button.setClickable(true);
                minus_button.setImageResource(R.drawable.down_fill96); // normal image for the image-button for minus button
                plus_button.setClickable(true);
                plus_button.setImageResource(R.drawable.up_fill96); // normal image for the image-button for the plus button
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
                            day_temp_home.setText(day_temp + " \u2103"); // \u2103 is the code for the celsius degree symbol
                            night_temp_home.setText(night_temp + " \u2103"); // \u2103 is the code for the celsius degree symbol
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();
    }

    /* Check if user is connected to a network */
    public boolean checkConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


    // method for the three dots menu in the app activity bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // method that handles the action in the three dots menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // setting up the dialog that pops up when you click on info in the menu
        if (id == R.id.action_info) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle("Homescreen Info"); // title of the dialog box
            builder.setMessage(R.string.info_home); // text in the dialog box
            builder.setPositiveButton("Go Back", null); // button that goes back to the actiivty
            builder.show();
        }
        return super.onOptionsItemSelected(item);
    }



}
