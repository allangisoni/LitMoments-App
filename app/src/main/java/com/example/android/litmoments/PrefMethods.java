package com.example.android.litmoments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;

public class PrefMethods extends AppCompatActivity{

    public   void setUpTheme(Context context, SharedPreferences.OnSharedPreferenceChangeListener listener){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        //SharedPreferences.OnSharedPreferenceChangeListener prefListener ;

      //  PreferenceManager.getDefaultSharedPreferences(context).registerOnSharedPreferenceChangeListener(listener);
       // sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        String userTheme = sharedPreferences.getString(context.getString(R.string.key_uiTheme), "2");
        if (userTheme.equals("2")) {
            context.setTheme(R.style.LitStyle);
        }
        else if (userTheme.equals("1")) {
           context.setTheme(R.style.BlueLitStyle);

        }
        else if (userTheme.equals("0")) {
            context.setTheme(R.style.BlueLitStyle);
        }
    }

    public void changeListenerTheme(SharedPreferences sharedPreferences, Context context){

        String userTheme = sharedPreferences.getString(getString(R.string.key_uiTheme), "2");
        if (userTheme.equals("2")) {
            context.setTheme(R.style.LitStyle);
        }
        else if (userTheme.equals("1")) {
            context.setTheme(R.style.BlueLitStyle);

        }
        else if (userTheme.equals("0")) {
            context.setTheme(R.style.BlueLitStyle);
        }
    }

}
