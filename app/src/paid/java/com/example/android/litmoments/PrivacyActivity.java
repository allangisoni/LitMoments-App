package com.example.android.litmoments;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.ajts.androidmads.fontutils.FontUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PrivacyActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = PrivacyActivity.class.getName();
    @BindView(R.id.entrytoolbar)
    Toolbar toolbar;
    @BindView(R.id.textView_content) TextView tvPrivacy;
    @BindView(R.id.textView_content2) TextView tvPrivacy2;
    @BindView(R.id.textView_content3) TextView tvPrivacy3;
    @BindView(R.id.textView_content4) TextView tvPrivacy4;
    @BindView(R.id.textView_content5) TextView tvPrivacy5;
    @BindView(R.id.textView_content6) TextView tvPrivacy6;
    @BindView(R.id.textView_content7) TextView tvPrivacy7;
    @BindView(R.id.textView_content8) TextView tvPrivacy8;
    @BindView(R.id.textView_content9) TextView tvPrivacy9;
    @BindView(R.id.textView_content10) TextView tvPrivacy10;
    @BindView(R.id.textView_content11) TextView tvPrivacy11;

    Boolean isWhite =true, isStrawberry= false, isInnocentpink=false, isMagicalpurple=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        loadUiTheme(sharedPreferences);

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        ButterKnife.bind(this);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            setUpThemeContent();
            loadWidgetColors(sharedPreferences);

        }
    }

    public void setUpThemeContent() {
        if (isWhite == true) {
            toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
            ((TextView) toolbar.getChildAt(0)).setTextColor(getResources().getColor(android.R.color.white));


        } else {
            toolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
            ((TextView) toolbar.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }

    public  void loadWidgetColors(SharedPreferences sharedPreferences) {
        String selectedFont = sharedPreferences.getString(getString(R.string.key_uiThemeFont), "6");
        if (selectedFont.equals("0")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.parisienneregular);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy2, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy3, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy4, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy5, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy6, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy7, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy8, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy9, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy10, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy11, myCustomFont);



        } else if (selectedFont.equals("1")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.patrick_hand_sc);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy2, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy3, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy4, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy5, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy6, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy7, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy8, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy9, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy10, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy11, myCustomFont);

        } else if (selectedFont.equals("2")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.sofadi_one);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy2, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy3, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy4, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy5, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy6, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy7, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy8, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy9, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy10, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy11, myCustomFont);


        } else if (selectedFont.equals("3")) {
            FontUtils fontUtils = new FontUtils();
            Typeface myCustomFont = Typeface.create("sans-serif-condensed", Typeface.NORMAL);
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy2, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy3, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy4, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy5, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy6, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy7, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy8, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy9, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy10, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy11, myCustomFont);
        } else if (selectedFont.equals("4")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.concert_one);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy2, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy3, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy4, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy5, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy6, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy7, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy8, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy9, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy10, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy11, myCustomFont);

        } else if (selectedFont.equals("5")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.oleo_script);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy2, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy3, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy4, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy5, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy6, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy7, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy8, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy9, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy10, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy11, myCustomFont);
        } else if (selectedFont.equals("6")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.pt_sans_narrow);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy2, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy3, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy4, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy5, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy6, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy7, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy8, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy9, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy10, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy11, myCustomFont);

        } else if (selectedFont.equals("7")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.roboto_condensed_light);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy2, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy3, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy4, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy5, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy6, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy7, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy8, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy9, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy10, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy11, myCustomFont);
        } else if (selectedFont.equals("8")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.shadows_into_light);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy2, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy3, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy4, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy5, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy6, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy7, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy8, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy9, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy10, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy11, myCustomFont);
        } else if (selectedFont.equals("9")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.slabo_13px);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy2, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy3, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy4, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy5, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy6, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy7, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy8, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy9, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy10, myCustomFont);
            fontUtils.applyFontToView(tvPrivacy11, myCustomFont);

        }
    }
    private void loadUiTheme(SharedPreferences sharedPreferences){

        String userTheme = sharedPreferences.getString(getString(R.string.key_uiTheme), "2");
        if (userTheme.equals("2")) {
            setTheme(R.style.LitStyle);
            isWhite = false;
            isInnocentpink = true;
            isMagicalpurple = false;
            isStrawberry = false;
        }
        else if (userTheme.equals("1")) {
            setTheme(R.style.ReddishLitStyle);
            isWhite = true;
            isInnocentpink = false;
            isMagicalpurple = false;
            isStrawberry = true;

        }
        else if (userTheme.equals("0")) {
            setTheme(R.style.BlueLitStyle);
            isWhite = true;
            isInnocentpink = false;
            isMagicalpurple = true;
            isStrawberry = false;
        }else if (userTheme.equals("3")) {
            setTheme(R.style.YellowLitStyle);
            isWhite=false;
        } else if (userTheme.equals("4")) {
            setTheme(R.style.BluishLitStyle);
            isWhite=true;
        } else if (userTheme.equals("5")) {
            setTheme(R.style.GreenishLitStyle);
            isWhite=false;
        } else if (userTheme.equals("6")) {
            setTheme(R.style.TacaoLitStyle);
            isWhite=false;
        }else if (userTheme.equals("8")) {
            setTheme(R.style.TyrianLitStyle);
            isWhite=true;
        }else if (userTheme.equals("7")) {
            setTheme(R.style.DarkLitStyle);
            isWhite=true;
        }

        else{

        }
    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //prefMethods.changeListenerTheme(sharedPreferences, this);
        if(key.equals(getResources().getString(R.string.key_uiTheme))) {
            loadUiTheme(sharedPreferences);
            PrivacyActivity.this.recreate();
        } else if(key.equals(getResources().getString(R.string.key_uiThemeFont))){
            loadWidgetColors(sharedPreferences);
            PrivacyActivity.this.recreate();
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
