package com.thermostatmobileak.android.mobilethermostat;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.thermostatapp.util.HeatingSystem;
import org.thermostatapp.util.Switch;
import org.thermostatapp.util.WeekProgram;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Saturday.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class Monday extends Fragment {

    // Declare arraylists with switches for each day
    public ArrayList<Switch> mondaySwitches;

    // Declare week program
    WeekProgram wkProgram;
    // Declare layout
    Button bMondayAdd;
    Button bMondayRemoveAll;
    Button bMondayChange;
    TextView mondaySwitch1, mondaySwitch2, mondaySwitch3, mondaySwitch4, mondaySwitch5;
    ImageButton[] bMondayRemoveSwitches = new ImageButton[5]; // remove buttons
    TextView mondayDayTempText, mondayNightTempText, mondayDayTemp, mondayNightTemp;
    EditText mondayDaySwitchHrs, mondayDaySwitchMins, mondayNightSwitchHrs, mondayNightSwitchMins;
    TextView mondayTitle;
    static TextView dayTimeText;
    static TextView nightTimeText;
    // Declare day and night temperature
    double dayTemp;
    double nightTemp;
    // Declare day and daynumber to be initialized in subclasses
    String day;
    int dayNumber;
    // Some local variables that didn't want to be local without being final (inner class)
    int j;
    int remove1;
    int remove2;
    // Switch vars
    static String daySwitchTime;
    static String nightSwitchTime;
    static Boolean isDay;
    static String[] times; // 0: day hour, 1: day minute, 2: night hour, 3: night minute
    static int[] input; // 0: day hour, 1: day minute, 2: night hour, 3: night minute

    boolean allowed = true;
    ImageButton mon_day_edit, mon_night_edit;
    TextView mon_day_time, mon_night_time;

    private OnFragmentInteractionListener mListener;

    public Monday() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_monday, container, false);
        super.onCreate(savedInstanceState);

        dayTimeText = (TextView) view.findViewById(R.id.mon_day_time);
        nightTimeText = (TextView) view.findViewById(R.id.mon_night_time);

        times = new String[4];

        input = new int[]{0, 0, 0, 0};
        isDay = true;
        displayInput(input[0], input[1]);
        isDay = false;
        displayInput(input[2], input[3]);

        HeatingSystem.BASE_ADDRESS = "http://wwwis.win.tue.nl/2id40-ws/004";
        HeatingSystem.WEEK_PROGRAM_ADDRESS = "http://wwwis.win.tue.nl/2id40-ws/004/weekprogram";

        day = "Monday";
        dayNumber = 1;



        bMondayAdd = (Button) view.findViewById(R.id.mon_add);
        bMondayRemoveAll = (Button)view.findViewById(R.id.mon_remove_all);
        bMondayChange = (Button) view.findViewById(R.id.mon_change_button);
        mondayDayTempText = (TextView)view.findViewById(R.id.mon_day_temp);
        mondayNightTempText = (TextView)view.findViewById(R.id.mon_night_temp);
        // mondayTitle = (TextView)getView.findViewById(R.id.mondayTitle);

        // title = (TextView)findViewById(R.id.mondayTitle);
        mondaySwitch1 = (TextView)view.findViewById(R.id.mon_switch1);
        mondaySwitch2 = (TextView)view.findViewById(R.id.mon_switch2);
        mondaySwitch3 = (TextView)view.findViewById(R.id.mon_switch3);
        mondaySwitch4 = (TextView)view.findViewById(R.id.mon_switch4);
        mondaySwitch5 = (TextView)view.findViewById(R.id.mon_switch5);
        bMondayRemoveSwitches[0] = (ImageButton)view.findViewById(R.id.mon_trash1);
        bMondayRemoveSwitches[1] = (ImageButton)view.findViewById(R.id.mon_trash2);
        bMondayRemoveSwitches[2] = (ImageButton)view.findViewById(R.id.mon_trash3);
        bMondayRemoveSwitches[3] = (ImageButton)view.findViewById(R.id.mon_trash4);
        bMondayRemoveSwitches[4] = (ImageButton)view.findViewById(R.id.mon_trash5);

        mondayDayTemp = (TextView)view.findViewById(R.id.mon_day_temp);
        mondayNightTemp = (TextView)view.findViewById(R.id.mon_night_temp);

        mon_day_edit = (ImageButton) view.findViewById(R.id.mon_day_edit);
        mon_day_time = (TextView) view.findViewById(R.id.mon_day_time);

        mon_night_edit = (ImageButton) view.findViewById(R.id.mon_night_edit);
        mon_night_time = (TextView) view.findViewById(R.id.mon_night_time);



        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dayTemp = Double.parseDouble(HeatingSystem.get("dayTemperature"));
                    nightTemp = Double.parseDouble(HeatingSystem.get("nightTemperature"));
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mondayDayTemp.setText("Day temperature: " + dayTemp + " \u2103");
                            mondayNightTemp.setText("Night temperature: " + nightTemp +  " \u2103");
                        }
                    });
                    wkProgram = HeatingSystem.getWeekProgram();
                    mondaySwitches = wkProgram.getDay("Monday");
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
                        Thread.sleep(500);
                        wkProgram = HeatingSystem.getWeekProgram();
                        mondaySwitches = wkProgram.getDay("Monday");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                displaySwitches();
                            }
                        });
                    }
                } catch (InterruptedException e){

                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }.start();

        /* Go to activity to change day & night temperature when Change button is clicked */
        bMondayChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), DayNight_Temp_Change.class));
            }
        });

        /* Set on click listener for Add button */
        bMondayAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Make sure all input fields have 2 digit format even if user inputs fewer digits
                for (int i = 0; i < times.length; i++) {
                    if (times[i].length() == 0) {
                        times[i] = "00";
                    } else if (times[i].length() == 1) {
                        times[i] = "0" + times[i];
                    }
                }
                // Make sure new switch has acceptable switch times
                int newDay = Integer.parseInt(times[0] + times[1], 10);
                int newNight = Integer.parseInt(times[2] + times[3], 10);
                for(int i = 0; i<5; i++) {
                    if(wkProgram.data.get(day).get(2*i).getState()) {
                        // Convert active switch's time to int
                        String tmpd = wkProgram.data.get(day).get(2*i).getTime();
                        String tmpd2 = tmpd.substring(0,2) + tmpd.substring(3,5);
                        String tmpn = wkProgram.data.get(day).get(2*i + 1).getTime();
                        String tmpn2 = tmpn.substring(0,2) + tmpn.substring(3,5);
                        int oldDay = Integer.parseInt(tmpd2, 10);
                        int oldNight = Integer.parseInt(tmpn2, 10);
                        // Check if the new switch does not overlap with active switch
                        if (newDay < oldDay && newNight < oldDay) {
                            // The new switch is before the active switch.
                        } else if (newDay > oldNight && newNight > oldNight) {
                            // The new switch is after the active switch.
                        } else {
                            // The new switch overlaps with the active switch.
                            allowed = false;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast overlap = Toast.makeText(getActivity().getApplicationContext(), "The switch was not added: a new switch can't overlap with an already active switch.", Toast.LENGTH_LONG);
                                    overlap.show();
                                }
                            });
                        }
                    }
                }
                // Check if the new night switch is after the new day switch
                if (!(newDay < newNight)) {
                    allowed = false;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast night = Toast.makeText(getActivity().getApplicationContext(), "The switch was not added: the night switch must be later than the day switch.", Toast.LENGTH_LONG);
                            night.show();
                        }
                    });
                }
                // Concatenate strings to create a hh:mm format
                daySwitchTime = times[0] + ":" + times[1];
                nightSwitchTime = times[2] + ":" + times[3];
                //add switches
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(allowed) {
                                setSwitch(day, daySwitchTime, nightSwitchTime);
                            } allowed = true;
                            HeatingSystem.setWeekProgram(wkProgram);
                            wkProgram = HeatingSystem.getWeekProgram();
                            mondaySwitches = wkProgram.getDay("Monday");
                            // Display switches again
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    displaySwitches();
                                }
                            });
                        } catch (Exception e) {
                            System.out.println("Error in getdata: " + e);
                        }
                    }
                }).start();
            }
        });

        /* Set on click listener for Remove All button */
        bMondayRemoveAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //Remove all switches from wkProgram
                    for (int i = 0; i < 5; i++){
                        wkProgram.data.get(day).set(2*i, new Switch("day", false, "23:59"));
                        wkProgram.data.get(day).set(2 * i + 1, new Switch("night", false, "23:59"));
                    }
                    displaySwitches();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                HeatingSystem.setWeekProgram(wkProgram);
                            } catch (Exception e) {
                                System.out.println("Error in getdata: " + e);
                            }
                        }
                    }).start();
                } catch (Exception e) {
                    System.err.println("Error from getdata 2 " + e);
                }
            }
        });

        /* Set on click listeners for all remove buttons */
        for(int i = 0; i < 5; i++){
            bMondayRemoveSwitches[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /* Check which button was pressed */
                    ImageButton clickedButton = (ImageButton) v;
                    switch (clickedButton.getId()) {
                        case R.id.mon_trash1:
                            j = 0;
                            break;
                        case R.id.mon_trash2:
                            j = 1;
                            break;
                        case R.id.mon_trash3:
                            j = 2;
                            break;
                        case R.id.mon_trash4:
                            j = 3;
                            break;
                        case R.id.mon_trash5:
                            j=4;
                            break;
                    }
                    remove1 = 2*j;
                    remove2 = (2*j)+1;
                    // Remove displayed text & icon for said switch
                    try {
                        bMondayRemoveSwitches[j].setVisibility(View.INVISIBLE);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                displaySwitches();
                            }
                        });
                    } catch (Exception e) {
                        System.err.println("Error from getdata " + e);
                    }
                    // Remove switch from server and upload week program to server
                    // todo: fix this. It doesn't always remove the correct switches...
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                wkProgram.RemoveSwitch(remove1, day);
                                HeatingSystem.setWeekProgram(wkProgram);
                                wkProgram = HeatingSystem.getWeekProgram();
                                mondaySwitches = wkProgram.getDay("Monday");
                                // Display switches again (doesn't really work yet)
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        displaySwitches();
                                    }
                                });
                            } catch (Exception e) {
                                System.out.println("Error in getdata: " + e);
                            }
                        }
                    }).start();
                }
            });
        }

        mon_day_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getActivity().getFragmentManager(), "timePicker");

                isDay = true;
            }
        });

        mon_day_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getActivity().getFragmentManager(), "timePicker");
                isDay = true;
            }

        });

        mon_night_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getActivity().getFragmentManager(), "timePicker");

                isDay = false;
            }
        });

        mon_night_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getActivity().getFragmentManager(), "timePicker");
                isDay = false;
            }

        });

        return view;
    }

    /* Reload day & night temperature when the day activity is revisited */
    public void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dayTemp = Double.parseDouble(HeatingSystem.get("dayTemperature"));
                    nightTemp = Double.parseDouble(HeatingSystem.get("nightTemperature"));
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mondayDayTemp.setText(dayTemp + " \u2103");
                            mondayNightTemp.setText(nightTemp + " \u2103");
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getActivity().getFragmentManager(), "timePicker");

        if(v.getId() == R.id.mon_day_edit || v.getId() == R.id.mon_day_time){
            isDay = true;
        } else {
            isDay = false;
        }
    }

    static void displayInput(int hourOfDay, int minute){
        String[] hourMinuteAmPm = int24HrsTo12HrsStr(hourOfDay, minute, false);

        if(isDay){
            times[0] = hourMinuteAmPm[0];
            times[1] = hourMinuteAmPm[1];
        } else {
            times[2] = hourMinuteAmPm[0];
            times[3] = hourMinuteAmPm[1];
        }

        hourMinuteAmPm = int24HrsTo12HrsStr(hourOfDay, minute, true);

        if(isDay){
            dayTimeText.setText(hourMinuteAmPm[0]+":"+hourMinuteAmPm[1]+" "+hourMinuteAmPm[2]);
        } else {
            nightTimeText.setText(hourMinuteAmPm[0]+":"+hourMinuteAmPm[1]+" "+hourMinuteAmPm[2]);
        }

        /*} else {
            if ((hourOfDay < 10) && ((Integer.parseInt(minuteStr) < 10))) {
                dayTimeText.setText("0"+hourOfDay+":"+"0"+minuteStr+" "+amPm);
            } else if ((hourOfDay < 10) && !((Integer.parseInt(minuteStr) < 10))) {
                dayTimeText.setText("0"+hourOfDay+":"+minuteStr+" "+amPm);
            } else if (!(hourOfDay < 10) && ((Integer.parseInt(minuteStr) < 10))) {
                dayTimeText.setText(hourOfDay+":"+"0"+minuteStr+" "+amPm);
            } else {
                dayTimeText.setText(hourOfDay+":"+minuteStr+" "+amPm);
            }

        }*/
        // Inflate the layout for this fragment
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    void displaySwitches(){
        if (mondaySwitches.get(0).getState()) {
            String hourMinuteDay = mondaySwitches.get(0).getTime();
            int[] intHourMinuteDay = Hrs24StrToInt(hourMinuteDay);
            String[] strArrHourMinuteDay = int24HrsTo12HrsStr(intHourMinuteDay[0], intHourMinuteDay[1], true);
            String hourMinuteNight = mondaySwitches.get(1).getTime();
            int[] intHourMinuteNight = Hrs24StrToInt(hourMinuteNight);
            String[] strArrHourMinuteNight = int24HrsTo12HrsStr(intHourMinuteNight[0], intHourMinuteNight[1], true);
            mondaySwitch1.setText("1) " + mondaySwitches.get(0).getType() + ": " + strArrHourMinuteDay[0]+":"+strArrHourMinuteDay[1]+" "+strArrHourMinuteDay[2] + ", " + mondaySwitches.get(1).getType() + ": "
                    + strArrHourMinuteNight[0]+":"+strArrHourMinuteNight[1]+" "+strArrHourMinuteNight[2] + " ");
            bMondayRemoveSwitches[0].setVisibility(View.VISIBLE); // makes button appear
        } else {
            mondaySwitch1.setText("");
            bMondayRemoveSwitches[0].setVisibility(View.INVISIBLE); // makes button appear
        }
        if (mondaySwitches.get(2).getState()) {
            String hourMinuteDay = mondaySwitches.get(2).getTime();
            int[] intHourMinuteDay = Hrs24StrToInt(hourMinuteDay);
            String[] strArrHourMinuteDay = int24HrsTo12HrsStr(intHourMinuteDay[0], intHourMinuteDay[1], true);
            String hourMinuteNight = mondaySwitches.get(3).getTime();
            int[] intHourMinuteNight = Hrs24StrToInt(hourMinuteNight);
            String[] strArrHourMinuteNight = int24HrsTo12HrsStr(intHourMinuteNight[0], intHourMinuteNight[1], true);
            mondaySwitch2.setText("2) " + mondaySwitches.get(2).getType() + ": " + strArrHourMinuteDay[0]+":"+strArrHourMinuteDay[1]+" "+strArrHourMinuteDay[2] + ", " + mondaySwitches.get(3).getType() + ": "
                    + strArrHourMinuteNight[0]+":"+strArrHourMinuteNight[1]+" "+strArrHourMinuteNight[2] + " ");
            bMondayRemoveSwitches[1].setVisibility(View.VISIBLE); // makes button appear
        } else {
            mondaySwitch2.setText("");
            bMondayRemoveSwitches[1].setVisibility(View.INVISIBLE); // makes button appear
        }
        if (mondaySwitches.get(4).getState()) {
            String hourMinuteDay = mondaySwitches.get(4).getTime();
            int[] intHourMinuteDay = Hrs24StrToInt(hourMinuteDay);
            String[] strArrHourMinuteDay = int24HrsTo12HrsStr(intHourMinuteDay[0], intHourMinuteDay[1], true);
            String hourMinuteNight = mondaySwitches.get(5).getTime();
            int[] intHourMinuteNight = Hrs24StrToInt(hourMinuteNight);
            String[] strArrHourMinuteNight = int24HrsTo12HrsStr(intHourMinuteNight[0], intHourMinuteNight[1], true);
            mondaySwitch3.setText("3) " + mondaySwitches.get(4).getType() + ": " + strArrHourMinuteDay[0]+":"+strArrHourMinuteDay[1]+" "+strArrHourMinuteDay[2] + ", " + mondaySwitches.get(5).getType() + ": "
                    + strArrHourMinuteNight[0]+":"+strArrHourMinuteNight[1]+" "+strArrHourMinuteNight[2] + " ");
            bMondayRemoveSwitches[2].setVisibility(View.VISIBLE); // makes button appear
        } else {
            mondaySwitch3.setText("");
            bMondayRemoveSwitches[2].setVisibility(View.INVISIBLE); // makes button appear
        }
        if (mondaySwitches.get(6).getState()) {
            String hourMinuteDay = mondaySwitches.get(6).getTime();
            int[] intHourMinuteDay = Hrs24StrToInt(hourMinuteDay);
            String[] strArrHourMinuteDay = int24HrsTo12HrsStr(intHourMinuteDay[0], intHourMinuteDay[1], true);
            String hourMinuteNight = mondaySwitches.get(7).getTime();
            int[] intHourMinuteNight = Hrs24StrToInt(hourMinuteNight);
            String[] strArrHourMinuteNight = int24HrsTo12HrsStr(intHourMinuteNight[0], intHourMinuteNight[1], true);
            mondaySwitch4.setText("4) " + mondaySwitches.get(6).getType() + ": " + strArrHourMinuteDay[0]+":"+strArrHourMinuteDay[1]+" "+strArrHourMinuteDay[2] + ", " + mondaySwitches.get(7).getType() + ": "
                    + strArrHourMinuteNight[0]+":"+strArrHourMinuteNight[1]+" "+strArrHourMinuteNight[2] + " ");
            bMondayRemoveSwitches[3].setVisibility(View.VISIBLE); // makes button appear
        } else {
            mondaySwitch4.setText("");
            bMondayRemoveSwitches[3].setVisibility(View.INVISIBLE); // makes button appear
        }
        if (mondaySwitches.get(8).getState()) {
            String hourMinuteDay = mondaySwitches.get(8).getTime();
            int[] intHourMinuteDay = Hrs24StrToInt(hourMinuteDay);
            String[] strArrHourMinuteDay = int24HrsTo12HrsStr(intHourMinuteDay[0], intHourMinuteDay[1], true);
            String hourMinuteNight = mondaySwitches.get(9).getTime();
            int[] intHourMinuteNight = Hrs24StrToInt(hourMinuteNight);
            String[] strArrHourMinuteNight = int24HrsTo12HrsStr(intHourMinuteNight[0], intHourMinuteNight[1], true);
            mondaySwitch5.setText("5) " + mondaySwitches.get(8).getType() + ": " + strArrHourMinuteDay[0]+":"+strArrHourMinuteDay[1]+" "+strArrHourMinuteDay[2] + ", " + mondaySwitches.get(9).getType() + ": "
                    + strArrHourMinuteNight[0]+":"+strArrHourMinuteNight[1]+" "+strArrHourMinuteNight[2] + " ");
            bMondayRemoveSwitches[4].setVisibility(View.VISIBLE); // makes button appear
        } else {
            mondaySwitch5.setText("");
            bMondayRemoveSwitches[4].setVisibility(View.INVISIBLE); // makes button appear
        }
    }

    static String[] int24HrsTo12HrsStr(int hour, int minute, boolean min12) {
        String amPm = "AM";
        String minuteStr = String.valueOf(minute);

        if(minute == 0){
            minuteStr = "00";
        } else if(minute < 10){
            minuteStr = "0"+String.valueOf(minute);
        }

        if(min12){
            if(hour >= 13){
                hour -= 12;
                amPm = "PM";
            } else if(hour == 12){
                amPm = "PM";
            } else if(hour == 0){
                hour = 12;
            }
        }

        String hourStr = String.valueOf(hour);

        if(hour < 10){
            hourStr = "0"+String.valueOf(hour);
        }

        String[] hourMinuteAmPm = new String[]{hourStr, minuteStr, amPm};
        return hourMinuteAmPm;
    }

    int[] Hrs24StrToInt(String hourMinute) {
        String hour = hourMinute.substring(0,2);
        String minute = hourMinute.substring(3,5);

        int intHour = Integer.parseInt(hour);
        int intMinute = Integer.parseInt(minute);

        int[] hourMin = new int[]{intHour, intMinute};
        return hourMin;
    }

    /* Add a day and a night switch to the array for a specified day */
    public void setSwitch(String day, String dayTime, String nightTime) {
        // If 5 switches are already enabled, tell the user no more switches can be added.
        if (wkProgram.data.get(day).get(8).getState()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast maxSwitchesAdded = Toast.makeText(getActivity().getApplicationContext(), "The switch was not added: you can't add more than 5 switches.", Toast.LENGTH_LONG);
                    maxSwitchesAdded.show();
                }
            });
        }
        for (int i=0; i<5; i++) {
            // Find an OFF pair
            if (!wkProgram.data.get(day).get(2*i).getState()) {
                // Set the OFF pair to be the new switch.
                wkProgram.data.get(day).set(2*i, new Switch("day", true, dayTime));
                wkProgram.data.get(day).set(2*i + 1, new Switch("night", true, nightTime));
                // When the last switch has been added, tell the user no more switches can be added.
                if(i==4) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast maxSwitchesAdded = Toast.makeText(getActivity().getApplicationContext(), "You've now added the maximum of 5 switches.", Toast.LENGTH_LONG);
                            maxSwitchesAdded.show();
                        }
                    });
                }
                i=5;
            }
        }
    }


}
