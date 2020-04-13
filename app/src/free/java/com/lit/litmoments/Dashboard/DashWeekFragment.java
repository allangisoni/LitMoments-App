package com.lit.litmoments.Dashboard;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
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
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lit.litmoments.AddJournal.JournalEntryModel;
import com.lit.litmoments.Data.RetrieveData;
import com.lit.litmoments.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DashWeekFragment extends Fragment {

    @BindView(R.id.barEntryChart)
    BarChart barMoodChart;
    @BindView(R.id.PieEntryChart)
    PieChart pieChart;
    @BindView(R.id.tvFavCount)
    TextView tvFavCount;
    //@BindView(R.id.tvpieChartTitle) TextView tvPieChartTitle;
    //  @BindView(R.id.cvPieChart) CardView cvPieChart;

    //  @BindView(R.id.tvBarTitle) TextView tvBarChartTitle;
    //  @BindView(R.id.cvBarChart) CardView cvBarChart;
    // @BindView(R.id.cvTreeChart) CardView cvTreeChart;
    // @BindView(R.id.tvfavoriteplaces) TextView favoritePlaces;
    // @BindView(R.id.tvTreeTitle) TextView favoriteplacesTitle;
    // @BindView(R.id.treeChart)  AnyChartView anyChartView ;
    private ArrayList<JournalEntryModel> journalList = new ArrayList<>();
    private ArrayList<JournalEntryModel> favJournalList = new ArrayList<>();
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private Context mContext;
    public static final String DATABASE_UPLOADS = "User's Journal Entries";
    SharedPreferences sharedPreferences;
    RetrieveData retrieveData ;
    int i = 0;


    public DashWeekFragment(){

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_dash_week_fragment, container,false);

        ButterKnife.bind(this, view);
       // setHasOptionsMenu(true);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        journalList.clear();
         mContext =getContext();
        FirebaseApp.initializeApp(getActivity().getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
        }

        String currentUid = mAuth.getCurrentUser().getUid();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        mDatabase = firebaseDatabase.getReference(DATABASE_UPLOADS).child(currentUid);
        retrieveData = new RetrieveData(mContext);
        //getEntryBarChartData();
        //getEntryPieChartData();
        //getFavoriteCount();
        getData();

        //retrieveData.getData(journalList, mDatabase);
        return view;
    }

    private void getEntryBarChartData( ArrayList<JournalEntryModel> mList){
        ArrayList<BarEntry> mEntryList = new ArrayList();
        ArrayList<JournalEntryModel> journalEntryList = mList;
        int SunCount =0,MonCount =0, TueCount =0, WedCount =0, ThurCount =0, FriCount =0, SatCount =0;

        for (JournalEntryModel model : mList){
            Calendar calendar = Calendar.getInstance();
            String sDate = model.getJournalDate();
            Date date1 = new Date();
            try {
                date1 = new SimpleDateFormat("yyyy-MM-dd").parse(sDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            calendar.setTime(date1);
            if(calendar.get(Calendar.DAY_OF_WEEK) == 2){
                MonCount +=1;
            } else if(calendar.get(Calendar.DAY_OF_WEEK) == 3){
                TueCount +=1;
            }else if(calendar.get(Calendar.DAY_OF_WEEK) == 4){
                WedCount +=1;
            }else if(calendar.get(Calendar.DAY_OF_WEEK) == 5){
                ThurCount +=1;
            }else if(calendar.get(Calendar.DAY_OF_WEEK) == 6){
                FriCount +=1;
            }else if(calendar.get(Calendar.DAY_OF_WEEK) == 7){
                SatCount +=1;
            }else if(calendar.get(Calendar.DAY_OF_WEEK) == 1){
                SunCount +=1;
            }
        }


        mEntryList.add(new BarEntry(0, SunCount));
        mEntryList.add(new BarEntry(1, MonCount));
        mEntryList.add(new BarEntry(2, TueCount));
        mEntryList.add(new BarEntry(3, WedCount));
        mEntryList.add(new BarEntry(4, ThurCount));
        mEntryList.add(new BarEntry(5, FriCount));
        mEntryList.add(new BarEntry(6, SatCount));

        BarDataSet barDataSet = new BarDataSet(mEntryList, "No of Entries");
        BarData data = new BarData(barDataSet);
        barDataSet.setDrawValues(false);
        //data.setValueFormatter(new PercentFormatter());
        barMoodChart.setData(data);
        barMoodChart.invalidate();
        Description description = new Description();
        description.setText(" ");
        description.setTextSize(15f);
        barMoodChart.setDescription(description);
        data.setValueTextSize(13f);
        data.setValueTextColor(getResources().getColor(R.color.colorAccent));
        data.setBarWidth(0.4f);
        barDataSet.setGradientColor(getResources().getColor(R.color.barchartstart),getResources().getColor(R.color.barchartend));
        Legend legend = barMoodChart.getLegend();
        legend.setTextColor(getResources().getColor(R.color.background));
        legend.setEnabled(false);
        barMoodChart.animateY(800);

        ArrayList<String> xLabels = new ArrayList<>();
        xLabels.add("Sun");
        xLabels.add("Mon");
        xLabels.add("Tue");
        xLabels.add("Wed");
        xLabels.add("Thur");
        xLabels.add("Fri");
        xLabels.add("Sat");
        XAxis xAxis = barMoodChart.getXAxis();
        xAxis.setTextColor(getResources().getColor(R.color.colorAccent));
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xLabels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        barMoodChart.setDrawGridBackground(false);
        barMoodChart.getXAxis().setDrawGridLines(false);
        barMoodChart.getAxisLeft().setDrawGridLines(false);
        barMoodChart.getAxisRight().setDrawGridLines(false);
        barMoodChart.getAxisRight().setEnabled(false);


    }

    private void getEntryPieChartData( ArrayList<JournalEntryModel> mList){
        ArrayList<JournalEntryModel> mEntryList = mList;

        ArrayList<JournalEntryModel> mHappyList = new ArrayList<>();
        ArrayList<JournalEntryModel> mSadList = new ArrayList<>();
        ArrayList<JournalEntryModel> mAmusedList = new ArrayList<>();

        if(mEntryList.size() >0) {
            for (JournalEntryModel model : mEntryList) {
                if (model.getJournalMood().toLowerCase().equals("happy")) {
                    mHappyList.add(model);
                    // mAmusedList.add(model);
                    //mSadList.add(model);
                } else if (model.getJournalMood().toLowerCase().equals("sad")) {
                    mSadList.add(model);
                } else if (model.getJournalMood().toLowerCase().equals("amused")) {
                    mAmusedList.add(model);
                } else {

                }
            }
            ArrayList<PieEntry> moodCount = new ArrayList<PieEntry>();
            float happyCount = mHappyList.size();
            float sadCount = mSadList.size();
            float amusedCount = mAmusedList.size();

            if (happyCount != 0) {
                moodCount.add(new PieEntry(happyCount, "Happy"));
            }

            if (sadCount != 0) {
                moodCount.add(new PieEntry(sadCount, "Sad"));
            }

            if (amusedCount != 0) {
                moodCount.add(new PieEntry(amusedCount, "Amused"));
            }


            PieDataSet pieDataSet = new PieDataSet(moodCount, "Moods");
            pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
            ArrayList<String> PieEntryLabels;
            PieEntryLabels = new ArrayList<String>();
            PieEntryLabels.add("Happy");
            PieEntryLabels.add("Sad");

            PieData data = new PieData();
            data.setDataSet(pieDataSet);
            data.setValueFormatter(new PercentFormatter());
            pieChart.setData(data);
            Description description = new Description();
            //description.setText(getString(R.string.piechartMood));
            // description.setPosition(290f, 40f);
            description.setText(" ");
            description.setTextSize(15f);

            pieChart.setDescription(description);
            pieChart.getDescription().setEnabled(false);
            data.setValueTextSize(10f);
            data.setValueTextColor(getResources().getColor(R.color.background));

            pieChart.setDrawEntryLabels(false);
            Legend legend = pieChart.getLegend();
            legend.setTextColor(getResources().getColor(R.color.colorAccent));
            legend.setEnabled(true);
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
            legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            legend.setWordWrapEnabled(true);
            legend.setDrawInside(false);
            legend.setYOffset(5f);
            pieChart.animateXY(1200, 1200);

        }


    }
    private void getData(){
        if(journalList.size() >0) {
            journalList.clear();
        }
        if(favJournalList.size()>0) {
            favJournalList.clear();
        }
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    for (DataSnapshot npsnapshot : dataSnapshot.getChildren()){

                        // JournalEntryModel journalEntryModel=npsnapshot.getValue(JournalEntryModel.class);
                        JournalEntryModel journalEntryModel = new JournalEntryModel();
                        journalEntryModel.setJournalDate(npsnapshot.getValue(JournalEntryModel.class).getJournalDate());
                        journalEntryModel.setJournalLocation(npsnapshot.getValue(JournalEntryModel.class).getJournalLocation());
                        journalEntryModel.setJournalWeather(npsnapshot.getValue(JournalEntryModel.class).getJournalWeather());
                        journalEntryModel.setJournalMood(npsnapshot.getValue(JournalEntryModel.class).getJournalMood());
                        journalEntryModel.setJournalTitle(npsnapshot.getValue(JournalEntryModel.class).getJournalTitle());
                        journalEntryModel.setJournalMessage(npsnapshot.getValue(JournalEntryModel.class).getJournalMessage());
                        journalEntryModel.setMonth(npsnapshot.getValue(JournalEntryModel.class).getMonth());
                        journalEntryModel.setDay(npsnapshot.getValue(JournalEntryModel.class).getDay());
                        journalEntryModel.setKey(npsnapshot.getValue(JournalEntryModel.class).getKey());
                        if(npsnapshot.getValue(JournalEntryModel.class).getIsFavorite() != null)
                        {
                            journalEntryModel.setIsFavorite(npsnapshot.getValue(JournalEntryModel.class).getIsFavorite());
                        } else {
                            journalEntryModel.setIsFavorite("False");
                        }
                        journalEntryModel.setJournalImagePath(npsnapshot.getValue(JournalEntryModel.class).getJournalImagePath());
                        // journalEntryModel.setJournalDate(npsnapshot.child());

                        /** JournalEntryModel journalEntryModel= new JournalEntryModel(npsnapshot.getValue(JournalEntryModel.class).getJournalDate(),
                         npsnapshot.getValue(JournalEntryModel.class).getJournalLocation(),
                         npsnapshot.getValue(JournalEntryModel.class).getJournalWeather(),
                         npsnapshot.getValue(JournalEntryModel.class).getJournalMood(),
                         npsnapshot.getValue(JournalEntryModel.class).getJournalTitle(),
                         npsnapshot.getValue(JournalEntryModel.class).getJournalMessage(),
                         npsnapshot.getValue(JournalEntryModel.class).getMonth(),
                         npsnapshot.getValue(JournalEntryModel.class).getDay(),
                         npsnapshot.getValue(JournalEntryModel.class).getKey(),
                         npsnapshot.getValue(JournalEntryModel.class).getIsFavorite()
                         ); **/

                        Log.d("dashboard" , journalEntryModel.getJournalTitle());
                        journalList.add(journalEntryModel);
                        if(journalEntryModel.getIsFavorite().equals("True")) {
                            // favJournalList.add(journalEntryModel);
                        }
                        //Toast.makeText(mContext, " "+ journalList.size(), Toast.LENGTH_SHORT).show();

                    }
                    Calendar calendar = Calendar.getInstance();
                    Calendar calendar1 = Calendar.getInstance();
                    ArrayList<JournalEntryModel> mEntryList = new ArrayList<>();
                    for (JournalEntryModel journalEntryModel: journalList){
                        String sDate = journalEntryModel.getJournalDate();
                        Date date1 = new Date();
                        try {
                            date1 = new SimpleDateFormat("yyyy-MM-dd").parse(sDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        int week = calendar.get(Calendar.WEEK_OF_YEAR);
                        calendar1.setTime(date1);
                        int mJournalWeek = calendar1.get(Calendar.WEEK_OF_YEAR);
                        if(week == mJournalWeek){
                            mEntryList.add(journalEntryModel);
                            if(journalEntryModel.getIsFavorite().equals("True")){
                                favJournalList.add(journalEntryModel);
                            }
                        }
                    }
                    getEntryPieChartData(mEntryList);
                    getEntryBarChartData(mEntryList);

                    tvFavCount.setText(Integer.toString(favJournalList.size()));
                    //journalAdapter.notifyDataSetChanged();

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // progressDialog.dismiss();
                //progressBar.setVisibility(View.INVISIBLE);

                Toast.makeText(mContext, "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
            }

        });


    }
    private void getFavoriteCount(){
        //int favCount=0;
      //  getFavoritesCount();
       // Toast.makeText(getContext(), " Count is"+ favCount, Toast.LENGTH_SHORT).show();



    }

    private   void  getFavoritesCount(){
        favJournalList.clear();
        i=0;

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    for (DataSnapshot npsnapshot : dataSnapshot.getChildren()){

                        // JournalEntryModel journalEntryModel=npsnapshot.getValue(JournalEntryModel.class);
                        JournalEntryModel journalEntryModel = new JournalEntryModel();
                        journalEntryModel.setJournalDate(npsnapshot.getValue(JournalEntryModel.class).getJournalDate());
                        journalEntryModel.setJournalLocation(npsnapshot.getValue(JournalEntryModel.class).getJournalLocation());
                        journalEntryModel.setJournalWeather(npsnapshot.getValue(JournalEntryModel.class).getJournalWeather());
                        journalEntryModel.setJournalMood(npsnapshot.getValue(JournalEntryModel.class).getJournalMood());
                        journalEntryModel.setJournalTitle(npsnapshot.getValue(JournalEntryModel.class).getJournalTitle());
                        journalEntryModel.setJournalMessage(npsnapshot.getValue(JournalEntryModel.class).getJournalMessage());
                        journalEntryModel.setMonth(npsnapshot.getValue(JournalEntryModel.class).getMonth());
                        journalEntryModel.setDay(npsnapshot.getValue(JournalEntryModel.class).getDay());
                        journalEntryModel.setKey(npsnapshot.getValue(JournalEntryModel.class).getKey());
                        if(npsnapshot.getValue(JournalEntryModel.class).getIsFavorite() != null)
                        {
                            journalEntryModel.setIsFavorite(npsnapshot.getValue(JournalEntryModel.class).getIsFavorite());
                        } else {
                            journalEntryModel.setIsFavorite("False");
                        }
                        journalEntryModel.setJournalImagePath(npsnapshot.getValue(JournalEntryModel.class).getJournalImagePath());
                        // journalEntryModel.setJournalDate(npsnapshot.child());


                        if(journalEntryModel.getIsFavorite().equals("True")) {
                           // favJournalList.add(journalEntryModel);
                            i = favJournalList.size();
                        }
                        //i +=1;

                        //Toast.makeText(mContext, " "+ list.size(), Toast.LENGTH_SHORT).show();
                        /**  Collections.sort(list, new Comparator<JournalEntryModel>() {
                         public int compare(JournalEntryModel o1, JournalEntryModel o2) {
                         SimpleDateFormat formatter2=new SimpleDateFormat("yyyy-MM-dd");
                         int result =0;
                         if (o1.getJournalDate() == null || o2.getJournalDate() == null)
                         result= 0;

                         try {
                         result=formatter2.parse(o2.getJournalDate()).compareTo(formatter2.parse(o1.getJournalDate()));
                         } catch (ParseException e) {
                         e.printStackTrace();
                         result = 0;
                         }

                         return result;
                         }
                         }); **/


                    }
                   // tvFavCount.setText(Integer.toString(favJournalList.size()));
                    //Toast.makeText(getContext(), " Count is"+ favJournalList.size(), Toast.LENGTH_SHORT).show();


                    //journalAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // progressDialog.dismiss();
                //progressBar.setVisibility(View.INVISIBLE);

                Toast.makeText(mContext, "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
            }


        });
    }




    public void setTripCount(){


        ArrayList<PieEntry> numoftrips = new ArrayList();
        int numoftrips2018 =0 ;
        Integer numoftrips2019;

        numoftrips2019 = journalList.size();


        numoftrips.add(new PieEntry(numoftrips2018, "2018",0));
        numoftrips.add(new PieEntry(numoftrips2019, "2019",1));

        PieDataSet pieDataSet = new PieDataSet(numoftrips, "Year");


        PieData data = new PieData(pieDataSet);
        data.setValueFormatter(new PercentFormatter());
        // pieChart.setData(data);
        Description description = new Description();
        //description.setText(getString(R.string.piechartMood));
        // description.setPosition(290f, 40f);
        description.setText(" ");
        description.setTextSize(15f);
        //pieChart.setDescription(description);
        data.setValueTextSize(13f);
        data.setValueTextColor(Color.DKGRAY);
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        //Legend legend = pieChart.getLegend();
        //  legend.setTextColor(getResources().getColor(R.color.background));
        // pieChart.animateXY(1200, 1200);


    }



    public void setMoodBarChart(){
        ArrayList<BarEntry> numofMoods = new ArrayList();
        ArrayList<String> happyListcount = new ArrayList<>();
        ArrayList<String> sadListcount = new ArrayList<>();
        ArrayList<String> amusedListcount = new ArrayList<>();

        for(JournalEntryModel journalEntryModel : journalList){
            String moodType = journalEntryModel.getJournalMood();
            if(moodType.toLowerCase().equalsIgnoreCase("happy")) {
                happyListcount.add(journalEntryModel.getJournalMood());
            }else if(moodType.toLowerCase().equalsIgnoreCase("sad")) {
                sadListcount.add(journalEntryModel.getJournalMood());
            } else if(moodType.toLowerCase().equalsIgnoreCase("amused")) {
                amusedListcount.add(journalEntryModel.getJournalMood());
            }
        }

        numofMoods.add(new BarEntry(0, happyListcount.size()));
        numofMoods.add(new BarEntry(1, sadListcount.size()));
        numofMoods.add(new BarEntry(2, amusedListcount.size()));

        BarDataSet barDataSet = new BarDataSet(numofMoods, "Mood Type");

        BarData data = new BarData(barDataSet);
        //data.setValueFormatter(new PercentFormatter());
        barMoodChart.setData(data);
        barMoodChart.invalidate();
        Description description = new Description();
        //description.setText(getString(R.string.piechartMood));
        // description.setPosition(290f, 40f);
        description.setText(" ");
        description.setTextSize(15f);
        barMoodChart.setDescription(description);
        data.setValueTextSize(13f);
        data.setValueTextColor(getResources().getColor(R.color.background));
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        Legend legend = barMoodChart.getLegend();
        legend.setTextColor(getResources().getColor(R.color.background));
        barMoodChart.animateY(800);

        ArrayList<String> xLabels = new ArrayList<>();
        xLabels.add("Happy");
        xLabels.add("Sad");
        xLabels.add("Amused");
        XAxis xAxis = barMoodChart.getXAxis();
        xAxis.setTextColor(getResources().getColor(R.color.background));
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xLabels));

        YAxisRenderer yAxis = barMoodChart.getRendererLeftYAxis();
        barMoodChart.getAxisLeft().setTextColor(getResources().getColor(R.color.background));
        barMoodChart.getAxisRight().setTextColor(getResources().getColor(R.color.background));

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
                placesfre.add(place + " " +" (" + Collections.frequency(locations, place) + ")");
            }
            //Log.d("placesfreq", placesfre.get(count));
            //count++;
        }

        placesfre = removeDuplicates(placesfre);
        Log.d("placesfreq", placesfre.toString());
        if(placesfre.size() >5){
            for (int i=0; i<6;i++){

                //  favoritePlaces.append(placesfre.get(i));
                //  favoritePlaces.append("\n");
                // favoritePlaces.append("\n");
            }
        } else {
            for (int i=0; i<placesfre.size();i++){
                //  favoritePlaces.append(placesfre.get(i));
                // favoritePlaces.append("\n");
                //  favoritePlaces.append("\n");
            }
        }

        YoYo.with(Techniques.SlideInUp)
                .duration(2000)
                .repeat(0);
        //   .playOn(favoritePlaces);

    }

    public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list)
    {

        // Create a new ArrayList
        ArrayList<T> newList = new ArrayList<T>();

        // Traverse through the first list
        for (T element : list) {

            // If this element is not present in newList
            // then add it
            if (!newList.contains(element)) {

                newList.add(element);
            }
        }

        // return the new list
        return newList;
    }
}
