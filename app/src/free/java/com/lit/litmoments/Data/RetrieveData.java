package com.lit.litmoments.Data;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lit.litmoments.AddJournal.JournalEntryModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public  class RetrieveData {

    int i = 0;
    private  Context mContext;
    private ProgressBar progressBar;
    public static final String DATABASE_UPLOADS = "User's Journal Entries";
    private ArrayList<JournalEntryModel> journalList = new ArrayList<>();

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    public  RetrieveData(Context context){
        this.mContext = context;
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
        }

        String currentUid = mAuth.getCurrentUser().getUid();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        mDatabase = firebaseDatabase.getReference(DATABASE_UPLOADS).child(currentUid);

    }


    public   void  getData( ArrayList<JournalEntryModel> list, DatabaseReference mDatabase){
        //list.clear();
        //final ProgressDialog progressDialog = new ProgressDialog(this);
        //progressDialog.setMessage("Please wait...........");
        // progressDialog.setTitle("Retrieving data");
        // progressDialog.show();
        //progressBar.setVisibility(View.VISIBLE);

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

                        list.add(journalEntryModel);
                        Toast.makeText(mContext, " "+ list.size(), Toast.LENGTH_SHORT).show();
                        Collections.sort(list, new Comparator<JournalEntryModel>() {
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
                        });

                        // Collections.reverse(journalList);
                        if(Build.VERSION.SDK_INT > 24) {
                            //  journalList.sort(Comparator.comparing(o -> o.getTimestampCreatedLong()));
                        }
                    }

                    //journalAdapter.notifyDataSetChanged();

                } else{

                }


                //journalList.add(journalEntryMode);
                //journalAdapter.notifyDataSetChanged();

                // progressDialog.dismiss();
               // progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // progressDialog.dismiss();
                //progressBar.setVisibility(View.INVISIBLE);

                Toast.makeText(mContext, "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
            }

        });

        if (list.size() == 0) {

        }

        Toast.makeText(mContext, " "+ list.size(), Toast.LENGTH_SHORT).show();
    }
    public  int  getFavoritesCount(){
        journalList.clear();

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


                             journalList.add(journalEntryModel);
                             i = (int) dataSnapshot.getChildrenCount();
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


    Toast.makeText(mContext, " "+ i, Toast.LENGTH_SHORT).show();

        return i;
    }
}
