package com.example.android.litmoments.Main;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ajts.androidmads.fontutils.FontUtils;
import com.example.android.litmoments.AddJournal.AddJournalEntry;
import com.example.android.litmoments.AddJournal.JournalEntryModel;
import com.example.android.litmoments.DashboardActivity;
import com.example.android.litmoments.PrefMethods;
import com.example.android.litmoments.R;
import com.example.android.litmoments.Settings.SettingsActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.supercharge.shimmerlayout.ShimmerLayout;

public class MainActivity extends AppCompatActivity implements JournalMainAdapter.OnItemClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private FirebaseAuth mAuth;
    @BindView(R.id.toolbar) Toolbar toolbar;
    //@BindView(R.id.iconRefresh) ImageView ivRefresh;
    @BindView(R.id.addjournal_fab)  FloatingActionButton addJournalFab;
    @BindView(R.id.rvJournalEntries)  RecyclerView rvJournalEntries;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    private JournalMainAdapter journalAdapter;
    private ArrayList<JournalEntryModel> journalList = new ArrayList<>();
    private DatabaseReference mDatabase;

    public static final String DATABASE_UPLOADS = "User's Journal Entries";
    private SearchView searchView;
    PrefMethods prefMethods = new PrefMethods();

    Boolean isWhite = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        loadUiTheme(sharedPreferences);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            setUpThemeContent();
            loadWidgetColors(sharedPreferences);
        }



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                /** Slide fadein = new Slide();
                 fadein.setDuration(3500);
                 Slide fadeout = new Slide();
                 fadeout.setDuration(3500);
                 // set an enter transition
                 getWindow().setSharedElementEnterTransition(fadein);
                 // set an exit transition
                 getWindow().setSharedElementExitTransition(fadeout);
                 // supportPostponeEnterTransition();
                 supportPostponeEnterTransition();
                 postponeEnterTransition();

                 **/
            Fade fade= new Fade();
            View view = getWindow().getDecorView();
            fade.excludeTarget(view.findViewById(R.id.action_bar_container), true);
            fade.excludeTarget(android.R.id.statusBarBackground, true);
            fade.excludeTarget(android.R.id.navigationBarBackground, true);

            getWindow().setEnterTransition(fade);
            getWindow().setExitTransition(fade);
           // getWindow().setSharedElementExitTransition(new Explode());


        }


        journalList.clear();
       // FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseApp.initializeApp(getApplicationContext());



        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            Toast.makeText(MainActivity.this, "Current user is "+" "+ mAuth.getCurrentUser().getDisplayName()+" ", Toast.LENGTH_SHORT).show();
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
        journalAdapter = new JournalMainAdapter(journalList, getApplicationContext(), this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvJournalEntries.setLayoutManager(mLayoutManager);
        rvJournalEntries.setItemAnimator(new DefaultItemAnimator());

        rvJournalEntries.setAdapter(journalAdapter);
        rvJournalEntries.setNestedScrollingEnabled(false);


        addJournalFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddJournalEntry.class));
            }
        });

       /** ivRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               getJornals();
            }
        }); **/

        getJornals();

    }

    public void setUpThemeContent (){
        if(isWhite == true) {
            toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
            ((TextView)toolbar.getChildAt(1)).setTextColor(getResources().getColor(android.R.color.white));
            ((ImageView) toolbar.getChildAt(0)).setColorFilter(getResources().getColor(android.R.color.white));
        } else {
            toolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
            ((TextView)toolbar.getChildAt(1)).setTextColor(getResources().getColor(R.color.colorAccent));
            ((ImageView) toolbar.getChildAt(0)).setColorFilter(getResources().getColor(R.color.colorAccent));
        }
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

                       JournalEntryModel journalEntryModel=npsnapshot.getValue(JournalEntryModel.class);
                        journalList.add(journalEntryModel);
                    }

                    journalAdapter.notifyDataSetChanged();

                }
               // progressDialog.dismiss();
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            // progressDialog.dismiss();
             progressBar.setVisibility(View.INVISIBLE);

             Toast.makeText(MainActivity.this, "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        ImageView imageView = (ImageView) findViewById(R.id.toolbarImage);



        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // hide action item

                imageView.setVisibility(View.INVISIBLE);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

               imageView.setVisibility(View.VISIBLE);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            // launch settings activity
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        } else if( id == R.id.action_refresh){
            getJornals();
            return  true;
        } else if(id == R.id.action_search){

            return true;


        } else  if(id == R.id.action_dashboard){
            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
            intent.putExtra("JournalList", journalList);
            startActivity(intent);
            return true;
        }

        else {

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(JournalEntryModel journalEntryModel) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        //getJornals();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

    }

    private void loadUiTheme (SharedPreferences sharedPreferences){

        String userTheme = sharedPreferences.getString(getResources().getString(R.string.key_uiTheme), "2");
        if (userTheme.equals("2")) {
            setTheme(R.style.LitStyle);
            isWhite = false;
        }
        else if (userTheme.equals("1")) {
            setTheme(R.style.ReddishLitStyle);
            isWhite = true;
        }
        else if (userTheme.equals("0")) {
            setTheme(R.style.BlueLitStyle);
            isWhite = true;
        }
    }

    public  void loadWidgetColors(SharedPreferences sharedPreferences){
        String selectedFont = sharedPreferences.getString(getString(R.string.key_uiThemeFont), "0");
        if(selectedFont.equals("0")){
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.parisienneregular);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
        } else if(selectedFont.equals("1")){
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.patrick_hand_sc);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
        } else if( selectedFont.equals("2")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.sofadi_one);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
        } else {

        }
    }



    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getResources().getString(R.string.key_uiTheme))) {
            loadUiTheme(sharedPreferences);
            MainActivity.this.recreate();
        } else if(key.equals(getResources().getString(R.string.key_uiThemeFont))){
            loadWidgetColors(sharedPreferences);
            MainActivity.this.recreate();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState. putParcelableArrayList("JournalEntires", (ArrayList)journalList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        journalList = savedInstanceState.getParcelableArrayList("JournalEntires");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(journalList == null){

        }
    }


    /**
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            // launch settings activity
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        } else if( id == R.id.action_refresh){
            getJornals();
            return  true;
        } else{

        }

        return super.onOptionsItemSelected(item);
    }

**/
}
