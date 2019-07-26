package com.example.android.litmoments.Settings;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceGroupAdapter;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceViewHolder;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ajts.androidmads.fontutils.FontUtils;
import com.codemybrainsout.ratingdialog.RatingDialog;
import com.example.android.litmoments.AboutusActivity;
import com.example.android.litmoments.Application.AppCompatPreferenceActivity;
import com.example.android.litmoments.Login.LoginActivity;
import com.example.android.litmoments.PrefMethods;
import com.example.android.litmoments.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;



public class SettingsActivity  extends AppCompatPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    Boolean isWhite =true;
    Toolbar toolbar;
//Loads Shared preferences
   SharedPreferences sharedPreferences;
   // private FirebaseAuth mAuth;
    private SettingsFragment settingsFragment;





    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPreferences  = PreferenceManager.getDefaultSharedPreferences(this);
        loadUiTheme(sharedPreferences);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        super.onCreate(savedInstanceState);

        setupActionBar();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Fade fade= new Fade();
            View view = getWindow().getDecorView();
            fade.excludeTarget(view.findViewById(R.id.action_bar_container), true);
            fade.excludeTarget(android.R.id.statusBarBackground, true);
            fade.excludeTarget(android.R.id.navigationBarBackground, true);

            getWindow().setEnterTransition(fade);
            getWindow().setExitTransition(fade);

        }

        // load settings fragment
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();


    }


    private void setupActionBar() {
      /**  ViewGroup rootView = (ViewGroup) findViewById(R.id.action_bar_root); //id from appcompat

        if (rootView != null) {
            View view = getLayoutInflater().inflate(R.layout.settings_toolbar, rootView, false);
            rootView.addView(view, 0);

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            setUpThemeContent();
            loadWidgetColors(sharedPreferences);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        } **/


        getLayoutInflater().inflate(R.layout.settings_toolbar, (ViewGroup) findViewById(android.R.id.content));
        toolbar = findViewById(R.id.toolbar);
       // toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setUpThemeContent();
      /**  Drawable backArrow = getResources().getDrawable(R.drawable.ic_back);
        if(isWhite == false) {
            backArrow.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        } else {
            backArrow.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        }
        getSupportActionBar().setHomeAsUpIndicator(backArrow);
         getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true); **/


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            if(isWhite == false) {
                toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
            } else {
                toolbar.getNavigationIcon().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
            }
        }

        loadWidgetColors(sharedPreferences);



    }



    public static class MainPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);
            FirebaseAuth mAuth;
            mAuth = FirebaseAuth.getInstance();

            // gallery EditText change listener
            //bindPreferenceSummaryToValue(findPreference(getString(R.string.key_uiTheme)));
            //bindPreferenceSummaryToValue(findPreference(getString(R.string.key_uiThemeFont)));

            // notification preference change listener
           // bindPreferenceSummaryToValue(findPreference(getString(R.string.key_notifications_new_message_ringtone)));

            Preference preference = findPreference(getResources().getString(R.string.key_userAccount));
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
              @Override
              public boolean onPreferenceClick(Preference preference) {
                 if(mAuth.getCurrentUser() != null){
                     mAuth.signOut();
                     Intent intent = new Intent(getActivity(), LoginActivity.class);
                     intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                     startActivity(intent);
                 }
                  return true;
              }
          });


            Preference prefabout = findPreference(getResources().getString(R.string.key_about));

            prefabout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity(), AboutusActivity.class);
                    startActivity(intent);
                    return true;
                }
            });


            Preference preffeedback = findPreference(getResources().getString(R.string.key_feedback));
            preffeedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"allangisoni@gmail.com"});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Lit moments feedback");
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(intent);
                    } else{
                        //
                        Toast.makeText(getActivity().getApplicationContext(), "No email client installed", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });


            Preference prefrateus = findPreference(getResources().getString(R.string.key_rateus));
            prefrateus.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    return true;
                }
            });
        }






        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            // remove dividers
            View rootView = getView();
            ListView list = (ListView) rootView.findViewById(android.R.id.list);
            list.setDivider(null);



        }




        static class CustomPreferenceGroupAdapter extends PreferenceGroupAdapter {

            @SuppressLint("RestrictedApi")
            public CustomPreferenceGroupAdapter(PreferenceGroup preferenceGroup) {
                super(preferenceGroup);
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onBindViewHolder(PreferenceViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);
                android.support.v7.preference.Preference currentPreference = getItem(position);
                //For a preference category we want the divider shown above.
                if (position != 0 && currentPreference instanceof PreferenceCategory) {
                    holder.setDividerAllowedAbove(true);
                    holder.setDividerAllowedBelow(false);
                } else {
                    //For other dividers we do not want to show divider above
                    //but allow dividers below for CategoryPreference dividers.
                    holder.setDividerAllowedAbove(false);
                    holder.setDividerAllowedBelow(true);
                }
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

 /**   private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }


     * A preference value change listener that updates the preference's summary
     * to reflect its new value.

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);


            }
            else {
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

  **/

    public void setUpThemeContent (){
        if(isWhite == true) {
         //   toolbar.getChildAt(0).setBackgroundColor(getResources().getColor(android.R.color.white));
            ((AppCompatTextView) toolbar.getChildAt(0)).setTextAppearance(this, R.style.SettingsTitleStyle);
        } else {
            ((AppCompatTextView) toolbar.getChildAt(0)).setTextAppearance(this, R.style.SettingsTitleStyleAccent);
        }
    }


    @Override
    public boolean onNavigateUp() {
        // return super.onNavigateUp();
        onBackPressed();
        return true;
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferencess, String key) {
        //prefMethods.changeListenerTheme(sharedPreferences, this);
        if(key.equals(getResources().getString(R.string.key_uiTheme))) {
            loadUiTheme(sharedPreferencess);
            this.recreate();
        } else if(key.equals(getResources().getString(R.string.key_uiThemeFont))){
            loadWidgetColors(sharedPreferencess);
            this.recreate();
        } else if (key.equals(getResources().getString(R.string.key_userAccount))){

        } else {

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



    @Override
    protected boolean isValidFragment(String fragmentName) {
        return SettingsActivity.class.getName().equals(fragmentName);
    }
}
