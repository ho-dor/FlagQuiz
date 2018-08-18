package com.example.kunal.flagquiz;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {
    //keys to store SharedPreference values.
        public static final String CHOICES = "pref_numberOfChoices";
        public static final String REGIONS = "pref_regionsToInclude";

    private boolean phone = true; //for portrait mode.
    private boolean preferencesChanged=true; //did the preferences change.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PreferenceManager.setDefaultValues(this,R.xml.preferences,false);

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                preferencesChanged = true;

                MainActivityFragment quizFragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.quizfragment);
            if(key.equals(CHOICES)){
                quizFragment.updateGuessRows(sharedPreferences);
                quizFragment.resetQuiz();
            }
            else if(key.equals(REGIONS)) {
                Set<String> regions = sharedPreferences.getStringSet(REGIONS, null);
                if (regions != null && regions.size() > 0) {
                    quizFragment.updateRegions(sharedPreferences);
                    quizFragment.resetQuiz();
                } else {
                    SharedPreferences.Editor editor =
                            sharedPreferences.edit();
                    regions.add(getString(R.string.default_region));
                    editor.putStringSet(REGIONS, regions);
                    editor.apply();
                    Toast.makeText(MainActivity.this,
                            R.string.default_region_message,
                            Toast.LENGTH_SHORT).show();
                }
            }
                Toast.makeText(MainActivity.this,
                         R.string.restarting_quiz,
                         Toast.LENGTH_SHORT).show();
            }
        });

        int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        if(screenSize==Configuration.SCREENLAYOUT_SIZE_LARGE||screenSize==Configuration.SCREENLAYOUT_SIZE_XLARGE){
            phone=false;
        }

        if(phone){
            setRequestedOrientation(
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(preferencesChanged){
            MainActivityFragment mainActivityFragment = (MainActivityFragment)getSupportFragmentManager().findFragmentById(R.id.quizfragment);
            mainActivityFragment.updateGuessRows(PreferenceManager.getDefaultSharedPreferences(this));
            mainActivityFragment.updateRegions(PreferenceManager.getDefaultSharedPreferences(this));
            mainActivityFragment.resetQuiz();
            preferencesChanged=false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        int orientation = getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_PORTRAIT) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Intent intent = new Intent(this,SettingsActivity.class);
        startActivity(intent);
       // int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }

}
