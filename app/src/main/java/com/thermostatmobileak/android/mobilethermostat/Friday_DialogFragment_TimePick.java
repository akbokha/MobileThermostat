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
public class Friday_DialogFragment_TimePick extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        // Todo: Set current entered as default
        int hour;
        int minute;
        if(Friday.dayOrNot){
            hour = Friday.input[0];
            minute = Friday.input[1];
        } else {
            hour = Friday.input[2];
            minute = Friday.input[3];
        }

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hours, int minute) {
        // Do something with the time chosen by the user
        if(Friday.dayOrNot){
            Friday.input[0] = hours;
            Friday.input[1] = minute;
        } else {
            Friday.input[2] = hours;
            Friday.input[3] = minute;
        }

        Friday.show_input(hours, minute);
    }
}
