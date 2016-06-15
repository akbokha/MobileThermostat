package com.thermostatmobileak.android.mobilethermostat;

import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
public class Wednesday extends Fragment {

    /* Arraylist for the switches for Wednesday and The initializations of the textviews for the
       switches in the layout for the wednesday switches */
    public ArrayList<Switch> wed_switches;
    TextView wed_switch1, wed_switch2, wed_switch3, wed_switch4, wed_switch5;

    // Textviews for displaying the day and night temperature in the weekday fragment
    TextView wed_dayTemp, wednesdayNightTemp;

    /* Buttons in the weekday fragment (one for adding switches, one to remove all switches
       and one that is an intent to the activity where you can change the day/night temperature*/
    Button wed_add, wed_removeAll, wed_changeDayNight;

    // Array for the imagebuttons (trashcans) to delete individual switches (contrary to removeAll)
    ImageButton[] wed_independentSwitches = new ImageButton[5];

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
    static String[] times;
    /* entry 0: day hours
       entry 1: day minutes
       entry 2: night hours
       entry 4: night minutes*/
    static int[] input;
    /* entry 0: day hours
       entry 1: day minutes
       entry 2: night hours
       entry 4: night minutes*/
    static Boolean dayOrNot;


    boolean grant = true;

    // Imagebuttons (pencils) to edit the day and night time when adding switches
    ImageButton wed_day_edit, wed_night_edit;

    // Textviews to display day and night time when adding switches
    TextView wed_day_time, wed_night_time;

    private OnFragmentInteractionListener mListener;

    public Wednesday() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // makes life easier / neater when passing/ calling certain methods
        day = "Wednesday";

        HeatingSystem.BASE_ADDRESS = "http://wwwis.win.tue.nl/2id40-ws/004";
        HeatingSystem.WEEK_PROGRAM_ADDRESS = "http://wwwis.win.tue.nl/2id40-ws/004/weekprogram";

        /* See stackoverflo, otherwise will get errors when using findviewbyID
        since we are extending Fragment instead of AppCompatActivity for example*/
        View view = inflater.inflate(R.layout.fragment_wednesday, container, false);
        super.onCreate(savedInstanceState);

        dayswitch_time = (TextView) view.findViewById(R.id.wed_day_time);
        nightswitch_time = (TextView) view.findViewById(R.id.wed_night_time);

        // needs to be in this order (because of call to show_input
        times = new String[4];
        input = new int[]{0, 0, 0, 0};
        dayOrNot = true;
        show_input(input[0], input[1]);
        dayOrNot = false;
        show_input(input[2], input[3]);

        wed_switch1 = (TextView)view.findViewById(R.id.wed_switch1);
        wed_switch2 = (TextView)view.findViewById(R.id.wed_switch2);
        wed_switch3 = (TextView)view.findViewById(R.id.wed_switch3);
        wed_switch4 = (TextView)view.findViewById(R.id.wed_switch4);
        wed_switch5 = (TextView)view.findViewById(R.id.wed_switch5);
        wed_independentSwitches[0] = (ImageButton)view.findViewById(R.id.wed_trash1);
        wed_independentSwitches[1] = (ImageButton)view.findViewById(R.id.wed_trash2);
        wed_independentSwitches[2] = (ImageButton)view.findViewById(R.id.wed_trash3);
        wed_independentSwitches[3] = (ImageButton)view.findViewById(R.id.wed_trash4);
        wed_independentSwitches[4] = (ImageButton)view.findViewById(R.id.wed_trash5);

        wed_day_edit = (ImageButton) view.findViewById(R.id.wed_day_edit);
        wed_day_time = (TextView) view.findViewById(R.id.wed_day_time);
        wed_night_edit = (ImageButton) view.findViewById(R.id.wed_night_edit);
        wed_night_time = (TextView) view.findViewById(R.id.wed_night_time);

        wed_dayTemp = (TextView)view.findViewById(R.id.wed_day_temp);
        wednesdayNightTemp = (TextView)view.findViewById(R.id.wed_night_temp);

