package com.example.android.litmoments;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ajts.androidmads.fontutils.FontUtils;
import com.lit.litmoments.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutusActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {


    @BindView(R.id.linearLayout) LinearLayout linearLayout;
    @BindView(R.id.tvlitname) TextView litname;
    @BindView(R.id.tvlitversion) TextView litversion;
    @BindView(R.id.tvlitsummary) TextView litsummary;
    @BindView(R.id.tvlitdev) TextView litdev;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.ivabout)
    ImageView imageView;

    Boolean isWhite =true, isStrawberry= false, isInnocentpink=false, isMagicalpurple=false,
            isYellow = false, isBlue=false, isGreen=false, isTacao=false,isDark=false, isTyrian=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sharedPreferences;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        loadUiTheme(sharedPreferences);



        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
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

        if(isWhite == true){

            litname.setTextColor(getResources().getColor(android.R.color.white));
            litversion.setTextColor(getResources().getColor(android.R.color.white));
            litsummary.setTextColor(getResources().getColor(android.R.color.white));
            litdev.setTextColor(getResources().getColor(android.R.color.white));

        } else {
            litname.setTextColor(getResources().getColor(R.color.colorAccent));
            litversion.setTextColor(getResources().getColor(R.color.colorAccent));
            litsummary.setTextColor(getResources().getColor(R.color.colorAccent));
            litdev.setTextColor(getResources().getColor(R.color.colorAccent));
        }

        if(isStrawberry== true){
            linearLayout.setBackgroundColor(getResources().getColor(R.color.reddishcolorPrimary));
        }

        if(isMagicalpurple == true){
            linearLayout.setBackgroundColor(getResources().getColor(R.color.bluecolorPrimary));
        }

        if(isInnocentpink == true){
            linearLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }

        if(isYellow == true){
            linearLayout.setBackgroundColor(getResources().getColor(R.color.yellowcolorPrimary));
        }
        if(isBlue == true){
            linearLayout.setBackgroundColor(getResources().getColor(R.color.bluishcolorPrimary));
        }
        if(isGreen == true){
            linearLayout.setBackgroundColor(getResources().getColor(R.color.greenishcolorPrimary));
        }
        if(isTacao == true){
            linearLayout.setBackgroundColor(getResources().getColor(R.color.tacaocolorPrimary));
        }
        if(isDark == true){
            linearLayout.setBackgroundColor(getResources().getColor(R.color.blacktheme));
            imageView.setColorFilter(getResources().getColor(R.color.cardviewtitle));
        }
        if(isTyrian == true){
            linearLayout.setBackgroundColor(getResources().getColor(R.color.tyriancolorPrimary));
            imageView.setColorFilter(getResources().getColor(android.R.color.white));
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

    public  void loadWidgetColors(SharedPreferences sharedPreferences){
        String selectedFont = sharedPreferences.getString(getString(R.string.key_uiThemeFont), "6");
        if(selectedFont.equals("0")){
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.parisienneregular);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
            fontUtils.applyFontToView(litname, myCustomFont);
            fontUtils.applyFontToView(litsummary, myCustomFont);
            fontUtils.applyFontToView(litversion, myCustomFont);
            fontUtils.applyFontToView(litdev, myCustomFont);

        } else if(selectedFont.equals("1")){
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.patrick_hand_sc);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
            fontUtils.applyFontToView(litname, myCustomFont);
            fontUtils.applyFontToView(litsummary, myCustomFont);
            fontUtils.applyFontToView(litversion, myCustomFont);
            fontUtils.applyFontToView(litdev, myCustomFont);
        } else if( selectedFont.equals("2")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.sofadi_one);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
            fontUtils.applyFontToView(litname, myCustomFont);
            fontUtils.applyFontToView(litsummary, myCustomFont);
            fontUtils.applyFontToView(litversion, myCustomFont);
            fontUtils.applyFontToView(litdev, myCustomFont);
        } else if(selectedFont.equals("3")){
            FontUtils fontUtils = new FontUtils();
            Typeface myCustomFont = Typeface.create("sans-serif-condensed", Typeface.NORMAL);
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
            fontUtils.applyFontToView(litname, myCustomFont);
            fontUtils.applyFontToView(litsummary, myCustomFont);
            fontUtils.applyFontToView(litversion, myCustomFont);
            fontUtils.applyFontToView(litdev, myCustomFont);
        }
        else if( selectedFont.equals("4")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.concert_one);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
            fontUtils.applyFontToView(litname, myCustomFont);
            fontUtils.applyFontToView(litsummary, myCustomFont);
            fontUtils.applyFontToView(litversion, myCustomFont);
            fontUtils.applyFontToView(litdev, myCustomFont);


        }
        else if( selectedFont.equals("5")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.oleo_script);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
            fontUtils.applyFontToView(litname, myCustomFont);
            fontUtils.applyFontToView(litsummary, myCustomFont);
            fontUtils.applyFontToView(litversion, myCustomFont);
            fontUtils.applyFontToView(litdev, myCustomFont);
        }
        else if( selectedFont.equals("6")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.pt_sans_narrow);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
            fontUtils.applyFontToView(litname, myCustomFont);
            fontUtils.applyFontToView(litsummary, myCustomFont);
            fontUtils.applyFontToView(litversion, myCustomFont);
            fontUtils.applyFontToView(litdev, myCustomFont);

        }  else if( selectedFont.equals("7")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.roboto_condensed_light);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
            fontUtils.applyFontToView(litname, myCustomFont);
            fontUtils.applyFontToView(litsummary, myCustomFont);
            fontUtils.applyFontToView(litversion, myCustomFont);
            fontUtils.applyFontToView(litdev, myCustomFont);
        }  else if( selectedFont.equals("8")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.shadows_into_light);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
            fontUtils.applyFontToView(litname, myCustomFont);
            fontUtils.applyFontToView(litsummary, myCustomFont);
            fontUtils.applyFontToView(litversion, myCustomFont);
            fontUtils.applyFontToView(litdev, myCustomFont);
        } else if( selectedFont.equals("9")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.slabo_13px);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
            fontUtils.applyFontToView(litname, myCustomFont);
            litname.setTextSize(26f);
            fontUtils.applyFontToView(litsummary, myCustomFont);
            fontUtils.applyFontToView(litversion, myCustomFont);
            fontUtils.applyFontToView(litdev, myCustomFont);
        }else {

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
            isYellow =false;
            isBlue =false;
            isGreen=false;
            isTacao=false;
            isDark=false;
        }
        else if (userTheme.equals("1")) {
            setTheme(R.style.ReddishLitStyle);
            isWhite = true;
            isInnocentpink = false;
            isMagicalpurple = false;
            isStrawberry = true;
            isYellow =false;
            isBlue =false;
            isGreen=false;
            isTacao=false;
            isDark=false;

        }
        else if (userTheme.equals("0")) {
            setTheme(R.style.BlueLitStyle);
            isWhite = true;
            isInnocentpink = false;
            isMagicalpurple = true;
            isStrawberry = false;
            isYellow =false;
            isBlue =false;
            isGreen=false;
            isTacao=false;
            isDark=false;
        }
        else if (userTheme.equals("3")) {
            setTheme(R.style.YellowLitStyle);
            isWhite=false;
            isInnocentpink = false;
            isMagicalpurple = false;
            isStrawberry = false;
            isYellow =true;
            isBlue =false;
            isGreen=false;
            isTacao=false;
            isDark=false;

        } else if (userTheme.equals("4")) {
            setTheme(R.style.BluishLitStyle);
            isWhite=true;
            isInnocentpink = false;
            isMagicalpurple = false;
            isStrawberry = false;
            isYellow =false;
            isBlue =true;
            isGreen=false;
            isTacao=false;
            isDark=false;
        } else if (userTheme.equals("5")) {
            setTheme(R.style.GreenishLitStyle);
            isWhite=false;
            isInnocentpink = false;
            isMagicalpurple = false;
            isStrawberry = false;
            isYellow =false;
            isBlue =false;
            isGreen=true;
            isTacao=false;
            isDark=false;
        } else if (userTheme.equals("6")) {
            setTheme(R.style.TacaoLitStyle);
            isWhite=false;
            isInnocentpink = false;
            isMagicalpurple = false;
            isStrawberry = false;
            isYellow =false;
            isBlue =false;
            isGreen=false;
            isTacao=true;
            isDark=false;
        }else if (userTheme.equals("8")) {
            setTheme(R.style.TyrianLitStyle);
            isWhite=true;
            isInnocentpink = false;
            isMagicalpurple = false;
            isStrawberry = false;
            isYellow =false;
            isBlue =false;
            isGreen=false;
            isTacao=false;
            isDark=false;
            isTyrian=true;
        }
        else if (userTheme.equals("7")) {
            setTheme(R.style.DarkLitStyle);
            isWhite=true;
            isInnocentpink = false;
            isMagicalpurple = false;
            isStrawberry = false;
            isYellow =false;
            isBlue =false;
            isGreen=false;
            isTacao=false;
            isDark=true;

        }


        else{

        }
    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //prefMethods.changeListenerTheme(sharedPreferences, this);
        if(key.equals(getResources().getString(R.string.key_uiTheme))) {
            loadUiTheme(sharedPreferences);
            AboutusActivity.this.recreate();
        } else if(key.equals(getResources().getString(R.string.key_uiThemeFont))){
            loadWidgetColors(sharedPreferences);
            AboutusActivity.this.recreate();
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
