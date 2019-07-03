package com.example.android.litmoments.DispJournal;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.litmoments.AddJournal.JournalEntryModel;
import com.example.android.litmoments.EditJournal.EditJournalEntry;
import com.example.android.litmoments.Main.MainActivity;
import com.example.android.litmoments.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DisplayJournal extends AppCompatActivity {
    @BindView(R.id.entrytoolbar)  Toolbar entryToolbar;
    @BindView(R.id.tvdpJournalDate)  TextView tvJournalDate;
    @BindView(R.id.tvdpJournalLocation)  TextView tvJournalLocation;
    @BindView(R.id.tvdpJournalWeather)  TextView tvJournalWeather;
    @BindView(R.id.tvdpJournalMood)  TextView tvJournalMood;
    @BindView(R.id.tvdpJournalTitle)  TextView tvJournalTitle;
    @BindView(R.id.tvdpJournalMessage)  TextView tvJournalMessage;
    @BindView(R.id.ivdpJournalPhoto)  ImageView ivJournalPhoto;
    @BindView(R.id.ivWeather)  ImageView ivJournalWeather;
    @BindView(R.id.ivMood)  ImageView ivJournalMood;

    JournalEntryModel journalEntry;
    Intent intent;

    String journalKey = " ";
    private DatabaseReference mDatabase;
    public static final String DATABASE_UPLOADS = "User's Journal Entries";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
     //   getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_journal);
        ButterKnife.bind(this);




        if (entryToolbar != null)
        {
            setSupportActionBar(entryToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            }




        //make translucent statusBar on kitkat devices
      if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
          //  getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
           setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);

           getWindow().setStatusBarColor(Color.BLACK);
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
           // supportPostponeEnterTransition();

        }




        mAuth = FirebaseAuth.getInstance();
        String currentUid = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference(DATABASE_UPLOADS).child(currentUid);


        Bundle extras = getIntent().getExtras();
        journalEntry = extras.getParcelable("journalItems");


        if(journalEntry != null){
           getJournalDetails();

           //  ArrayList<String> arrimageFile = new ArrayList<>();
           // arrimageFile.addAll(journalEntry.getJournalImagePath());
           // Toast.makeText(DisplayJournal.this, "No is " + " "+ arrimageFile.size()+" ", Toast.LENGTH_SHORT).show();
        }

        ivJournalPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (journalEntry.getJournalImagePath().size() == 0) {

                }
                else {
                    ArrayList<String> arrimageFile = new ArrayList<>();
                    arrimageFile.addAll(journalEntry.getJournalImagePath());
                    Intent intent = new Intent(DisplayJournal.this, ImageSliderActivity.class);
                    // intent.putExtra("displayjournal", journalEntry.getJournalImagePath()));
                    intent.putStringArrayListExtra("imagefiles", arrimageFile);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        supportFinishAfterTransition();
        return true;
    }


    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }


    public void getJournalDetails (){

      tvJournalDate.setText(journalEntry.getJournalDate());
      tvJournalLocation.setText(journalEntry.getJournalLocation());
      tvJournalWeather.setText(journalEntry.getJournalWeather());
      tvJournalMood.setText(journalEntry.getJournalMood());
      if(journalEntry.getJournalWeather().equalsIgnoreCase("Sunny")){
          Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_sunny);
          ivJournalWeather.setImageBitmap(bitmap);
      } else if(journalEntry.getJournalWeather().equalsIgnoreCase("Cloudy")){
          Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_cloudy);
          ivJournalWeather.setImageBitmap(bitmap);
      }else if(journalEntry.getJournalWeather().equalsIgnoreCase("Rainy")){
          Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_rainy);
          ivJournalWeather.setImageBitmap(bitmap);
      } else{

      }

        if(journalEntry.getJournalMood().equalsIgnoreCase("Happy")){
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_happy);
            ivJournalMood.setImageBitmap(bitmap);
        } else if(journalEntry.getJournalMood().equalsIgnoreCase("Sad")){
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_sad);
            ivJournalMood.setImageBitmap(bitmap);
        }else if(journalEntry.getJournalMood().equalsIgnoreCase("Surprised")){
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_surprised);
            ivJournalMood.setImageBitmap(bitmap);
        } else{

        }


      tvJournalTitle.setText(journalEntry.getJournalTitle());
      tvJournalMessage.setText(journalEntry.getJournalMessage());
        try {
            if (journalEntry.getJournalImagePath().get(0) != null) {
                Picasso.with(DisplayJournal.this).load(journalEntry.getJournalImagePath().get(0)).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.ic_mesut).error(R.drawable.ic_mesut).into(ivJournalPhoto, new Callback() {
                    @Override
                    public void onSuccess() {
                       // supportPostponeEnterTransition();
                        scheduleStartPostponedTransition(ivJournalPhoto);

                    }

                    @Override
                    public void onError() {
                        Picasso.with(DisplayJournal.this).load(journalEntry.getJournalImagePath().get(0))
                                .placeholder(R.drawable.ic_mesut).error(R.drawable.ic_mesut).into(ivJournalPhoto, new Callback() {
                            @Override
                            public void onSuccess() {

                                scheduleStartPostponedTransition(ivJournalPhoto);
                            }

                            @Override
                            public void onError() {
                                Log.v("Picasso","Could not fetch image");
                            }
                        });
                    }
                });
            } else {
                Picasso.with(DisplayJournal.this).load(R.drawable.ic_mesut).placeholder(R.drawable.ic_mesut).error(R.drawable.ic_mesut).into(ivJournalPhoto);
                scheduleStartPostponedTransition(ivJournalPhoto);
            }
        }
         catch (Exception e){
                Picasso.with(DisplayJournal.this).load(R.drawable.ic_mesut).placeholder(R.drawable.ic_mesut).error(R.drawable.ic_mesut).into(ivJournalPhoto);
                scheduleStartPostponedTransition(ivJournalPhoto);
            }
      journalKey = journalEntry.getKey();
        }

    private void scheduleStartPostponedTransition(final View sharedElement) {
        sharedElement.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        sharedElement.getViewTreeObserver().removeOnPreDrawListener(this);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            supportStartPostponedEnterTransition();
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
    }

    public void deleteJournal(){

       mDatabase.child(journalKey).removeValue(new DatabaseReference.CompletionListener() {
           @Override
           public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
               Intent intent = new Intent(DisplayJournal.this, MainActivity.class);
               startActivity(intent);
           }
       });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.display_journal_menu, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_edit) {
            // launch settings activity
            Intent intent = new Intent(DisplayJournal.this, EditJournalEntry.class);
            intent.putExtra("journalEntry", journalEntry);
            startActivity(intent);
            return true;
        } else if(id == R.id.action_delete){
           deleteJournal();
           return true;

        }

        else{

        }
        return super.onOptionsItemSelected(item);
    }
}