        wed_add = (Button) view.findViewById(R.id.wed_add);
        wed_removeAll = (Button)view.findViewById(R.id.wed_remove_all);
        wed_changeDayNight = (Button) view.findViewById(R.id.wed_change_button);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try { // Get the day and night temperature to display in the fragment
                    dayTemp = Double.parseDouble(HeatingSystem.get("dayTemperature"));
                    nightTemp = Double.parseDouble(HeatingSystem.get("nightTemperature"));
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {  // setting the day and nigh temperature in the fragment
                            wed_dayTemp.setText(dayTemp + " \u2103");
                            wednesdayNightTemp.setText(nightTemp +  " \u2103");
                        }
                    });
                    weekProgram = HeatingSystem.getWeekProgram();
                    wed_switches = weekProgram.getDay("Wednesday");
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
                        wed_switches = weekProgram.getDay("Wednesday");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                show_max5_switches(); // display current switches
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
        wed_changeDayNight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), DayNight_Temp_Change.class));
            }
        });

        /* Listener for the adding of the swithes (when clicking the add switch button +
          handling the errors with toast messages (e.g. overlap switches)
         */
        wed_add.setOnClickListener(new View.OnClickListener() {
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
                // Check whether the inputted times (Wednesday_DialogFragment_TimePick) are acceptable in
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
                        // Check wheter there is overlap in th input (Wednesday_DialogFragment_TimePick)
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
                                    Toast overlap = Toast.makeText(getActivity().getApplicationContext(), "Switch is not added. Switch overlaps with current active switches. ", Toast.LENGTH_SHORT);
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
                            Toast night = Toast.makeText(getActivity().getApplicationContext(), "Switch is not added. Please make sure that the night switch is later than the day switch.", Toast.LENGTH_SHORT);
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
                            wed_switches = weekProgram.getDay("Wednesday");
                            // re-display
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    show_max5_switches(); // see org.thermostatapp.util for the method
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
        wed_removeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(v.getContext(), R.style.AppCompatAlertDialogStyle);
                    builder.setTitle("Confirmation Deletion Switches");
                    builder.setMessage("Are you sure you want to delete all the switches?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            //this deletes all the current switches (displayed) for this weekday
                            for (int i = 0; i < 5; i++){
                                weekProgram.data.get(day).set(2*i, new Switch("day", false, "23:59"));
                                weekProgram.data.get(day).set(2 * i + 1, new Switch("night", false, "23:59"));
                            }
                            show_max5_switches(); // see org.thermostatapp.util for the method
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
                            dialog.dismiss();
                        }  } );
                    builder.setNegativeButton("No",null);
                    builder.show();

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
            wed_independentSwitches[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // check which imagenbutton it was
                    ImageButton clickedButton = (ImageButton) v;
                    switch (clickedButton.getId()) {
                        case R.id.wed_trash1: // trash imagebutton for first switch
                            j = 0;
                            break;
                        case R.id.wed_trash2: // trash  imagebutton for second switch
                            j = 1;
                            break;
                        case R.id.wed_trash3:  // trash imagebutton for third switch
                            j = 2;
                            break;
                        case R.id.wed_trash4: // trash imagebutton for fourth switch
                            j = 3;
                            break;
                        case R.id.wed_trash5: // trash imagebutton for fifth switch
                            j=4;
                            break;
                    }
                    delete_1 = 2*j;
                    delete_2 = (2*j)+1;
                    // make the textview invisible when deleted (in the layout)
                    try {
                        wed_independentSwitches[j].setVisibility(View.INVISIBLE);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                show_max5_switches(); // see org.thermostatapp.util for the method
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
                                wed_switches = weekProgram.getDay("Wednesday");
                                // Display switches again (doesn't really work yet)
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        show_max5_switches(); // see org.thermostatapp.util for the method
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
        wed_day_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new Wednesday_DialogFragment_TimePick();
                newFragment.show(getActivity().getFragmentManager(), "timePicker");
                dayOrNot = true;
            }
        });

        /* Listener for the editing the time of the day switch (clicking on the edit imagebutton)
          We make use of a time picker dialog fragment (android provided)
          to set the time (hours : minutes)
        */
        wed_day_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new Wednesday_DialogFragment_TimePick();
                newFragment.show(getActivity().getFragmentManager(), "timePicker");
                dayOrNot = true;
            }

        });

        /* Listener for the editing the time of the night switch (clicking on the time textview)
          We make use of a time picker dialog fragment (android provided)
          to set the time (hours : minutes)
        */
        wed_night_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new Wednesday_DialogFragment_TimePick();
                newFragment.show(getActivity().getFragmentManager(), "timePicker");

                dayOrNot = false;
            }
        });

        /* Listener for the editing the time of the night switch (clicking on the edit imagebutton)
          We make use of a time picker dialog fragment (android provided)
          to set the time (hours : minutes)
        */
        wed_night_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new Wednesday_DialogFragment_TimePick();
                newFragment.show(getActivity().getFragmentManager(), "timePicker");
                dayOrNot = false;
            }

        });

        return view;
    }

    // End of onCreateView method

    /* onresume method. We reload things like the day and night temperature when the activity is
    revisited. It could for example be the case that we went to the change temperature activity (or
    to the main and via there to change day/night temperature) and back.
    The day and night temperature can therefore be changed.
     */
    public void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try { // (re-)fetch the day and night temppature
                    dayTemp = Double.parseDouble(HeatingSystem.get("dayTemperature"));
                    nightTemp = Double.parseDouble(HeatingSystem.get("nightTemperature"));
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() { // set the (new) day and night temperature in the layout
                            wed_dayTemp.setText(dayTemp + " \u2103");
                            wednesdayNightTemp.setText(nightTemp + " \u2103");
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();
    }

    static void show_input(int hours, int minute){

        boolean hours_lessThan10;
        if(hours < 10) {
            hours_lessThan10 = true;
        }
        else {
            hours_lessThan10 = false;
        }

        boolean minutes_lessThan10;
        if(minute < 10) {
            minutes_lessThan10 = true;
        }
        else {
            minutes_lessThan10 = false;
        }

        String str_hours = String.valueOf(hours);
        String str_minutes = String.valueOf(minute);

        if (hours_lessThan10){
            str_hours = 0 + str_hours;
        }

        if (minutes_lessThan10){
            str_minutes = 0 + str_minutes;
        }

        if(dayOrNot){
            times[0] = str_hours;
            times[1] = str_minutes;
        } else {
            times[2] = str_hours;
            times[3] = str_minutes;
        }

        if(dayOrNot){
            dayswitch_time.setText(times[0] + ":" + times[1]);
        } else {
            nightswitch_time.setText(times[2] + ":" + times[3]);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    // This method is provided since we use fragments for the weekdays
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    // This method is provided since we use fragments for the weekdays.
    // We need to override this method to inherit from Fragment
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    // This method is provided since we use fragments for the weekdays.
    // We need to override this method to inherit from Fragment
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

    // This method is provided since we use fragments for the weekdays.
    // We need to override this method to inherit from Fragment
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    // method that gets called when e.g. adding switches
    // Concatenate strings for the layout with correct information
    // + set visibility right in different cases (see if/else construction)
    void show_max5_switches(){ // first switch
        if (wed_switches.get(0).getState()) {
            String day_hours_minutes = wed_switches.get(0).getTime();
            int[] intday_hours_minutes = to_int_conversion_24hrs(day_hours_minutes);
            String[] string_a_day_hours_minutes = int_conversion_rightFormat(intday_hours_minutes[0], intday_hours_minutes[1]);
            String night_hours_minutes = wed_switches.get(1).getTime();
            int[] intnight_hours_minutes = to_int_conversion_24hrs(night_hours_minutes);
            String[] string_a_night_hours_minutes = int_conversion_rightFormat(intnight_hours_minutes[0], intnight_hours_minutes[1]);
            wed_switch1.setText("1. " + wed_switches.get(0).getType() + ": " + string_a_day_hours_minutes[0]+":"+string_a_day_hours_minutes[1] + ", " + wed_switches.get(1).getType() + ": "
                    + string_a_night_hours_minutes[0]+":"+string_a_night_hours_minutes[1] + " ");
            wed_independentSwitches[0].setVisibility(View.VISIBLE); // it becomes visible in the layout
        } else {
            wed_switch1.setText("");
            wed_independentSwitches[0].setVisibility(View.INVISIBLE); // it becomes invisible in the layout
        }
        if (wed_switches.get(2).getState()) { // second switch
            String day_hours_minutes = wed_switches.get(2).getTime();
            int[] intday_hours_minutes = to_int_conversion_24hrs(day_hours_minutes);
            String[] string_a_day_hours_minutes = int_conversion_rightFormat(intday_hours_minutes[0], intday_hours_minutes[1]);
            String night_hours_minutes = wed_switches.get(3).getTime();
            int[] intnight_hours_minutes = to_int_conversion_24hrs(night_hours_minutes);
            String[] string_a_night_hours_minutes = int_conversion_rightFormat(intnight_hours_minutes[0], intnight_hours_minutes[1]);
            wed_switch2.setText("2. " + wed_switches.get(2).getType() + ": " + string_a_day_hours_minutes[0]+":"+string_a_day_hours_minutes[1] + ", " + wed_switches.get(3).getType() + ": "
                    + string_a_night_hours_minutes[0]+":"+string_a_night_hours_minutes[1] + " ");
            wed_independentSwitches[1].setVisibility(View.VISIBLE); // it becomes visible in the layout
        } else {
            wed_switch2.setText("");
            wed_independentSwitches[1].setVisibility(View.INVISIBLE); // it becomes invisible in the layout
        }
        if (wed_switches.get(4).getState()) { // third switch
            String day_hours_minutes = wed_switches.get(4).getTime();
            int[] intday_hours_minutes = to_int_conversion_24hrs(day_hours_minutes);
            String[] string_a_day_hours_minutes = int_conversion_rightFormat(intday_hours_minutes[0], intday_hours_minutes[1]);
            String night_hours_minutes = wed_switches.get(5).getTime();
            int[] intnight_hours_minutes = to_int_conversion_24hrs(night_hours_minutes);
            String[] string_a_night_hours_minutes = int_conversion_rightFormat(intnight_hours_minutes[0], intnight_hours_minutes[1]);
            wed_switch3.setText("3. " + wed_switches.get(4).getType() + ": " + string_a_day_hours_minutes[0]+":"+string_a_day_hours_minutes[1] + ", " + wed_switches.get(5).getType() + ": "
                    + string_a_night_hours_minutes[0]+":"+string_a_night_hours_minutes[1] + " ");
            wed_independentSwitches[2].setVisibility(View.VISIBLE); // it becomes visible in the layout
        } else {
            wed_switch3.setText("");
            wed_independentSwitches[2].setVisibility(View.INVISIBLE); // it becomes invisible in the layout
        }
        if (wed_switches.get(6).getState()) { // fourt switch
            String day_hours_minutes = wed_switches.get(6).getTime();
            int[] intday_hours_minutes = to_int_conversion_24hrs(day_hours_minutes);
            String[] string_a_day_hours_minutes = int_conversion_rightFormat(intday_hours_minutes[0], intday_hours_minutes[1]);
            String night_hours_minutes = wed_switches.get(7).getTime();
            int[] intnight_hours_minutes = to_int_conversion_24hrs(night_hours_minutes);
            String[] string_a_night_hours_minutes = int_conversion_rightFormat(intnight_hours_minutes[0], intnight_hours_minutes[1]);
            wed_switch4.setText("4. " + wed_switches.get(6).getType() + ": " + string_a_day_hours_minutes[0]+":"+string_a_day_hours_minutes[1] + ", " + wed_switches.get(7).getType() + ": "
                    + string_a_night_hours_minutes[0]+":"+string_a_night_hours_minutes[1] + " ");
            wed_independentSwitches[3].setVisibility(View.VISIBLE); // it becomes visible in the layout
        } else {
            wed_switch4.setText("");
            wed_independentSwitches[3].setVisibility(View.INVISIBLE); // it becomes invisible in the layout
        }
        if (wed_switches.get(8).getState()) { // fifth switch
            String day_hours_minutes = wed_switches.get(8).getTime();
            int[] intday_hours_minutes = to_int_conversion_24hrs(day_hours_minutes);
            String[] string_a_day_hours_minutes = int_conversion_rightFormat(intday_hours_minutes[0], intday_hours_minutes[1]);
            String night_hours_minutes = wed_switches.get(9).getTime();
            int[] intnight_hours_minutes = to_int_conversion_24hrs(night_hours_minutes);
            String[] string_a_night_hours_minutes = int_conversion_rightFormat(intnight_hours_minutes[0], intnight_hours_minutes[1]);
            wed_switch5.setText("5. " + wed_switches.get(8).getType() + ": " + string_a_day_hours_minutes[0]+":"+string_a_day_hours_minutes[1] + ", " + wed_switches.get(9).getType() + ": "
                    + string_a_night_hours_minutes[0]+":"+string_a_night_hours_minutes[1] + " ");
            wed_independentSwitches[4].setVisibility(View.VISIBLE); // it becomes visible in the layout
        } else {
            wed_switch5.setText("");
            wed_independentSwitches[4].setVisibility(View.INVISIBLE); // it becomes invisible in the layout
        }
    }

    // Method that convers the time to the right output format
    static String[] int_conversion_rightFormat(int hour, int minute) {
        String minuteStr = String.valueOf(minute);

        if(minute < 10){
            minuteStr = "0"+String.valueOf(minute);
        }

        String hourStr = String.valueOf(hour);

        if(hour < 10){
            hourStr = "0"+String.valueOf(hour);
        }

        String[] hour_minutes = new String[]{hourStr, minuteStr, ""};
        return hour_minutes;
    }

    // Method that we use for conversion to an integer (from the string)
    int[] to_int_conversion_24hrs(String hourMinute) {
        String hours = hourMinute.substring(0,2); // get hours
        String minutes = hourMinute.substring(3,5); // get minutes

        int int_hours = Integer.parseInt(hours); // parse to an integer
        int int_minutes = Integer.parseInt(minutes); // parse to an integer

        int[] hours_minutes = new int[]{int_hours, int_minutes}; // make array of length two with
        // the two integer values for hours and minutes
        return hours_minutes; // return this array to be used further on
    }

    /* For each weekday we can set the day and night switch with this method
     + error handling and user feedback with toast-messages to notify the user when swithces
     are not added and why they are not added
    */
    public void setSwitch(String day, String dayTime, String nightTime) {
        // Notify the user that the maximum amount of switches (5) has been reached
        if (weekProgram.data.get(day).get(8).getState()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast toast_max_switches5 = Toast.makeText(getActivity().getApplicationContext(), "Switch is not added. You can add a maximum of 5 switches per weekday", Toast.LENGTH_SHORT);
                    toast_max_switches5.show(); // display the toast: length long
                }
            });
        }
        for (int i=0; i<5; i++) { // max 5 switches, therefore <
            // Search the off pair
            if (!weekProgram.data.get(day).get(2*i).getState()) {
                // same for new switch
                weekProgram.data.get(day).set(2*i, new Switch("day", true, dayTime));
                weekProgram.data.get(day).set(2*i + 1, new Switch("night", true, nightTime));
                /* This is rather error prevention than error handling. We notify the user
                that at this moment 5 switches have been added. The user will now know (if it is
                not a newby) that he can not add any more switches -> this will prevent him/her
                of adding another one (other toast message will then not be displayed)
                 */
                if(i==4) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast_max_switches5 = Toast.makeText(getActivity().getApplicationContext(), "You've now added the maximum of 5 switches.", Toast.LENGTH_SHORT);
                            toast_max_switches5.show(); // display the toast: length long
                        }
                    });
                }
                i=5;
            }
        }
    }


}
