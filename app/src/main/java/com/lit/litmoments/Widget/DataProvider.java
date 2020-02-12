package com.lit.litmoments.Widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import com.google.common.collect.ImmutableList;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lit.litmoments.AddJournal.JournalEntryModel;
import com.lit.litmoments.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class DataProvider implements RemoteViewsService.RemoteViewsFactory{

    List<JournalEntryModel> myListView ;
    Context mContext = null;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    public static final String DATABASE_UPLOADS = "User's Journal Entries";

    private CountDownLatch mCountDownLatch;

    public DataProvider(Context context, Intent intent) {
        mContext = context;
        myListView = new ArrayList<>();

    }

    @Override
    public void onCreate() {

        //initData();
    }

    @Override
    public void onDataSetChanged() {
         //initData();
        mCountDownLatch = new CountDownLatch(1);
       initData();
        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        Log.d("jornalSize", ""+ myListView.size());
        return myListView.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews view = new RemoteViews(mContext.getPackageName(),
                R.layout.collection_widget_list_item);


        JournalEntryModel journalEntryModel = myListView.get(position);
        Log.d("jornalTitle", ""+ journalEntryModel.getJournalTitle());
        view.setTextViewText(R.id.tvTitle, journalEntryModel.getJournalTitle());
        String myDate = journalEntryModel.getJournalDate();
        if(!myDate.isEmpty()) {
            String monthName, day, year;
            monthName = journalEntryModel.getMonth();
            try {
                monthName = formatMonth(monthName);


            } catch (Exception e) {

            }
            day = journalEntryModel.getDay();
            year = myDate.substring(0, 4);

            view.setTextViewText(R.id.tvMonth, monthName);
            view.setTextViewText(R.id.tvDay, day);
            view.setTextViewText(R.id.tvYear, year);

        } else {
            view.setTextViewText(R.id.tvMonth, "December");
            view.setTextViewText(R.id.tvDay,"28" );
            view.setTextViewText(R.id.tvYear, "2019");

        }

        view.setTextViewText(R.id.tvMessage, journalEntryModel.getJournalMessage());
        view.setTextViewText(R.id.tvLocation, journalEntryModel.getJournalLocation());
        if(journalEntryModel.getJournalImagePath().size() !=0) {
            try {
                Bitmap bitmap = Picasso.with(mContext).load(journalEntryModel.getJournalImagePath().get(0)).get();
                view.setImageViewBitmap(R.id.ivJournalImage,bitmap );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return view;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void initData() {
        if(myListView.size() > 0){
            myListView.clear();
        }


        FirebaseApp.initializeApp(mContext);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {


            String currentUid = mAuth.getCurrentUser().getUid();
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            //firebaseDatabase.setPersistenceEnabled(true);
            mDatabase = firebaseDatabase.getReference(DATABASE_UPLOADS).child(currentUid);

            mDatabase.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //myListView.clear();



                    if (dataSnapshot.exists()) {


                        if (mCountDownLatch.getCount() == 0) {
                            // Item changed externally. Initiate refresh.
                            Intent updateWidgetIntent = new Intent(mContext,
                                    CollectionWidget.class);
                            updateWidgetIntent.setAction(
                                    CollectionWidget.ACTION_DATA_UPDATED);
                            mContext.sendBroadcast(updateWidgetIntent);

                        } else {
                            if(dataSnapshot.exists()){

                        for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {

                            JournalEntryModel journalEntryModel = npsnapshot.getValue(JournalEntryModel.class);
                            Log.d("nsnapshot", "" + journalEntryModel.getJournalTitle());

                            JournalEntryModel model = new JournalEntryModel();
                            model.setJournalDate(npsnapshot.child("journalDate").getValue().toString());
                            model.setJournalTitle(npsnapshot.child("journalTitle").getValue().toString());
                            myListView.add(journalEntryModel);
                            //Log.d("jornalSize", ""+ myListView.size());

                            Collections.sort(myListView, new Comparator<JournalEntryModel>() {
                                public int compare(JournalEntryModel o1, JournalEntryModel o2) {
                                    SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
                                    int result = 0;
                                    if (o1.getJournalDate() == null || o2.getJournalDate() == null)
                                        result = 0;

                                    try {
                                        result = formatter2.parse(o2.getJournalDate()).compareTo(formatter2.parse(o1.getJournalDate()));

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                        result = 0;
                                    }

                                    return result;
                                }
                            });
                        }
                            mCountDownLatch.countDown();
                        }



                        }


                        //journalAdapter.notifyDataSetChanged();

                    } else {
                        JournalEntryModel journalEntryMode = new JournalEntryModel();
                        journalEntryMode.setJournalDate("2019-08-02");
                        journalEntryMode.setJournalLocation("Lit Place");
                        journalEntryMode.setJournalMood("Happy");
                        journalEntryMode.setJournalWeather("Sunny");
                        //journalEntryMode.setJournalTitle("Welcome" + " " + mAuth.getCurrentUser().getDisplayName());
                        journalEntryMode.setJournalTitle("Get started with Lit Moments");
                        journalEntryMode.setJournalMessage("Capture, store and share your Lit Moments with loved ones. Don't forget to check out your Lit board!!!! ");
                        journalEntryMode.setDay("02");
                        journalEntryMode.setMonth("08");
                        //journalEntryMode.setTime(ServerValue.TIMESTAMP);
                        ArrayList<String> imageurl = new ArrayList<>();
                        imageurl.add("https://firebasestorage.googleapis.com/v0/b/litmoments-de630.appspot.com/o/uploads%2F1564700689347.null?alt=media&token=a24fe027-4b6d-44eb-8951-185691450548");
                        journalEntryMode.setJournalImagePath(imageurl);
                        myListView.add(journalEntryMode);
                        //journalAdapter.notifyDataSetChanged();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // progressDialog.dismiss();
                    // progressBar.setVisibility(View.INVISIBLE);

                    // Toast.makeText(MainActivity.this, "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
                }


            });

        } else {
            mCountDownLatch.countDown();
           // Intent intent = new Intent(mContext, AuthActivity.class);
          //  mContext.startActivity(intent);
        }

    }


    public String formatMonth(String month) throws ParseException {
        SimpleDateFormat monthParse = new SimpleDateFormat("MM");
        SimpleDateFormat monthDisplay = new SimpleDateFormat("MMMM");
        return monthDisplay.format(monthParse.parse(month));
    }
}
