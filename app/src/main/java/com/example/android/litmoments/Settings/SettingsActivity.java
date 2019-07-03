package com.example.android.litmoments.Settings;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.NavUtils;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ajts.androidmads.fontutils.FontUtils;
import com.example.android.litmoments.Application.AppCompatPreferenceActivity;
import com.example.android.litmoments.PrefMethods;
import com.example.android.litmoments.R;

import butterknife.BindView;
import butterknife.ButterKnife;



public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = SettingsActivity.class.getSimpleName();
    PrefMethods prefMethods = new PrefMethods();

    @BindView(R.id.toolbar) Toolbar toolbar;

    Boolean isWhite = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences;
        sharedPreferences  = PreferenceManager.getDefaultSharedPreferences(this);
        loadUiTheme(sharedPreferences);

      sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Settings");
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

           setUpThemeContent();
           loadWidgetColors(sharedPreferences);
            //getSupportActionBar().setDisplayShowTitleEnabled(true);
            //toolbar.setTitle("Settings");
        }


        /**ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        } **/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Fade fade= new Fade();
            View view = getWindow().getDecorView();
            fade.excludeTarget(view.findViewById(R.id.action_bar_container), true);
            fade.excludeTarget(android.R.id.statusBarBackground, true);
            fade.excludeTarget(android.R.id.navigationBarBackground, true);

           // getWindow().setEnterTransition(fade);
            //getWindow().setExitTransition(fade);
            // supportPostponeEnterTransition();

        }


        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // load settings fragment
       // getLayoutInflater().inflate(R.layout.settings_toolbar, (ViewGroup) findViewById(android.R.id.content));
       /** Toolbar toolbar = findViewById(R.id.entrytoolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setTitle("Settings");
        }
      **/
       // getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();

    }

   public void setUpThemeContent (){
       if(isWhite == true) {
           toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
       } else {
           toolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
       }
   }


    @Override
    public boolean onNavigateUp() {
       // return super.onNavigateUp();
        onBackPressed();
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //if (item.getItemId() == android.R.id.home) {
            //onBackPressed();
           // NavUtils.navigateUpFromSameTask(this);
        //}
        return super.onOptionsItemSelected(item);
    }

    private void loadUiTheme(SharedPreferences sharedPreferences){

        String userTheme = sharedPreferences.getString(getString(R.string.key_uiTheme), "2");
        if (userTheme.equals("2")) {
            setTheme(R.style.LitStyle);
            isWhite=false;
        }
        else if (userTheme.equals("1")) {
            setTheme(R.style.ReddishLitStyle);
            isWhite=true;
        }
        else if (userTheme.equals("0")) {
            setTheme(R.style.BlueLitStyle);
            isWhite=true;
        } else{

        }


    }

    public  void loadWidgetColors(SharedPreferences sharedPreferences){
        String selectedFont = sharedPreferences.getString(getString(R.string.key_uiThemeFont), "0");
        if(selectedFont.equals("0")){
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.parisienneregular);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
        } else if(selectedFont.equals("1")){
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.patrick_hand_sc);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
        } else if( selectedFont.equals("2")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.sofadi_one);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
        } else {

        }
    }



    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
     //prefMethods.changeListenerTheme(sharedPreferences, this);
        if(key.equals(getResources().getString(R.string.key_uiTheme))) {
            loadUiTheme(sharedPreferences);
            SettingsActivity.this.recreate();
        } else if(key.equals(getResources().getString(R.string.key_uiThemeFont))){
            loadWidgetColors(sharedPreferences);
            SettingsActivity.this.recreate();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);

    }
}
