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

    /* Arraylist for the switches for Monday and The initializations of the textviews for the
       switches in the layout for the monday switches */
    public ArrayList<Switch> mondaySwitches;
    TextView mon_switch1, mon_switch2, mon_switch3, mon_switch4, mon_switch5;

    // Textviews for displaying the day and night temperature in the weekday fragment
    TextView mon_dayTemp, mondayNightTemp;

    /* Buttons in the weekday fragment (one for adding switches, one to remove all switches
       and one that is an intent to the activity where you can change the day/night temperature*/
    Button mon_add, mon_removeAll, mon_changeDayNight;

    // Array for the imagebuttons (trashcans) to delete individual switches (contrary to removeAll)
    ImageButton[] mon_independentSwitches = new ImageButton[5];

    // Weekprogram, see package provided by the university (org.thermostatapp.util)
    WeekProgram weekProgram;

    // TextViews for the displaying the time of the day and night switches (when adding)
    static TextView dayswitch_time, nightswitch_time;

    // initializing the variables for the day and night temperature
    double dayTemp, nightTemp;

    // we will use this when making certain calls
    String day;

    // local variables to be used later on
    int j, delete_1, delete_2;

    // variables that we will use in the timepicker class for example
    static String switch_DayTime, switch_NightTime;
    static int[] input;
    /* entry 0: day hours
       entry 1: day minutes
       entry 2: night hours
       entry 4: night minutes*/
    static Boolean dayOrNot;
    static String[] times;
    /* entry 0: day hours
       entry 1: day minutes
       entry 2: night hours
       entry 4: night minutes*/

    boolean grant = true;

    // Imagebuttons (pencils) to edit the day and night time when adding switches
    ImageButton mon_day_edit, mon_night_edit;

    // Textviews to display day and night time when adding switches
    TextView mon_day_time, mon_night_time;

    private OnFragmentInteractionListener mListener;

    public Monday() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // makes life easier / neater when passing/ calling certain methods
        day = "Monday";

        HeatingSystem.BASE_ADDRESS = "http://wwwis.win.tue.nl/2id40-ws/004";
        HeatingSystem.WEEK_PROGRAM_ADDRESS = "http://wwwis.win.tue.nl/2id40-ws/004/weekprogram";

        /* See stackoverflo, otherwise will get errors when using findviewbyID
        since we are extending Fragment instead of AppCompatActivity for example*/
        View view = inflater.inflate(R.layout.fragment_monday, container, false);
        super.onCreate(savedInstanceState);

        input = new int[]{0, 0, 0, 0};
        dayOrNot = true;
        displayInput(input[0], input[1]);
        dayOrNot = false;
        displayInput(input[2], input[3]);
        times = new String[4];

        dayswitch_time = (TextView) view.findViewById(R.id.mon_day_time);
        nightswitch_time = (TextView) view.findViewById(R.id.mon_night_time);

        mon_switch1 = (TextView)view.findViewById(R.id.mon_switch1);
        mon_switch2 = (TextView)view.findViewById(R.id.mon_switch2);
        mon_switch3 = (TextView)view.findViewById(R.id.mon_switch3);
        mon_switch4 = (TextView)view.findViewById(R.id.mon_switch4);
        mon_switch5 = (TextView)view.findViewById(R.id.mon_switch5);
        mon_independentSwitches[0] = (ImageButton)view.findViewById(R.id.mon_trash1);
        mon_independentSwitches[1] = (ImageButton)view.findViewById(R.id.mon_trash2);
        mon_independentSwitches[2] = (ImageButton)view.findViewById(R.id.mon_trash3);
        mon_independentSwitches[3] = (ImageButton)view.findViewById(R.id.mon_trash4);
        mon_independentSwitches[4] = (ImageButton)view.findViewById(R.id.mon_trash5);

        mon_day_edit = (ImageButton) view.findViewById(R.id.mon_day_edit);
        mon_day_time = (TextView) view.findViewById(R.id.mon_day_time);
        mon_night_edit = (ImageButton) view.findViewById(R.id.mon_night_edit);
        mon_night_time = (TextView) view.findViewById(R.id.mon_night_time);

        mon_dayTemp = (TextView)view.findViewById(R.id.mon_day_temp);
        mondayNightTemp = (TextView)view.findViewById(R.id.mon_night_temp);

        mon_add = (Button) view.findViewById(R.id.mon_add);
        mon_removeAll = (Button)view.findViewById(R.id.mon_remove_all);
        mon_changeDayNight = (Button) view.findViewById(R.id.mon_change_button);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try { // Get the day and night temperature to display in the fragment
                    dayTemp = Double.parseDouble(HeatingSystem.get("dayTemperature"));
                    nightTemp = Double.parseDouble(HeatingSystem.get("nightTemperature"));
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {  // setting the day and nigh temperature in the fragment
                            mon_dayTemp.setText(dayTemp + " \u2103");
                            mondayNightTemp.setText(nightTemp +  " \u2103");
                        }
                    });
                    weekProgram = HeatingSystem.getWeekProgram();
                    mondaySwitches = weekProgram.getDay("Monday");
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
                        weekProgram = HeatingSystem.getWeekProgram();
                        mondaySwitches = weekProgram.getDay("Monday");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                displaySwitches(); // display current switches
                                // to do however: fix the little lag while loading
                            }
                        });
                    }
                } catch (InterruptedException e){

                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }.start();

        /* Intent to the activity where you can change the day and night temperature when clicking
           change button underneath the displaying of the day and night temperature
         */
        mon_changeDayNight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), DayNight_Temp_Change.class));
            }
        });

        /* Listener for the adding of the swithes (when clicking the add switch button +
          handling the errors with toast messages (e.g. overlap switches)
         */
        mon_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // always format of two digits
                for (int i = 0; i < times.length; i++) {
                    if (times[i].length() == 0) {
                        times[i] = "00";
                    } else if (times[i].length() == 1) {
                        times[i] = "0" + times[i];
                    }
                }
                // Check whether the inputted times (timepickerfragment) are acceptable in
                int input_day = Integer.parseInt(times[0] + times[1], 10);
                int input_night = Integer.parseInt(times[2] + times[3], 10);
                for(int i = 0; i<5; i++) {
                    if(weekProgram.data.get(day).get(2*i).getState()) {
                        // Convert active switch's time to int
                        String day_tmp = weekProgram.data.get(day).get(2*i).getTime();
                        String day_tmp2 = day_tmp.substring(0,2) + day_tmp.substring(3,5);
                        String night_tmp = weekProgram.data.get(day).get(2*i + 1).getTime();
                        String night_tmp2 = night_tmp.substring(0,2) + night_tmp.substring(3,5);
                        int day_old = Integer.parseInt(day_tmp2, 10);
                        int night_old = Integer.parseInt(night_tmp2, 10);
                        // Check wheter there is overlap in th input (timepickerfragment)
                        if (input_day < day_old && input_night < day_old) {
                            // the inputted switch comes before the current switch(es)
                        } else if (input_day > night_old && input_night > night_old) {
                            // the inputted switch comes after the current switch(es)
                        } else {
                            // not before or after: thus overlap with switch -> toastmessage
                            grant = false;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast overlap = Toast.makeText(getActivity().getApplicationContext(), "Switch is not added. Switch overlaps with current active switches. ", Toast.LENGTH_LONG);
                                    overlap.show();
                                }
                            });
                        }
                    }
                }
                /* Same as before, but now for the night input for the switch (check whether
                   the night switch input comes after the day input switch (required) */
                if (!(input_day < input_night)) {
                    grant = false;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast night = Toast.makeText(getActivity().getApplicationContext(), "Switch is not added. Please make sure that the night switch is later than the day switch.", Toast.LENGTH_LONG);
                            night.show();
                        }
                    });
                }

                // Adjust the string to the correct format hours:minutes (two digits:two digits)
                switch_DayTime = times[0] + ":" + times[1];
                switch_NightTime = times[2] + ":" + times[3];

                // Show the switches
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(grant) { // see org.thermostatapp.util for the method
                                setSwitch(day, switch_DayTime, switch_NightTime);
                            } grant = true;
                            HeatingSystem.setWeekProgram(weekProgram);
                            weekProgram = HeatingSystem.getWeekProgram();
                            mondaySwitches = weekProgram.getDay("Monday");
                            // re-display
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    displaySwitches(); // see org.thermostatapp.util for the method
                                }
                            });
                        } catch (Exception e) {
                            System.out.println("Error in getdata: " + e);
                        }
                    }
                }).start();
            }
        });

        /* Listener for the removal of all the swithes (when clicking the remove all button
         */
        mon_removeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //this deletes all the current switches (displayed) for this weekday
                    for (int i = 0; i < 5; i++){
                        weekProgram.data.get(day).set(2*i, new Switch("day", false, "23:59"));
                        weekProgram.data.get(day).set(2 * i + 1, new Switch("night", false, "23:59"));
                    }
                    displaySwitches(); // see org.thermostatapp.util for the method
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try { // see org.thermostatapp.util for the method
                                HeatingSystem.setWeekProgram(weekProgram);
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

        /* Listener for the removal of the swithes, but this time for the deleting the
          individual swithces (insted of all) (when clicking the trash image button next
          to the particular switch
         */
        for(int i = 0; i < 5; i++){
            mon_independentSwitches[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // check which imagenbutton it was
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
                    delete_1 = 2*j;
                    delete_2 = (2*j)+1;
                    // make the textview invisible when deleted
                    try {
                        mon_independentSwitches[j].setVisibility(View.INVISIBLE);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                displaySwitches(); // see org.thermostatapp.util for the method
                            }
                        });
                    } catch (Exception e) {
                        System.err.println("Error from getdata " + e);
                    }
                    // also delete it from the server + handling the weekprogram
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try { // see org.thermostatapp.util for the method
                                weekProgram.RemoveSwitch(delete_1, day);
                                HeatingSystem.setWeekProgram(weekProgram);
                                weekProgram = HeatingSystem.getWeekProgram();
                                mondaySwitches = weekProgram.getDay("Monday");
                                // Display switches again (doesn't really work yet)
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        displaySwitches(); // see org.thermostatapp.util for the method
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

        /* Listener for the editing the time of the day switch (clicking on the time textview)
          We make use of a time picker dialog fragment (android provided)
          to set the time (hours : minutes)
        */
        mon_day_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getActivity().getFragmentManager(), "timePicker");
                dayOrNot = true;
            }
        });

        /* Listener for the editing the time of the day switch (clicking on the edit imagebutton)
          We make use of a time picker dialog fragment (android provided)
          to set the time (hours : minutes)
        */
        mon_day_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getActivity().getFragmentManager(), "timePicker");
                dayOrNot = true;
            }

        });

        /* Listener for the editing the time of the night switch (clicking on the time textview)
          We make use of a time picker dialog fragment (android provided)
          to set the time (hours : minutes)
        */
        mon_night_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getActivity().getFragmentManager(), "timePicker");

                dayOrNot = false;
            }
        });

        /* Listener for the editing the time of the night switch (clicking on the edit imagebutton)
          We make use of a time picker dialog fragment (android provided)
          to set the time (hours : minutes)
        */
        mon_night_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getActivity().getFragmentManager(), "timePicker");
                dayOrNot = false;
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
                            mon_dayTemp.setText(dayTemp + " \u2103");
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
            dayOrNot = true;
        } else {
            dayOrNot = false;
        }
    }

    static void displayInput(int hourOfDay, int minute){
        String[] hourMinuteAmPm = int24HrsTo12HrsStr(hourOfDay, minute, false);

        if(dayOrNot){
            times[0] = hourMinuteAmPm[0];
            times[1] = hourMinuteAmPm[1];
        } else {
            times[2] = hourMinuteAmPm[0];
            times[3] = hourMinuteAmPm[1];
        }

        hourMinuteAmPm = int24HrsTo12HrsStr(hourOfDay, minute, true);

        if(dayOrNot){
            dayswitch_time.setText(hourMinuteAmPm[0]+":"+hourMinuteAmPm[1]+" "+hourMinuteAmPm[2]);
        } else {
            nightswitch_time.setText(hourMinuteAmPm[0]+":"+hourMinuteAmPm[1]+" "+hourMinuteAmPm[2]);
        }
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
            mon_switch1.setText("1) " + mondaySwitches.get(0).getType() + ": " + strArrHourMinuteDay[0]+":"+strArrHourMinuteDay[1]+" "+strArrHourMinuteDay[2] + ", " + mondaySwitches.get(1).getType() + ": "
                    + strArrHourMinuteNight[0]+":"+strArrHourMinuteNight[1]+" "+strArrHourMinuteNight[2] + " ");
            mon_independentSwitches[0].setVisibility(View.VISIBLE); // makes button appear
        } else {
            mon_switch1.setText("");
            mon_independentSwitches[0].setVisibility(View.INVISIBLE); // makes button appear
        }
        if (mondaySwitches.get(2).getState()) {
            String hourMinuteDay = mondaySwitches.get(2).getTime();
            int[] intHourMinuteDay = Hrs24StrToInt(hourMinuteDay);
            String[] strArrHourMinuteDay = int24HrsTo12HrsStr(intHourMinuteDay[0], intHourMinuteDay[1], true);
            String hourMinuteNight = mondaySwitches.get(3).getTime();
            int[] intHourMinuteNight = Hrs24StrToInt(hourMinuteNight);
            String[] strArrHourMinuteNight = int24HrsTo12HrsStr(intHourMinuteNight[0], intHourMinuteNight[1], true);
            mon_switch2.setText("2) " + mondaySwitches.get(2).getType() + ": " + strArrHourMinuteDay[0]+":"+strArrHourMinuteDay[1]+" "+strArrHourMinuteDay[2] + ", " + mondaySwitches.get(3).getType() + ": "
                    + strArrHourMinuteNight[0]+":"+strArrHourMinuteNight[1]+" "+strArrHourMinuteNight[2] + " ");
            mon_independentSwitches[1].setVisibility(View.VISIBLE); // makes button appear
        } else {
            mon_switch2.setText("");
            mon_independentSwitches[1].setVisibility(View.INVISIBLE); // makes button appear
        }
        if (mondaySwitches.get(4).getState()) {
            String hourMinuteDay = mondaySwitches.get(4).getTime();
            int[] intHourMinuteDay = Hrs24StrToInt(hourMinuteDay);
            String[] strArrHourMinuteDay = int24HrsTo12HrsStr(intHourMinuteDay[0], intHourMinuteDay[1], true);
            String hourMinuteNight = mondaySwitches.get(5).getTime();
            int[] intHourMinuteNight = Hrs24StrToInt(hourMinuteNight);
            String[] strArrHourMinuteNight = int24HrsTo12HrsStr(intHourMinuteNight[0], intHourMinuteNight[1], true);
            mon_switch3.setText("3) " + mondaySwitches.get(4).getType() + ": " + strArrHourMinuteDay[0]+":"+strArrHourMinuteDay[1]+" "+strArrHourMinuteDay[2] + ", " + mondaySwitches.get(5).getType() + ": "
                    + strArrHourMinuteNight[0]+":"+strArrHourMinuteNight[1]+" "+strArrHourMinuteNight[2] + " ");
            mon_independentSwitches[2].setVisibility(View.VISIBLE); // makes button appear
        } else {
            mon_switch3.setText("");
            mon_independentSwitches[2].setVisibility(View.INVISIBLE); // makes button appear
        }
        if (mondaySwitches.get(6).getState()) {
            String hourMinuteDay = mondaySwitches.get(6).getTime();
            int[] intHourMinuteDay = Hrs24StrToInt(hourMinuteDay);
            String[] strArrHourMinuteDay = int24HrsTo12HrsStr(intHourMinuteDay[0], intHourMinuteDay[1], true);
            String hourMinuteNight = mondaySwitches.get(7).getTime();
            int[] intHourMinuteNight = Hrs24StrToInt(hourMinuteNight);
            String[] strArrHourMinuteNight = int24HrsTo12HrsStr(intHourMinuteNight[0], intHourMinuteNight[1], true);
            mon_switch4.setText("4) " + mondaySwitches.get(6).getType() + ": " + strArrHourMinuteDay[0]+":"+strArrHourMinuteDay[1]+" "+strArrHourMinuteDay[2] + ", " + mondaySwitches.get(7).getType() + ": "
                    + strArrHourMinuteNight[0]+":"+strArrHourMinuteNight[1]+" "+strArrHourMinuteNight[2] + " ");
            mon_independentSwitches[3].setVisibility(View.VISIBLE); // makes button appear
        } else {
            mon_switch4.setText("");
            mon_independentSwitches[3].setVisibility(View.INVISIBLE); // makes button appear
        }
        if (mondaySwitches.get(8).getState()) {
            String hourMinuteDay = mondaySwitches.get(8).getTime();
            int[] intHourMinuteDay = Hrs24StrToInt(hourMinuteDay);
            String[] strArrHourMinuteDay = int24HrsTo12HrsStr(intHourMinuteDay[0], intHourMinuteDay[1], true);
            String hourMinuteNight = mondaySwitches.get(9).getTime();
            int[] intHourMinuteNight = Hrs24StrToInt(hourMinuteNight);
            String[] strArrHourMinuteNight = int24HrsTo12HrsStr(intHourMinuteNight[0], intHourMinuteNight[1], true);
            mon_switch5.setText("5) " + mondaySwitches.get(8).getType() + ": " + strArrHourMinuteDay[0]+":"+strArrHourMinuteDay[1]+" "+strArrHourMinuteDay[2] + ", " + mondaySwitches.get(9).getType() + ": "
                    + strArrHourMinuteNight[0]+":"+strArrHourMinuteNight[1]+" "+strArrHourMinuteNight[2] + " ");
            mon_independentSwitches[4].setVisibility(View.VISIBLE); // makes button appear
        } else {
            mon_switch5.setText("");
            mon_independentSwitches[4].setVisibility(View.INVISIBLE); // makes button appear
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
        if (weekProgram.data.get(day).get(8).getState()) {
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
            if (!weekProgram.data.get(day).get(2*i).getState()) {
                // Set the OFF pair to be the new switch.
                weekProgram.data.get(day).set(2*i, new Switch("day", true, dayTime));
                weekProgram.data.get(day).set(2*i + 1, new Switch("night", true, nightTime));
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
