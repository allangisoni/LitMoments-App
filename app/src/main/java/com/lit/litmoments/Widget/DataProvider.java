package com.lit.litmoments.Widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class DataProvider implements RemoteViewsService.RemoteViewsFactory{

    List<JournalEntryModel> myListView = new ArrayList<>();
    Context mContext = null;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    public static final String DATABASE_UPLOADS = "User's Journal Entries";

    public DataProvider(Context context, Intent intent) {
        mContext = context;


    }

    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {
         initData();

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
       // Toast.makeText(mContext,""+journalEntryModel.getJournalTitle(), Toast.LENGTH_SHORT).show();
        Log.d("jornalTitle", ""+ journalEntryModel.getJournalTitle());
        view.setTextViewText(R.id.tvTitle, journalEntryModel.getJournalTitle());
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

        }

        String currentUid = mAuth.getCurrentUser().getUid();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        mDatabase = firebaseDatabase.getReference(DATABASE_UPLOADS).child(currentUid);

        mDatabase.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //myListView.clear();
                if (dataSnapshot.exists()){

                    for (DataSnapshot npsnapshot : dataSnapshot.getChildren()){



                        JournalEntryModel journalEntryModel = npsnapshot.getValue(JournalEntryModel.class);
                        Log.d("nsnapshot", ""+ journalEntryModel.getJournalTitle());

                         JournalEntryModel model = new JournalEntryModel();
                         model.setJournalDate(npsnapshot.child("journalDate").getValue().toString());
                         model.setJournalTitle(npsnapshot.child("journalTitle").getValue().toString());
                         myListView.add(journalEntryModel);
                        //Log.d("jornalSize", ""+ myListView.size());




                       /* Collections.sort(myListView, new Comparator<JournalEntryModel>() {
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
                        }); */

                        // Collections.reverse(journalList);
                        if(Build.VERSION.SDK_INT > 24) {
                            //  journalList.sort(Comparator.comparing(o -> o.getTimestampCreatedLong()));
                        }
                    }



                    //journalAdapter.notifyDataSetChanged();

                } else{
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



    }
}
