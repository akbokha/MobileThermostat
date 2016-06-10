package com.thermostatmobileak.android.mobilethermostat;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;


import java.util.Calendar;

/**
 * Created by s158881 on 10/06/2016.
 */
public class Wednesday_DialogFragment_TimePick extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        // Todo: Set current entered as default
        int hour;
        int minute;
        if(Wednesday.dayOrNot){
            hour = Wednesday.input[0];
            minute = Wednesday.input[1];
        } else {
            hour = Wednesday.input[2];
            minute = Wednesday.input[3];
        }

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hours, int minute) {
        // Do something with the time chosen by the user
        if(Wednesday.dayOrNot){
            Wednesday.input[0] = hours;
            Wednesday.input[1] = minute;
        } else {
            Wednesday.input[2] = hours;
            Wednesday.input[3] = minute;
        }

        Wednesday.show_input(hours, minute);
    }
}
