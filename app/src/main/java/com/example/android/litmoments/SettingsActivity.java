package com.example.android.litmoments;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ListView;


public class SettingsActivity extends AppCompatPreferenceActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // load settings fragment
        getLayoutInflater().inflate(R.layout.settings_toolbar, (ViewGroup) findViewById(android.R.id.content));
        Toolbar toolbar = findViewById(R.id.entrytoolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setTitle("Settings");
        }

        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();

    }


    @Override
    public boolean onNavigateUp() {
       // return super.onNavigateUp();
        onBackPressed();
        return true;
    }

    public static class MainPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);
            // gallery EditText change listener
          /**  bindPreferenceSummaryToValue(findPreference(getString(R.string.key_gallery_name)));

            // notification preference change listener
            bindPreferenceSummaryToValue(findPreference(getString(R.string.key_notifications_new_message_ringtone)));

            // feedback preference click listener
            Preference myPref = findPreference(getString(R.string.key_send_feedback));
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    sendFeedback(getActivity());
                    return true;
                }
            });

           **/
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
 /**
    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    } **/



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


}
