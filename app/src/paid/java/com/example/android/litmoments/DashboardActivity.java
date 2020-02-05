package com.example.android.litmoments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.ajts.androidmads.fontutils.FontUtils;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.android.litmoments.AddJournal.JournalEntryModel;
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

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DashboardActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = DashboardActivity.class.getName();
    @BindView(R.id.entrytoolbar) Toolbar entryToolbar;
    @BindView(R.id.pieChart) PieChart pieChart;
    @BindView(R.id.tvpieChartTitle) TextView tvPieChartTitle;
    @BindView(R.id.cvPieChart) CardView cvPieChart;
    @BindView(R.id.barChart) BarChart barChart;
    @BindView(R.id.tvBarTitle) TextView tvBarChartTitle;
    @BindView(R.id.cvBarChart) CardView cvBarChart;
    @BindView(R.id.cvTreeChart) CardView cvTreeChart;
    @BindView(R.id.tvfavoriteplaces) TextView favoritePlaces;
    @BindView(R.id.tvTreeTitle) TextView favoriteplacesTitle;
   // @BindView(R.id.treeChart)  AnyChartView anyChartView ;
    private ArrayList<JournalEntryModel> journalList = new ArrayList<>();

    Boolean isWhite = false, isLit = false , isDark = false;

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
            setUpThemeContent();
            loadWidgetColors(sharedPreferences);

        }
       if(getIntent().getSerializableExtra("JournalList") != null){
           journalList =(ArrayList<JournalEntryModel>) getIntent().getSerializableExtra("JournalList");
       } else{
           Log.d("JournalItems", Integer.toString(journalList.size()));
       }

        tvPieChartTitle.setTextColor(getResources().getColor(R.color.bluecolorPrimary));
        tvPieChartTitle.setTextSize(16f);
        tvBarChartTitle.setTextSize(16f);
        favoriteplacesTitle.setTextSize(16f);
        favoritePlaces.setTextSize(14f);
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


        setPieChartData();
        setBarChartData();
        getFavoritePlaces();


    }

    public void setPieChartData() {
        ArrayList<PieEntry> numofmood = new ArrayList();

        int numofHappyMoods = 0;
        int numofSadMoods = 0, numofDreamyMoods=0, numofAmusedMoods=0, numofRomanticMoods=0, numofMysteMoods=0;

        for(JournalEntryModel journalEntryModel : journalList){
            String moodType = journalEntryModel.getJournalMood();
            if(moodType.toLowerCase().equalsIgnoreCase("happy")){
                numofHappyMoods = numofHappyMoods + 1;
            } else if(moodType.toLowerCase().equalsIgnoreCase("sad")){
                numofSadMoods = numofSadMoods + 1;
            }else if(moodType.toLowerCase().equalsIgnoreCase("Amused")){
                numofAmusedMoods = numofAmusedMoods + 1;
            }else if(moodType.toLowerCase().equalsIgnoreCase("Dreamy")){
                numofDreamyMoods = numofDreamyMoods + 1;
            }else if(moodType.toLowerCase().equalsIgnoreCase("Mysterious")){
                numofMysteMoods = numofMysteMoods + 1;
            }else if(moodType.toLowerCase().equalsIgnoreCase("Romantic")){
                numofRomanticMoods = numofRomanticMoods + 1;
            }

            else{

            }
        }

    /**    if(numofAmusedMoods!=0){
            numofmood.add(new PieEntry(numofAmusedMoods, "Amused",2));
        } else if(numofSadMoods != 0 ){
            numofmood.add(new PieEntry(numofSadMoods, "Sad",1));
        }else if(numofDreamyMoods != 0 ){
            numofmood.add(new PieEntry(numofDreamyMoods, "Dreamy",3));
        }else if(numofMysteMoods != 0 ){
            numofmood.add(new PieEntry(numofMysteMoods, "Mystery",4));
        }else if(numofRomanticMoods != 0 ){
            numofmood.add(new PieEntry(numofRomanticMoods, "Romantic",5));
        }else if(numofHappyMoods != 0 ){
            numofmood.add(new PieEntry(numofHappyMoods, "Happy",0));
        }
     **/

        numofmood.add(new PieEntry(numofHappyMoods, "Happy",0));
        numofmood.add(new PieEntry(numofSadMoods, "Sad",1));
        numofmood.add(new PieEntry(numofAmusedMoods, "Amused",2));
        numofmood.add(new PieEntry(numofDreamyMoods, "Dreamy",3));
        numofmood.add(new PieEntry(numofMysteMoods, "Mystery",4));
        numofmood.add(new PieEntry(numofRomanticMoods, "Romantic",5));

        PieDataSet pieDataSet = new PieDataSet(numofmood, "Mood Type");


        PieData data = new PieData(pieDataSet);
        data.setValueFormatter(new PercentFormatter());
        pieChart.setData(data);

        Description description = new Description();
        //description.setText(getString(R.string.piechartMood));
      // description.setPosition(290f, 40f);
        description.setText(" ");
        description.setTextSize(15f);
        pieChart.setDescription(description);
        data.setValueTextSize(13f);
        data.setValueTextColor(Color.DKGRAY);
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        Legend legend = pieChart.getLegend();
        legend.setTextColor(getResources().getColor(R.color.background));
        pieChart.animateXY(1200, 1200);

    }


    public void setBarChartData() {
        ArrayList<BarEntry> numofVisits = new ArrayList();
        ArrayList<String> places = new ArrayList();
         ArrayList<String> placesVisited = new ArrayList<>();

        for(JournalEntryModel journalEntryModel : journalList){
            String location = journalEntryModel.getJournalLocation();
         if(location != null){
             if(!placesVisited.contains(location.toLowerCase().trim())){
                 placesVisited.add(location.toLowerCase().trim());
             }
         }
        }

        int count =0;

      /** for (String place : placesVisited){

          numofVisits.add(new BarEntry(count++,count));
          places.add(place);
          count ++;
       } **/
          numofVisits.add(new BarEntry(0, 0));
          numofVisits.add(new BarEntry(1, 0));
          numofVisits.add(new BarEntry(2,journalList.size()));
      //  numofmood.add(new PieEntry(numofHappyMoods, "Happy",0));
        //numofmood.add(new PieEntry(numofSadMoods, "Sad",1));

        BarDataSet barDataSet = new BarDataSet(numofVisits, "Years");



        BarData data = new BarData(barDataSet);
        //data.setValueFormatter(new PercentFormatter());
        barChart.setData(data);
        barChart.invalidate();
        Description description = new Description();
        //description.setText(getString(R.string.piechartMood));
        // description.setPosition(290f, 40f);
        description.setText(" ");
        description.setTextSize(15f);
        barChart.setDescription(description);
        data.setValueTextSize(13f);
        data.setValueTextColor(getResources().getColor(R.color.background));
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        Legend legend = barChart.getLegend();
        legend.setTextColor(getResources().getColor(R.color.background));
        barChart.animateY(800);

        ArrayList<String> xLabels = new ArrayList<>();
        xLabels.add("2017");
        xLabels.add("2018");
        xLabels.add("2019");
        XAxis xAxis = barChart.getXAxis();
        xAxis.setTextColor(getResources().getColor(R.color.background));
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xLabels));

        YAxisRenderer yAxis = barChart.getRendererLeftYAxis();
        barChart.getAxisLeft().setTextColor(getResources().getColor(R.color.background));
        barChart.getAxisRight().setTextColor(getResources().getColor(R.color.background));


    }

   public void getFavoritePlaces(){

        ArrayList<String> locations= new ArrayList<>();

       for(JournalEntryModel journalEntryModel : journalList){
           String location = journalEntryModel.getJournalLocation();
           locations.add(location);

       }
    ArrayList<String> placesfre = new ArrayList<>();

       int count =0;
      // locations = removeDuplicates(locations);

     for(String place : locations){
             if(!place.equals(" ") && !place.isEmpty() && place.trim() != "" ) {
                 placesfre.add(place + " " +" (" +Collections.frequency(locations, place) + ")");
             }
             //Log.d("placesfreq", placesfre.get(count));
             //count++;
     }

       placesfre = removeDuplicates(placesfre);
       Log.d("placesfreq", placesfre.toString());
      if(placesfre.size() >5){
       for (int i=0; i<6;i++){

           favoritePlaces.append(placesfre.get(i));
           favoritePlaces.append("\n");
           favoritePlaces.append("\n");
       }
      } else {
          for (int i=0; i<placesfre.size();i++){
              favoritePlaces.append(placesfre.get(i));
              favoritePlaces.append("\n");
              favoritePlaces.append("\n");
          }
      }

       YoYo.with(Techniques.SlideInUp)
               .duration(2000)
               .repeat(0)
               .playOn(favoritePlaces);

   }

    public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list)
    {

        // Create a new ArrayList
        ArrayList<T> newList = new ArrayList<T>();

        // Traverse through the first list
        for (T element : list) {

            // If this element is not present in newList
            // then add it
            if (!newList.contains(element)){

                newList.add(element);
            }
        }

        // return the new list
        return newList;
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
            fontUtils.applyFontToView(tvPieChartTitle, myCustomFont);
            fontUtils.applyFontToView(tvBarChartTitle, myCustomFont);
            fontUtils.applyFontToView(favoritePlaces, myCustomFont);
            fontUtils.applyFontToView(favoriteplacesTitle, myCustomFont);

        } else if(selectedFont.equals("1")){
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.patrick_hand_sc);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(entryToolbar, myCustomFont);
            fontUtils.applyFontToView(tvPieChartTitle, myCustomFont);
            fontUtils.applyFontToView(tvBarChartTitle, myCustomFont);
            fontUtils.applyFontToView(favoritePlaces, myCustomFont);
            fontUtils.applyFontToView(favoriteplacesTitle, myCustomFont);
        } else if( selectedFont.equals("2")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.sofadi_one);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(entryToolbar, myCustomFont);
            fontUtils.applyFontToView(tvPieChartTitle, myCustomFont);
            fontUtils.applyFontToView(tvBarChartTitle, myCustomFont);
            fontUtils.applyFontToView(favoritePlaces, myCustomFont);
            fontUtils.applyFontToView(favoriteplacesTitle, myCustomFont);
        } else if( selectedFont.equals("4")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.concert_one);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(entryToolbar, myCustomFont);
            fontUtils.applyFontToView(tvPieChartTitle, myCustomFont);
            fontUtils.applyFontToView(tvBarChartTitle, myCustomFont);
            fontUtils.applyFontToView(favoritePlaces, myCustomFont);
            fontUtils.applyFontToView(favoriteplacesTitle, myCustomFont);

        }
        else if( selectedFont.equals("5")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.oleo_script);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(entryToolbar, myCustomFont);
            fontUtils.applyFontToView(tvPieChartTitle, myCustomFont);
            fontUtils.applyFontToView(tvBarChartTitle, myCustomFont);
            fontUtils.applyFontToView(favoritePlaces, myCustomFont);
            fontUtils.applyFontToView(favoriteplacesTitle, myCustomFont);
        }
        else if( selectedFont.equals("6")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.pt_sans_narrow);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(entryToolbar, myCustomFont);
            fontUtils.applyFontToView(tvPieChartTitle, myCustomFont);
            fontUtils.applyFontToView(tvBarChartTitle, myCustomFont);
            fontUtils.applyFontToView(favoritePlaces, myCustomFont);
            fontUtils.applyFontToView(favoriteplacesTitle, myCustomFont);

        }  else if( selectedFont.equals("7")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.roboto_condensed_light);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(entryToolbar, myCustomFont);
            fontUtils.applyFontToView(tvPieChartTitle, myCustomFont);
            fontUtils.applyFontToView(tvBarChartTitle, myCustomFont);
            fontUtils.applyFontToView(favoritePlaces, myCustomFont);
            fontUtils.applyFontToView(favoriteplacesTitle, myCustomFont);
        }  else if( selectedFont.equals("8")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.shadows_into_light);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(entryToolbar, myCustomFont);
            fontUtils.applyFontToView(tvPieChartTitle, myCustomFont);
            fontUtils.applyFontToView(tvBarChartTitle, myCustomFont);
            fontUtils.applyFontToView(favoritePlaces, myCustomFont);
            fontUtils.applyFontToView(favoriteplacesTitle, myCustomFont);
        } else if( selectedFont.equals("9")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.slabo_13px);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(entryToolbar, myCustomFont);
            fontUtils.applyFontToView(tvPieChartTitle, myCustomFont);
            fontUtils.applyFontToView(tvBarChartTitle, myCustomFont);
            fontUtils.applyFontToView(favoritePlaces, myCustomFont);
            fontUtils.applyFontToView(favoriteplacesTitle, myCustomFont);
        }else {

        }
    }
    private void loadUiTheme(SharedPreferences sharedPreferences){

        String userTheme = sharedPreferences.getString(getString(R.string.key_uiTheme), "2");
        if (userTheme.equals("2")) {
            setTheme(R.style.LitStyle);
           isWhite = false;
           isDark=false;
        }
        else if (userTheme.equals("1")) {
            setTheme(R.style.ReddishLitStyle);
            isWhite = true;
            isDark=false;

            //isLit = true;

        }
        else if (userTheme.equals("0")) {
            setTheme(R.style.BlueLitStyle);
            isWhite = true;
            isDark=false;

            //isLit =  false;
        } else if (userTheme.equals("3")) {
            setTheme(R.style.YellowLitStyle);
            isWhite=false;
            isDark=false;
        } else if (userTheme.equals("4")) {
            setTheme(R.style.BluishLitStyle);
            isWhite=true;
            isDark=false;
        } else if (userTheme.equals("5")) {
            setTheme(R.style.GreenishLitStyle);
            isWhite=false;
            isDark=false;
        } else if (userTheme.equals("6")) {
            setTheme(R.style.TacaoLitStyle);
            isWhite=false;
            isDark=false;
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



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
