package com.lit.litmoments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.ajts.androidmads.fontutils.FontUtils;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lit.litmoments.AddJournal.JournalEntryModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.renderer.YAxisRenderer;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.lit.litmoments.Dashboard.DashMonthFragment;
import com.lit.litmoments.Dashboard.DashWeekFragment;
import com.lit.litmoments.Dashboard.DashYearFragment;
import com.lit.litmoments.Favorites.FavoritesFragment;
import com.lit.litmoments.Main.HomeFragment;
import com.lit.litmoments.Main.TabbedMainActivity;
import com.lit.litmoments.Photos.PhotosActivity;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.lit.litmoments.Data.RetrieveData.DATABASE_UPLOADS;

public class DashboardActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = DashboardActivity.class.getName();
    @BindView(R.id.entrytoolbar) Toolbar entryToolbar;


    private ArrayList<JournalEntryModel> journalList = new ArrayList<>();

    Boolean isWhite = false, isLit = false,isDark = false;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private Context mContext;
    public static final String DATABASE_UPLOADS = "User's Journal Entries";

    private TabLayout tabLayout;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        loadUiTheme(sharedPreferences);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);
        if (entryToolbar != null) {
            setSupportActionBar(entryToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            //setUpThemeContent();
            loadWidgetColors(sharedPreferences);

        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


     /**   FirebaseApp.initializeApp(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            //  Toast.makeText(MainActivity.this, "Welcome back "+" "+ mAuth.getCurrentUser().getDisplayName()+" ", Toast.LENGTH_SHORT).show();
        }

        String currentUid = mAuth.getCurrentUser().getUid();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        mDatabase = firebaseDatabase.getReference(DATABASE_UPLOADS).child(currentUid);
        mContext = DashboardActivity.this;


        if(getIntent().getSerializableExtra("JournalList") != null){
          // journalList =(ArrayList<JournalEntryModel>) getIntent().getSerializableExtra("JournalList");
       } else{
           Log.d("JournalItems", Integer.toString(journalList.size()));
       } **/

     /**   tvPieChartTitle.setTextColor(getResources().getColor(R.color.bluecolorPrimary));
        tvPieChartTitle.setTextSize(16f);
        cvPieChart.setCardBackgroundColor(getResources().getColor(R.color.piechartBackground));
        tvBarChartTitle.setTextColor(getResources().getColor(R.color.bluecolorPrimary));
        tvBarChartTitle.setTextSize(16f);
        cvBarChart.setCardBackgroundColor(getResources().getColor(R.color.piechartBackground));
        cvTreeChart.setCardBackgroundColor(getResources().getColor(R.color.piechartBackground));
        favoriteplacesTitle.setTextSize(16f);
        favoritePlaces.setTextSize(14f);
        favoriteplacesTitle.setTextColor(getResources().getColor(R.color.bluecolorPrimary));
        favoritePlaces.setTextColor(getResources().getColor(R.color.background));
        if(!isDark) {
            tvBarChartTitle.setTextColor(getResources().getColor(R.color.bluecolorPrimary));
            cvPieChart.setCardBackgroundColor(getResources().getColor(R.color.piechartBackground));
            cvBarChart.setCardBackgroundColor(getResources().getColor(R.color.piechartBackground));
            cvTreeChart.setCardBackgroundColor(getResources().getColor(R.color.piechartBackground));
            favoriteplacesTitle.setTextColor(getResources().getColor(R.color.bluecolorPrimary));
            favoritePlaces.setTextColor(getResources().getColor(R.color.background));
        } else {
            tvBarChartTitle.setTextColor(getResources().getColor(R.color.bluecolorPrimary));
            cvPieChart.setCardBackgroundColor(getResources().getColor(R.color.blacktheme));
            cvBarChart.setCardBackgroundColor(getResources().getColor(R.color.blacktheme));
            cvTreeChart.setCardBackgroundColor(getResources().getColor(R.color.blacktheme));
            favoritePlaces.setTextColor(getResources().getColor(R.color.background));
            favoriteplacesTitle.setTextColor(getResources().getColor(R.color.bluecolorPrimary));
        }
**/
        //setPieChartData();
       // setBarChartData();



        if(journalList.size() > 0) {
         //   setMoodBarChart();
           // setTripCount();
           // getFavoritePlaces();
            //getData( journalList, mDatabase);

        } else{
            //RetrieveData retrieveData = new RetrieveData(this);
           // getData( journalList, mDatabase);
           // Toast.makeText(mContext, " "+ journalList.size(), Toast.LENGTH_SHORT).show();
        }


    }


    public void setUpThemeContent() {
        if (isWhite == true) {
            entryToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
            ((TextView) entryToolbar.getChildAt(0)).setTextColor(getResources().getColor(android.R.color.white));


        } else {
            entryToolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
            ((TextView) entryToolbar.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorAccent));
        }

        if(isDark==true && isWhite== true) {
            entryToolbar.setTitleTextColor(getResources().getColor(R.color.blackthemeAccent));
            ((TextView) entryToolbar.getChildAt(0)).setTextColor(getResources().getColor(R.color.blackthemeAccent));

        }
    }


    public  void loadWidgetColors(SharedPreferences sharedPreferences){
        String selectedFont = sharedPreferences.getString(getString(R.string.key_uiThemeFont), "6");
        if(selectedFont.equals("0")){
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.parisienneregular);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(entryToolbar, myCustomFont);
            //fontUtils.applyFontToView(tvPieChartTitle, myCustomFont);
           // fontUtils.applyFontToView(tvBarChartTitle, myCustomFont);
           // fontUtils.applyFontToView(favoritePlaces, myCustomFont);
            //fontUtils.applyFontToView(favoriteplacesTitle, myCustomFont);

        } else if(selectedFont.equals("1")){
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.patrick_hand_sc);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(entryToolbar, myCustomFont);
          //  fontUtils.applyFontToView(tvPieChartTitle, myCustomFont);
           // fontUtils.applyFontToView(tvBarChartTitle, myCustomFont);
           // fontUtils.applyFontToView(favoritePlaces, myCustomFont);
           // fontUtils.applyFontToView(favoriteplacesTitle, myCustomFont);
        } else if( selectedFont.equals("2")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.sofadi_one);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(entryToolbar, myCustomFont);
          //  fontUtils.applyFontToView(tvPieChartTitle, myCustomFont);
           // fontUtils.applyFontToView(tvBarChartTitle, myCustomFont);
           // fontUtils.applyFontToView(favoritePlaces, myCustomFont);
           // fontUtils.applyFontToView(favoriteplacesTitle, myCustomFont);
        } else if( selectedFont.equals("4")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.concert_one);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(entryToolbar, myCustomFont);
          //  fontUtils.applyFontToView(tvPieChartTitle, myCustomFont);
           // fontUtils.applyFontToView(tvBarChartTitle, myCustomFont);
           // fontUtils.applyFontToView(favoritePlaces, myCustomFont);
            //fontUtils.applyFontToView(favoriteplacesTitle, myCustomFont);

        }
        else if( selectedFont.equals("5")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.oleo_script);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(entryToolbar, myCustomFont);
           // fontUtils.applyFontToView(tvPieChartTitle, myCustomFont);
           // fontUtils.applyFontToView(tvBarChartTitle, myCustomFont);
           // fontUtils.applyFontToView(favoritePlaces, myCustomFont);
           // fontUtils.applyFontToView(favoriteplacesTitle, myCustomFont);
        }
        else if( selectedFont.equals("6")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.pt_sans_narrow);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(entryToolbar, myCustomFont);
            //fontUtils.applyFontToView(tvPieChartTitle, myCustomFont);
          //  fontUtils.applyFontToView(tvBarChartTitle, myCustomFont);
          //  fontUtils.applyFontToView(favoritePlaces, myCustomFont);
          //  fontUtils.applyFontToView(favoriteplacesTitle, myCustomFont);

        }  else if( selectedFont.equals("7")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.roboto_condensed_light);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(entryToolbar, myCustomFont);
           // fontUtils.applyFontToView(tvPieChartTitle, myCustomFont);
          //  fontUtils.applyFontToView(tvBarChartTitle, myCustomFont);
           // fontUtils.applyFontToView(favoritePlaces, myCustomFont);
           // fontUtils.applyFontToView(favoriteplacesTitle, myCustomFont);
        }  else if( selectedFont.equals("8")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.shadows_into_light);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(entryToolbar, myCustomFont);
            //fontUtils.applyFontToView(tvPieChartTitle, myCustomFont);
           // fontUtils.applyFontToView(tvBarChartTitle, myCustomFont);
           // fontUtils.applyFontToView(favoritePlaces, myCustomFont);
           // fontUtils.applyFontToView(favoriteplacesTitle, myCustomFont);
        } else if( selectedFont.equals("9")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.slabo_13px);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(entryToolbar, myCustomFont);
         //   fontUtils.applyFontToView(tvPieChartTitle, myCustomFont);
           // fontUtils.applyFontToView(tvBarChartTitle, myCustomFont);
          //  fontUtils.applyFontToView(favoritePlaces, myCustomFont);
          //  fontUtils.applyFontToView(favoriteplacesTitle, myCustomFont);
        }else {

        }
    }
    private void loadUiTheme(SharedPreferences sharedPreferences){

        String userTheme = sharedPreferences.getString(getString(R.string.key_uiTheme), "2");
        if (userTheme.equals("2")) {
            setTheme(R.style.LitStyle);
           isWhite = false;
        }
        else if (userTheme.equals("1")) {
            setTheme(R.style.ReddishLitStyle);
            isWhite = true;

            //isLit = true;

        }
        else if (userTheme.equals("0")) {
            setTheme(R.style.BlueLitStyle);
            isWhite = true;

            //isLit =  false;
        } else if (userTheme.equals("3")) {
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
            isDark=false;
        }else if (userTheme.equals("7")) {
            setTheme(R.style.DarkLitStyle);
            isWhite=true;
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
            DashboardActivity.this.recreate();
        } else if(key.equals(getResources().getString(R.string.key_uiThemeFont))){
            loadWidgetColors(sharedPreferences);
            DashboardActivity.this.recreate();
        }
    }





    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DashWeekFragment(), "Week");
        adapter.addFragment(new DashMonthFragment(), "Month");
        adapter.addFragment(new DashYearFragment(), "Year");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
            //return null;
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
