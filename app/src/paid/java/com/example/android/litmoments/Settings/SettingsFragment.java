package com.example.android.litmoments.Settings;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceGroupAdapter;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceViewHolder;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListView;

import com.lit.litmoments.R;


public class SettingsFragment extends PreferenceFragmentCompat {


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_main);

        ListPreference listPreference = (ListPreference) findPreference(getString(R.string.key_uiTheme));
        if(listPreference.getValue()==null) {
            // to ensure we don't get a null value
            // set first value by default
            listPreference.setValueIndex(0);
        }
        listPreference.setSummary(listPreference.getValue().toString());
        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary(newValue.toString());
                return true;
            }
        });


        ListPreference listFontPreference = (ListPreference) findPreference(getString(R.string.key_uiThemeFont));
        if(listFontPreference.getValue()==null) {
            // to ensure we don't get a null value
            // set first value by default
            listPreference.setValueIndex(0);
        }
        listFontPreference.setSummary(listFontPreference.getValue().toString());
        listFontPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary(newValue.toString());
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

    @Override
    protected RecyclerView.Adapter onCreateAdapter(PreferenceScreen preferenceScreen) {
        return new CustomPreferenceGroupAdapter(preferenceScreen);
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
            Preference currentPreference = getItem(position);
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
