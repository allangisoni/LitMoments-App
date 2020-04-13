package com.lit.litmoments.Main;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lit.litmoments.AddJournal.AddJournalEntry;
import com.lit.litmoments.AddJournal.JournalEntryModel;
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

public class HomeFragment extends Fragment {

    private FirebaseAuth mAuth;
   // @BindView(R.id.toolbar)Toolbar toolbar;
    //@BindView(R.id.iconRefresh) ImageView ivRefresh;
    @BindView(R.id.addjournal_fab)
    FloatingActionButton addJournalFab;
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
    String currentUid = "";

    SharedPreferences sharedPreferences;

    public  HomeFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container,false);

        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        journalList.clear();
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseApp.initializeApp(getActivity().getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            //  Toast.makeText(MainActivity.this, "Welcome back "+" "+ mAuth.getCurrentUser().getDisplayName()+" ", Toast.LENGTH_SHORT).show();
        }

        currentUid = mAuth.getCurrentUser().getUid();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        mDatabase = firebaseDatabase.getReference(DATABASE_UPLOADS).child(currentUid);



        //getUserData(sDatabase);
        //isPremiumUser = getUserStatus(subscribersList);
        // mdefaultdatabase = firebaseDatabase.getReference("Default dbase").child(currentUid);
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


        addJournalFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddJournalEntry.class);
                intent.putExtra("isPremium", isPremiumUser);
                startActivity(intent);

            }
        });

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

                        // Collections.reverse(journalList);
                        if(Build.VERSION.SDK_INT > 24) {
                            //  journalList.sort(Comparator.comparing(o -> o.getTimestampCreatedLong()));
                        }
                    }

                    journalAdapter.notifyDataSetChanged();

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
                    journalList.add(journalEntryMode);
                    journalAdapter.notifyDataSetChanged();
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
                if(currentUid != " ")
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_main, menu);

        MenuItem menuItem = menu.findItem(R.id.item_save);
        MenuItem menuPhotoItem = menu.findItem(R.id.item_image);
        Drawable drawable = menu.findItem(R.id.action_search).getIcon();
        if(drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
        }
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        ImageView imageView = (ImageView) getActivity().findViewById(R.id.toolbarImage);
        isWhite = getUITheme();
        if(!isWhite) {
            ImageView icon = searchView.findViewById(android.support.v7.appcompat.R.id.search_button);
            icon.setColorFilter(Color.BLACK);

        }

                searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // hide action item

                //imageView.setVisibility(View.INVISIBLE);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

               // imageView.setVisibility(View.VISIBLE);
                return false;

            }
        });

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted

                 journalAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                journalAdapter.getFilter().filter(query);
                return false;
            }
        });
    }

    /** @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        journalList = savedInstanceState.getParcelableArrayList("JournalEntires");
    } **/




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_search) {
            return true;
        }


        /**    if (id == R.id.action_settings) {
         // launch settings activity
         Intent intent = new Intent(TabbedMainActivity.this, SettingsActivity.class);
         // intent.putExtra("isPremium", isPremiumUser);
         startActivity(intent);
         return true;
         } else if( id == R.id.action_refresh){
         getJornals();
         getUserData(sDatabase);
         return  true;
         } else if(id == R.id.action_search){

         return true;


         } else  if(id == R.id.action_dashboard){
         Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
         intent.putExtra("JournalList", journalList);
         startActivity(intent);
         return true;
         } else if(id == R.id.action_removeads) {

         Intent intent = new Intent(MainActivity.this, RemoveAdsActivity.class);
         startActivity(intent);
         return  true;

         } else if(id == R.id.action_photos){
         Intent intent = new Intent(MainActivity.this, PhotosActivity.class);
         startActivity(intent);
         return  true;
         }

         else {

         } **/

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);



    }
    private boolean getUITheme(){
    String userTheme = sharedPreferences.getString(getResources().getString(R.string.key_uiTheme), "2");
    boolean status =false;
        if (userTheme.equals("2")) {
           status =false;
           //isWhite=false;
        }
       else if (userTheme.equals("1")) {

            status = true;

        }
        else if (userTheme.equals("0")) {

            status = true;

        }
        else if (userTheme.equals("3")) {

            status=false;

        } else if (userTheme.equals("4")) {

            status=true;

        } else if (userTheme.equals("5")) {

            status=false;

        } else if (userTheme.equals("6")) {

            status=false;

        }
        else if (userTheme.equals("8")) {

            status = true;

        }
        else if (userTheme.equals("7")) {

            status=true;
            isDarkTheme = true;
        }
   return status;
    }
}
