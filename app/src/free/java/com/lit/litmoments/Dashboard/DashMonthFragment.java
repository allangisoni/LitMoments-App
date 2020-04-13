package com.lit.litmoments.Dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DashMonthFragment extends Fragment {


    @BindView(R.id.barEntryChart)
    BarChart barMoodChart;
    @BindView(R.id.PieEntryChart)
    PieChart pieChart;
    @BindView(R.id.tvFavCount)
    TextView tvFavCount;

    private ArrayList<JournalEntryModel> journalList = new ArrayList<>();
    private ArrayList<JournalEntryModel> favJournalList = new ArrayList<>();
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private Context mContext;
    public static final String DATABASE_UPLOADS = "User's Journal Entries";
    SharedPreferences sharedPreferences;
    RetrieveData retrieveData ;
    int i = 0;


    public DashMonthFragment(){

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_dash_month_fragment, container,false);

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
        int JanCount =0,FebCount =0, MarCount =0, AprCount =0, MayCount =0, JunCount =0, JulCount =0,
        AugCount = 0, SepCount=0, OctCount=0, NovCount=0, DecCount=0;

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
            if(calendar.get(Calendar.MONTH) == 0){
                JanCount +=1;
            }
            else if(calendar.get(Calendar.MONTH) == 1){
                FebCount +=1;
            }
            else if(calendar.get(Calendar.MONTH) == 2){
                MarCount +=1;
            }
            else if(calendar.get(Calendar.MONTH) == 3){
                AprCount +=1;
            }else if(calendar.get(Calendar.MONTH) == 4){
                MayCount +=1;
            }else if(calendar.get(Calendar.MONTH) == 5){
                JunCount +=1;
            }else if(calendar.get(Calendar.MONTH) == 6){
                JulCount +=1;
            }else if(calendar.get(Calendar.MONTH) == 7){
                AugCount +=1;
            }else if(calendar.get(Calendar.MONTH) == 8){
                SepCount +=1;
            }
            else if(calendar.get(Calendar.MONTH) == 9){
                OctCount +=1;
            }
            else if(calendar.get(Calendar.MONTH) == 10){
                NovCount +=1;
            }
            else if(calendar.get(Calendar.MONTH) == 11){
                DecCount +=1;
            }

        }


        mEntryList.add(new BarEntry(0, JanCount));
        mEntryList.add(new BarEntry(1, FebCount));
        mEntryList.add(new BarEntry(2, MarCount));
        mEntryList.add(new BarEntry(3, AprCount));
        mEntryList.add(new BarEntry(4, MayCount));
        mEntryList.add(new BarEntry(5, JunCount));
        mEntryList.add(new BarEntry(6, JulCount));
        mEntryList.add(new BarEntry(7, AugCount));
        mEntryList.add(new BarEntry(8, SepCount));
        mEntryList.add(new BarEntry(9, OctCount));
        mEntryList.add(new BarEntry(10, NovCount));
        mEntryList.add(new BarEntry(11, DecCount));

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
        xLabels.add("Jan");
        xLabels.add("Feb");
        xLabels.add("Mar");
        xLabels.add("Apr");
        xLabels.add("May");
        xLabels.add("Jun");
        xLabels.add("Jul");
        xLabels.add("Aug");
        xLabels.add("Sep");
        xLabels.add("Oct");
        xLabels.add("Nov");
        xLabels.add("Dec");


        XAxis xAxis = barMoodChart.getXAxis();
        xAxis.setTextColor(getResources().getColor(R.color.colorAccent));
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xLabels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(xLabels.size());
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
            ArrayList<PieEntry> moodCount = new ArrayList();
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


            PieDataSet pieDataSet = new PieDataSet(moodCount, "");
            pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
            ArrayList<String> PieEntryLabels;
            PieEntryLabels = new ArrayList<String>();
            PieEntryLabels.add("Happy");
            PieEntryLabels.add("Sad");

            PieData data = new PieData(pieDataSet);
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
            data.setValueTextColor(Color.DKGRAY);

            pieChart.setDrawEntryLabels(false);
            Legend legend = pieChart.getLegend();
            legend.setTextColor(getResources().getColor(R.color.colorAccent));
            legend.setEnabled(true);
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
                           //  favJournalList.add(journalEntryModel);
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
                        int year = calendar.get(Calendar.YEAR);
                        calendar1.setTime(date1);
                        int mJournalYear = calendar1.get(Calendar.YEAR);
                        if(year == mJournalYear){
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

}
