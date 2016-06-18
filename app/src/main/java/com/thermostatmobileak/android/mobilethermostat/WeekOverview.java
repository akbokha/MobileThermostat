package com.thermostatmobileak.android.mobilethermostat;

import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


// class for the overview with a viewpager and tablayout
// Code is partially from a thread on stackOverflow
public class WeekOverview extends AppCompatActivity{
    ViewPager pager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.week_overview);

        pager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        // Fragment manager to add fragment in viewpager we will pass object of Fragment manager to adpater class.
        FragmentManager manager = getSupportFragmentManager();

        //object of PagerAdapter passing fragment manager object as a parameter of constructor of PagerAdapter class.
        PagerAdapter adapter = new PagerAdapter(manager);

        //set Adapter to view pager
        pager.setAdapter(adapter);

        //set tablayout with viewpager
        tabLayout.setupWithViewPager(pager);

        // adding functionality to tab and viewpager to manage each other when a page is changed or when a tab is selected
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        //Setting tabs from adpater
        tabLayout.setTabsFromPagerAdapter(adapter);

    }

    // method that gets called when activity is quited (permanently)
    public void onDestroy() {
        super.onDestroy();
        Toast toast_changes_saved = Toast.makeText(getApplicationContext(), "Week Program change(s) are saved", Toast.LENGTH_SHORT);
        toast_changes_saved.show(); // display the toast: length short
    }

    // method that handles the stuff that should happen when the activity is paused (e.g. go to day/night temp activity)
    public void onPause(){
        super.onPause();
        Toast toast_changes_saved = Toast.makeText(getApplicationContext(), "Week Program change(s) are saved", Toast.LENGTH_SHORT);
        toast_changes_saved.show(); // display the toast: length short
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
            builder.setTitle("Week Program Info"); // title for the dialog box
            builder.setMessage(R.string.info_week_program); // text for in the dialog box
            builder.setPositiveButton("Go Back", null); // button that goes back to the activity
            builder.show();
        }
        return super.onOptionsItemSelected(item);
    }


}