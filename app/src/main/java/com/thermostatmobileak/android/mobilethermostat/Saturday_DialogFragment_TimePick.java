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
public class Saturday_DialogFragment_TimePick extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        // Todo: Set current entered as default
        int hour;
        int minute;
        if(Saturday.dayOrNot){
            hour = Saturday.input[0];
            minute = Saturday.input[1];
        } else {
            hour = Saturday.input[2];
            minute = Saturday.input[3];
        }

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hours, int minute) {
        // Do something with the time chosen by the user
        if(Saturday.dayOrNot){
            Saturday.input[0] = hours;
            Saturday.input[1] = minute;
        } else {
            Saturday.input[2] = hours;
            Saturday.input[3] = minute;
        }

        Saturday.show_input(hours, minute);
    }
}
