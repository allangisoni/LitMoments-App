package com.example.android.litmoments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

    JournalEntryModel journalEntry;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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


        Bundle extras = getIntent().getExtras();
        journalEntry = extras.getParcelable("journalItems");


        if(journalEntry != null){
           getJournalDetails();
          //  Toast.makeText(DisplayJournal.this, "Title is" + " "+ journalEntry.getJournalImagePath()+" ", Toast.LENGTH_SHORT).show();
        }

        ivJournalPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            Intent intent = new Intent(DisplayJournal.this, ImageSliderActivity.class);
            intent.putExtra("displayjournal", journalEntry.getJournalImagePath().get(0));
            startActivity(intent);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
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
      tvJournalTitle.setText(journalEntry.getJournalTitle());
      tvJournalMessage.setText(journalEntry.getJournalMessage());
        try {
            if (journalEntry.getJournalImagePath().get(0) != null) {
                Picasso.with(DisplayJournal.this).load(journalEntry.getJournalImagePath().get(0)).placeholder(R.drawable.ic_mesut).error(R.drawable.ic_mesut).into(ivJournalPhoto);
            } else {
                Picasso.with(DisplayJournal.this).load(R.drawable.ic_mesut).placeholder(R.drawable.ic_mesut).error(R.drawable.ic_mesut).into(ivJournalPhoto);
            }
        }
         catch (Exception e){
                Picasso.with(DisplayJournal.this).load(R.drawable.ic_mesut).placeholder(R.drawable.ic_mesut).error(R.drawable.ic_mesut).into(ivJournalPhoto);
            }

        }
}
