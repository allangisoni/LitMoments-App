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

public class TermsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = TermsActivity.class.getName();
    @BindView(R.id.entrytoolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_terms) TextView tv_terms;
    @BindView(R.id.tv_terms1) TextView tv_terms1;
    @BindView(R.id.tv_terms2) TextView tv_terms2;
    @BindView(R.id.tv_terms3) TextView tv_terms3;
    @BindView(R.id.tv_terms4) TextView tv_terms4;
    @BindView(R.id.tv_terms5) TextView tv_terms5;
    @BindView(R.id.tv_terms6) TextView tv_terms6;
    @BindView(R.id.tv_terms7) TextView tv_terms7;
    @BindView(R.id.tv_terms8) TextView tv_terms8;
    @BindView(R.id.tv_terms9) TextView tv_terms9;
    @BindView(R.id.tv_terms10) TextView tv_terms10;

    Boolean isWhite =true, isStrawberry= false, isInnocentpink=false, isMagicalpurple=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        loadUiTheme(sharedPreferences);

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

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
            fontUtils.applyFontToView(tv_terms, myCustomFont);
            fontUtils.applyFontToView(tv_terms1, myCustomFont);
            fontUtils.applyFontToView(tv_terms2, myCustomFont);
            fontUtils.applyFontToView(tv_terms3, myCustomFont);
            fontUtils.applyFontToView(tv_terms4, myCustomFont);
            fontUtils.applyFontToView(tv_terms5, myCustomFont);
            fontUtils.applyFontToView(tv_terms6, myCustomFont);
            fontUtils.applyFontToView(tv_terms7, myCustomFont);
            fontUtils.applyFontToView(tv_terms8, myCustomFont);
            fontUtils.applyFontToView(tv_terms9, myCustomFont);
            fontUtils.applyFontToView(tv_terms10, myCustomFont);




        } else if (selectedFont.equals("1")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.patrick_hand_sc);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
            fontUtils.applyFontToView(tv_terms, myCustomFont);
            fontUtils.applyFontToView(tv_terms1, myCustomFont);
            fontUtils.applyFontToView(tv_terms2, myCustomFont);
            fontUtils.applyFontToView(tv_terms3, myCustomFont);
            fontUtils.applyFontToView(tv_terms4, myCustomFont);
            fontUtils.applyFontToView(tv_terms5, myCustomFont);
            fontUtils.applyFontToView(tv_terms6, myCustomFont);
            fontUtils.applyFontToView(tv_terms7, myCustomFont);
            fontUtils.applyFontToView(tv_terms8, myCustomFont);
            fontUtils.applyFontToView(tv_terms9, myCustomFont);
            fontUtils.applyFontToView(tv_terms10, myCustomFont);

        } else if (selectedFont.equals("2")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.sofadi_one);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
            fontUtils.applyFontToView(tv_terms, myCustomFont);
            fontUtils.applyFontToView(tv_terms1, myCustomFont);
            fontUtils.applyFontToView(tv_terms2, myCustomFont);
            fontUtils.applyFontToView(tv_terms3, myCustomFont);
            fontUtils.applyFontToView(tv_terms4, myCustomFont);
            fontUtils.applyFontToView(tv_terms5, myCustomFont);
            fontUtils.applyFontToView(tv_terms6, myCustomFont);
            fontUtils.applyFontToView(tv_terms7, myCustomFont);
            fontUtils.applyFontToView(tv_terms8, myCustomFont);
            fontUtils.applyFontToView(tv_terms9, myCustomFont);
            fontUtils.applyFontToView(tv_terms10, myCustomFont);


        } else if (selectedFont.equals("3")) {
            FontUtils fontUtils = new FontUtils();
            Typeface myCustomFont = Typeface.create("sans-serif-condensed", Typeface.NORMAL);
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
            fontUtils.applyFontToView(tv_terms, myCustomFont);
            fontUtils.applyFontToView(tv_terms1, myCustomFont);
            fontUtils.applyFontToView(tv_terms2, myCustomFont);
            fontUtils.applyFontToView(tv_terms3, myCustomFont);
            fontUtils.applyFontToView(tv_terms4, myCustomFont);
            fontUtils.applyFontToView(tv_terms5, myCustomFont);
            fontUtils.applyFontToView(tv_terms6, myCustomFont);
            fontUtils.applyFontToView(tv_terms7, myCustomFont);
            fontUtils.applyFontToView(tv_terms8, myCustomFont);
            fontUtils.applyFontToView(tv_terms9, myCustomFont);
            fontUtils.applyFontToView(tv_terms10, myCustomFont);
        } else if (selectedFont.equals("4")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.concert_one);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
            fontUtils.applyFontToView(tv_terms, myCustomFont);
            fontUtils.applyFontToView(tv_terms1, myCustomFont);
            fontUtils.applyFontToView(tv_terms2, myCustomFont);
            fontUtils.applyFontToView(tv_terms3, myCustomFont);
            fontUtils.applyFontToView(tv_terms4, myCustomFont);
            fontUtils.applyFontToView(tv_terms5, myCustomFont);
            fontUtils.applyFontToView(tv_terms6, myCustomFont);
            fontUtils.applyFontToView(tv_terms7, myCustomFont);
            fontUtils.applyFontToView(tv_terms8, myCustomFont);
            fontUtils.applyFontToView(tv_terms9, myCustomFont);
            fontUtils.applyFontToView(tv_terms10, myCustomFont);

        } else if (selectedFont.equals("5")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.oleo_script);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
            fontUtils.applyFontToView(tv_terms, myCustomFont);
            fontUtils.applyFontToView(tv_terms1, myCustomFont);
            fontUtils.applyFontToView(tv_terms2, myCustomFont);
            fontUtils.applyFontToView(tv_terms3, myCustomFont);
            fontUtils.applyFontToView(tv_terms4, myCustomFont);
            fontUtils.applyFontToView(tv_terms5, myCustomFont);
            fontUtils.applyFontToView(tv_terms6, myCustomFont);
            fontUtils.applyFontToView(tv_terms7, myCustomFont);
            fontUtils.applyFontToView(tv_terms8, myCustomFont);
            fontUtils.applyFontToView(tv_terms9, myCustomFont);
            fontUtils.applyFontToView(tv_terms10, myCustomFont);
        } else if (selectedFont.equals("6")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.pt_sans_narrow);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
            fontUtils.applyFontToView(tv_terms, myCustomFont);
            fontUtils.applyFontToView(tv_terms1, myCustomFont);
            fontUtils.applyFontToView(tv_terms2, myCustomFont);
            fontUtils.applyFontToView(tv_terms3, myCustomFont);
            fontUtils.applyFontToView(tv_terms4, myCustomFont);
            fontUtils.applyFontToView(tv_terms5, myCustomFont);
            fontUtils.applyFontToView(tv_terms6, myCustomFont);
            fontUtils.applyFontToView(tv_terms7, myCustomFont);
            fontUtils.applyFontToView(tv_terms8, myCustomFont);
            fontUtils.applyFontToView(tv_terms9, myCustomFont);
            fontUtils.applyFontToView(tv_terms10, myCustomFont);

        } else if (selectedFont.equals("7")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.roboto_condensed_light);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
            fontUtils.applyFontToView(tv_terms, myCustomFont);
            fontUtils.applyFontToView(tv_terms1, myCustomFont);
            fontUtils.applyFontToView(tv_terms2, myCustomFont);
            fontUtils.applyFontToView(tv_terms3, myCustomFont);
            fontUtils.applyFontToView(tv_terms4, myCustomFont);
            fontUtils.applyFontToView(tv_terms5, myCustomFont);
            fontUtils.applyFontToView(tv_terms6, myCustomFont);
            fontUtils.applyFontToView(tv_terms7, myCustomFont);
            fontUtils.applyFontToView(tv_terms8, myCustomFont);
            fontUtils.applyFontToView(tv_terms9, myCustomFont);
            fontUtils.applyFontToView(tv_terms10, myCustomFont);
        } else if (selectedFont.equals("8")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.shadows_into_light);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
            fontUtils.applyFontToView(tv_terms, myCustomFont);
            fontUtils.applyFontToView(tv_terms1, myCustomFont);
            fontUtils.applyFontToView(tv_terms2, myCustomFont);
            fontUtils.applyFontToView(tv_terms3, myCustomFont);
            fontUtils.applyFontToView(tv_terms4, myCustomFont);
            fontUtils.applyFontToView(tv_terms5, myCustomFont);
            fontUtils.applyFontToView(tv_terms6, myCustomFont);
            fontUtils.applyFontToView(tv_terms7, myCustomFont);
            fontUtils.applyFontToView(tv_terms8, myCustomFont);
            fontUtils.applyFontToView(tv_terms9, myCustomFont);
            fontUtils.applyFontToView(tv_terms10, myCustomFont);
        } else if (selectedFont.equals("9")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.slabo_13px);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
            fontUtils.applyFontToView(tv_terms, myCustomFont);
            fontUtils.applyFontToView(tv_terms1, myCustomFont);
            fontUtils.applyFontToView(tv_terms2, myCustomFont);
            fontUtils.applyFontToView(tv_terms3, myCustomFont);
            fontUtils.applyFontToView(tv_terms4, myCustomFont);
            fontUtils.applyFontToView(tv_terms5, myCustomFont);
            fontUtils.applyFontToView(tv_terms6, myCustomFont);
            fontUtils.applyFontToView(tv_terms7, myCustomFont);
            fontUtils.applyFontToView(tv_terms8, myCustomFont);
            fontUtils.applyFontToView(tv_terms9, myCustomFont);
            fontUtils.applyFontToView(tv_terms10, myCustomFont);

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
        } else if (userTheme.equals("8")) {
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
            TermsActivity.this.recreate();
        } else if(key.equals(getResources().getString(R.string.key_uiThemeFont))){
            loadWidgetColors(sharedPreferences);
            TermsActivity.this.recreate();
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
