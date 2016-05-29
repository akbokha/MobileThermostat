package com.thermostatmobileak.android.mobilethermostat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;


import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.thermostatapp.util.HeatingSystem;

import java.net.ConnectException;

import com.devadvance.circularseekbar.CircularSeekBar;
import com.devadvance.circularseekbar.CircularSeekBar.OnCircularSeekBarChangeListener;


public class MainActivity extends AppCompatActivity  {

    Button plus_button, minus_button, change_button, weekprogramButton;
    TextView current_temp, desired_temp;
    TextView day_temp_home, night_temp_home;
    double curr_temp, des_temp;
    double day_temp, night_temp;
    CircularSeekBar temp_seekbar;
    ImageView flame_drawable;
    CheckBox weekProgram;
    TextView weekProgramState, text_checkbox_state;
    boolean weekProgramEnabled, permanent_mode = true;
    ImageView info_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        plus_button = (Button) findViewById(R.id.plusbutton);
        minus_button = (Button) findViewById(R.id.minusbutton);

        HeatingSystem.BASE_ADDRESS = "http://pcwin889.win.tue.nl/2id40-ws/004";

        current_temp = (TextView) findViewById(R.id.current_temp);
        desired_temp = (TextView) findViewById(R.id.target_temp);
        temp_seekbar = (CircularSeekBar) findViewById(R.id.temp_seekbar);

        day_temp_home = (TextView) findViewById(R.id.day_temp_home);
        night_temp_home = (TextView) findViewById(R.id.night_temp_home);
        flame_drawable = (ImageView) findViewById(R.id.flame);

        weekProgram = (CheckBox) findViewById(R.id.WeekProgram_checkbox);
        weekProgramState = (TextView) findViewById(R.id.text_checkbox_weekprogram);
        text_checkbox_state = (TextView) findViewById(R.id.text_checkbox_weekprogram);

