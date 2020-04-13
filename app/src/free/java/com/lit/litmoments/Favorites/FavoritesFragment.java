package com.lit.litmoments.Favorites;

import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.lit.litmoments.Main.JournalMainAdapter;
import com.lit.litmoments.PrefMethods;
import com.lit.litmoments.R;
import com.lit.litmoments.SubscribersModelActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoritesFragment extends Fragment {
    private FirebaseAuth mAuth;
    @BindView(R.id.rvJournalEntries)
    RecyclerView rvJournalEntries;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.coordinatorlayout)
    CoordinatorLayout coordinatorLayout;

    private JournalMainAdapter journalAdapter;
    private ArrayList<JournalEntryModel> journalList = new ArrayList<>();
    private ArrayList<SubscribersModelActivity> subscribersList = new ArrayList<>();
    private DatabaseReference mDatabase,  mdefaultdatabase;
    SubscribersModelActivity subscribersModel;

    public static final String DATABASE_UPLOADS = "User's Journal Entries";
    private SearchView searchView;
    PrefMethods prefMethods = new PrefMethods();

    Boolean isWhite = false;
    Boolean isDarkTheme = false;
    boolean doubleBackToExitPressedOnce = false;

    // This is the Notification Channel ID. More about this in the next section
    public static final String NOTIFICATION_CHANNEL_ID = "channel_id";
    //User visible Channel Name
    public static final String CHANNEL_NAME = "Notification Channel";
    private DatabaseReference sDatabase;
    Boolean isPremiumUser = false;

    SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_favorites_fragment);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_favorites_fragment, container, false);

        ButterKnife.bind(this, view);
        //setHasOptionsMenu(true);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        journalList.clear();
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseApp.initializeApp(getActivity().getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            //  Toast.makeText(MainActivity.this, "Welcome back "+" "+ mAuth.getCurrentUser().getDisplayName()+" ", Toast.LENGTH_SHORT).show();
        }

        String currentUid = mAuth.getCurrentUser().getUid();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        mDatabase = firebaseDatabase.getReference(DATABASE_UPLOADS).child(currentUid);


        if(savedInstanceState != null){
            journalList = savedInstanceState.getParcelableArrayList("JournalEntires");
        }
        else {
            journalList = journalList;
        }

        journalAdapter = new JournalMainAdapter(journalList, getContext(), new JournalMainAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(JournalEntryModel journalEntryModel) {

            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rvJournalEntries.setLayoutManager(mLayoutManager);
        rvJournalEntries.setItemAnimator(new DefaultItemAnimator());

        rvJournalEntries.setAdapter(journalAdapter);
        rvJournalEntries.setNestedScrollingEnabled(false);


        getJornals();

        return view;
    }


    private void getJornals(){


        //final ProgressDialog progressDialog = new ProgressDialog(this);
        //progressDialog.setMessage("Please wait...........");
        // progressDialog.setTitle("Retrieving data");
        // progressDialog.show();
        progressBar.setVisibility(View.VISIBLE);
        mDatabase.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                journalList.clear();
                if (dataSnapshot.exists()){
                    for (DataSnapshot npsnapshot : dataSnapshot.getChildren()){

                        if(npsnapshot.getValue(JournalEntryModel.class).getIsFavorite() != null) {

                            if (npsnapshot.getValue(JournalEntryModel.class).getIsFavorite().equals("True")) {

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
                                journalEntryModel.setIsFavorite(npsnapshot.getValue(JournalEntryModel.class).getIsFavorite());
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

                                journalList.add(journalEntryModel);
                                Collections.sort(journalList, new Comparator<JournalEntryModel>() {
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
                            }
                        }

                    }

                    journalAdapter.notifyDataSetChanged();

                } else{
             /**       JournalEntryModel journalEntryMode = new JournalEntryModel();
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
                    journalList.add(journalEntryMode);
                    journalAdapter.notifyDataSetChanged();
              **/
                }




                //journalList.add(journalEntryMode);
                //journalAdapter.notifyDataSetChanged();

                // progressDialog.dismiss();
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // progressDialog.dismiss();
                progressBar.setVisibility(View.INVISIBLE);

                Toast.makeText(getActivity(), "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
            }

        });

        if (journalList.size() == 0) {


        }


    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState. putParcelableArrayList("JournalEntires", (ArrayList)journalList);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null) {
            journalList = savedInstanceState.getParcelableArrayList("JournalEntires");
        }
    }
}
