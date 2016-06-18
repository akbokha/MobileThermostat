package com.thermostatmobileak.android.mobilethermostat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by s158881 on 6-6-2016.
 */
public class PagerAdapter extends FragmentStatePagerAdapter{
    /// we need this method for the scrollable fragment ayout that we use
    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        Fragment frag =null;
        switch (position){
            case 0:
                frag =new Monday();
                break;
            case 1:
                frag = new Tuesday();
                break;
            case 2:
                frag =new Wednesday();
                break;
            case 3:
                frag = new Thursday();
                break;
            case 4:
                frag = new Friday();
                break;
            case 5:
                frag = new Saturday();
                break;
            case 6:
                frag = new Sunday();
                break;
        }
        return frag;
    }

    @Override
    public int getCount() {
        return 7;
    }

    // the text of the differnt fragments of the layout
    @Override
    public CharSequence getPageTitle(int position) {
        String title=" ";
        switch (position){
            case 0:
                title="MON";
                break;
            case 1:
                title="TUE";
                break;
            case 2:
                title="WED";
                break;
            case 3:
                title = "THU";
                break;
            case 4:
                title = "FRI";
                break;
            case 5:
                title = "SAT";
                break;
            case 6:
                title = "SUN";
                break;
        }

        return title;
    }
}