        info_button = (ImageView) findViewById(R.id.info_button);
        change_button = (Button) findViewById(R.id.change_button);
        weekprogramButton = (Button) findViewById(R.id.week_program_button);

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
                            day_temp_home.setText(day_temp + " \u2103");
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
                    des_temp = Double.parseDouble(HeatingSystem.get("currentTemperature")); // It's supposed to be getting currentTemperature
                    DesiredTempUpdate();
                    // Set day and night temperature
                    day_temp = Double.parseDouble(HeatingSystem.get("dayTemperature"));
                    night_temp = Double.parseDouble(HeatingSystem.get("nightTemperature"));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            day_temp_home.setText(day_temp + " \u2103");
                            night_temp_home.setText(night_temp + " \u2103");
                        }
                    });

                    // Set the flame visibility
                    if (Double.parseDouble(HeatingSystem.get("currentTemperature")) < Double.parseDouble(HeatingSystem.get("targetTemperature"))) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                flame_drawable.setImageAlpha(255);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                flame_drawable.setImageAlpha(40);
                            }
                        });
                    }
                    // Set the checkbox for Vacation Mode
                    if (HeatingSystem.get("weekProgramState").equals("off")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                weekProgram.setChecked(false);
                                weekProgramState.setText("Week program is enabled.");
                            }
                        });

                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                weekProgram.setChecked(true);
                                weekProgramState.setText("Week program is disabled.");
                            }
                        });

                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            current_temp.setText(des_temp + " \u2103");
                            temp_seekbar.setProgress((int) (des_temp * 10 - 50));
                        }
                    });
                    //System.out.println("vTemp1:" + HeatingSystem.get("currentTemperature"));
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
                        curr_temp = Double.parseDouble(HeatingSystem.get("currentTemperature"));
                        Double targetBuffer = Double.parseDouble(HeatingSystem.get("targettemperature"));
                        weekProgramEnabled = (HeatingSystem.getVacationMode());

                        if (targetBuffer != des_temp) {
                            des_temp = targetBuffer;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    temp_seekbar.setProgress((int) (des_temp * 10 - 50));
                                }
                            });
                        }
                        DesiredTempUpdate();
                        // Set the flame visibility
                        if (Double.parseDouble(HeatingSystem.get("currentTemperature")) < Double.parseDouble(HeatingSystem.get("targetTemperature"))) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    flame_drawable.setImageAlpha(255);
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    flame_drawable.setImageAlpha(40);
                                }
                            });
                        }
                        // Set the checkbox for Vacation Mode
                        if (permanent_mode) {
                            if (HeatingSystem.get("weekProgramState").equals("off")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        weekProgram.setChecked(false);
                                        text_checkbox_state.setText("Week program is disabled.");
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        weekProgram.setChecked(true);
                                        text_checkbox_state.setText("Week program is enabled.");
                                    }
                                });
                            }
                        } else {
                            permanent_mode = true;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                current_temp.setText("" + curr_temp + " \u2103");
                            }
                        });
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

        /* Set listeners for all buttons
        // Seekbar listener
        temp_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                des_temp = (progress + 50) / 10.0;
                DesiredTempUpdate();
                setInputLimits();
                putCurrentTemperature();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        }); */

        /*
        // Info button listener
        info_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        day_temp_home.setVisibility(View.INVISIBLE);
                        night_temp_home.setVisibility(View.INVISIBLE);
                        change_button.setVisibility(View.INVISIBLE);
                       // infoText.setVisibility(View.VISIBLE);
                       // close.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        // Close button listener
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        infoText.setVisibility(View.INVISIBLE);
                        close.setVisibility(View.INVISIBLE);
                        dayTempHome.setVisibility(View.VISIBLE);
                        nightTempHome.setVisibility(View.VISIBLE);
                        bChangeHome.setVisibility(View.VISIBLE);
                    }
                });
            }
        }); */

        // Increase temperature button listener
        plus_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//increase temperature via button
                if (des_temp <= 30) {
                    des_temp += 0.1;
                    DesiredTempUpdate();
                    temp_seekbar.setProgress((int) (des_temp * 10 - 50));
                    setInputLimits();
                }
                putCurrentTemperature();
            }
        });
        // Decrease temperature button listener
        minus_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//decrease temperature via button
                if (des_temp >= 5) {
                    des_temp -= 0.1;
                    DesiredTempUpdate();
                    temp_seekbar.setProgress((int) (des_temp * 10 - 50));
                    setInputLimits();
                }
                putCurrentTemperature();
            }
        });

        /*
        // Week overview button listener
        weekprogramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toWeekOverview = new Intent(v.getContext(), WeekOverview.class);
                startActivity(toWeekOverview);
            }
        }); */

        // Vacation mode checkbox listener
        weekProgram.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                permanent_mode = false;
                if (!isChecked) {
                    disableWeekProgram();

                } else {
                    enableWeekProgram();
                }
            }
        });


        // intent to new activity + listener for change button on home sreen
        change_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), DayNight_Temp_Change.class));
            }
        });

        temp_seekbar.setOnSeekBarChangeListener(new com.thermostatmobileak.android.mobilethermostat.CircleSeekBarListener(){
        @Override
        public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
            des_temp = (progress + 50) / 10.0;
            DesiredTempUpdate();
            setInputLimits();
            putCurrentTemperature();

        }
            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
            }
        });

    }

    /* Disabling the week program */
    void disableWeekProgram() {
        // System.out.println("Permanent mode: trying to set program to OFF");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HeatingSystem.put("weekProgramState", "off");
                    // System.out.println("Permanent mode: trying to set program to OFF");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            text_checkbox_state.setText("Week program is disabled.");
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /* Enabling the week program */
    void enableWeekProgram() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HeatingSystem.put("weekProgramState", "on");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            text_checkbox_state.setText("Week program is enabled.");
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /* Putting the target temperature */
    void putCurrentTemperature() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HeatingSystem.put("currentTemperature", "" + des_temp);
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
        });
    }


    /* Graying out buttons and re-enabling them */
    public void setInputLimits() {
        if (des_temp == 30) {
            plus_button.setClickable(false);
            plus_button.getBackground().setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY);
            minus_button.setClickable(true);
            minus_button.getBackground().setColorFilter(null);
        } else if (des_temp == 5) {
            minus_button.setClickable(false);
            minus_button.getBackground().setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY);
            plus_button.setClickable(true);
        } else {
            minus_button.setClickable(true);
            minus_button.getBackground().setColorFilter(null);
            plus_button.setClickable(true);
            plus_button.getBackground().setColorFilter(null);
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
                            day_temp_home.setText(day_temp + " \u2103");
                            night_temp_home.setText(night_temp + " \u2103");
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();
    }

    /* Check if user is connected to a network */
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
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
