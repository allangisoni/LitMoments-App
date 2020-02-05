package com.lit.litmoments.Main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ajts.androidmads.fontutils.FontUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.lit.litmoments.R;
import com.lit.litmoments.AddJournal.JournalEntryModel;
import com.lit.litmoments.DispJournal.DisplayJournal;
import com.lit.litmoments.SubscribersModelActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class JournalViewHolder  extends RecyclerView.ViewHolder{

    public TextView tvJournalMonth,tvJournalDay, tvJournalYear,  tvJournalTitle, tvJournalMessage, tvJournalLocation;

    public ImageView ivJournalPhoto;
    ShimmerLayout shimmerText;

    private  List<JournalEntryModel> filteredJournallist;




    public JournalViewHolder(View itemView) {
        super(itemView);
     // tvJournalDate= itemView.findViewById(R.id.tvDate);
      tvJournalMonth= itemView.findViewById(R.id.tvMonth);
      tvJournalDay= itemView.findViewById(R.id.tvDay);
      tvJournalYear= itemView.findViewById(R.id.tvYear);
      tvJournalTitle= itemView.findViewById(R.id.tvTitle);
      tvJournalMessage = itemView.findViewById(R.id.tvMessage);
      tvJournalLocation = itemView.findViewById(R.id.tvLocation);
      ivJournalPhoto = itemView.findViewById(R.id.ivJournalImage);


    }

    public void bind(final JournalEntryModel journalItem, final JournalMainAdapter.OnItemClickListener listener) {

       // final  JournalEntryModel journalItem =  filteredJournallist.get(getAdapterPosition());
        // journalItem = filteredJournallist.get(getAdapterPosition());
        Picasso picasso = Picasso.with(itemView.getContext());
        picasso.setIndicatorsEnabled(false);
        try{

        if(journalItem.getJournalImagePath().get(0) != null) {

             //Picasso.with(itemView.getContext()).setIndicatorsEnabled(false);
             picasso.load(journalItem.getJournalImagePath().get(0)).fit().centerCrop().networkPolicy(NetworkPolicy.OFFLINE)
                     .placeholder(R.drawable.ic_vectorjournal).error(R.drawable.ic_offline).into(ivJournalPhoto, new Callback() {
                 @Override
                 public void onSuccess() {

                 }

                 @Override
                 public void onError() {
                     picasso.with(itemView.getContext()).load(journalItem.getJournalImagePath().get(0)).fit().centerCrop()
                             .placeholder(R.drawable.ic_vectorjournal).error(R.drawable.ic_offline).into(ivJournalPhoto, new Callback() {
                         @Override
                         public void onSuccess() {
                         }

                         @Override
                         public void onError() {
                             Log.v("Picasso","Could not fetch image");
                         }
                     });


                 }
             });
         }
         else {
             picasso.load(R.drawable.ic_vectorjournal).fit().centerCrop().networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.ic_letstravel).error(R.drawable.ic_offline).into(ivJournalPhoto);
         } }

         catch (Exception e){
             picasso.load(R.drawable.ic_vectorjournal).fit().centerCrop().networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.ic_letstravel).error(R.drawable.ic_offline).into(ivJournalPhoto);
        }

        // tvJournalDate.setText(journalItem.getJournalDate());
       // Toast.makeText(AddJournalEntry.this, monthName, Toast.LENGTH_LONG).show();
        String myDate = journalItem.getJournalDate();
        if(!myDate.isEmpty()) {
            String monthName, day, year;
            monthName = journalItem.getMonth();
            try {
                monthName = formatMonth(monthName);

              //  Toast.makeText(itemView.getContext(), monthName, Toast.LENGTH_LONG).show();
            } catch (Exception e) {

            }
            day = journalItem.getDay();
            year = myDate.substring(0, 4);

            //day = day.replace("-", "");
            tvJournalMonth.setText(monthName);
            tvJournalDay.setText(day);
            tvJournalYear.setText(year);
        } else {

            tvJournalMonth.setText("December");
            tvJournalDay.setText("28");
            tvJournalYear.setText("2019");
        }


         tvJournalTitle.setText(journalItem.getJournalTitle());
         tvJournalMessage.setText(journalItem.getJournalMessage());
         tvJournalLocation.setText(journalItem.getJournalLocation());

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());
        String selectedFont = sharedPreferences.getString(itemView.getResources().getString(R.string.key_uiThemeFont), "6");

        if( selectedFont != null &&selectedFont.equals("0")){
            Typeface myCustomFont = ResourcesCompat.getFont(itemView.getContext(), R.font.parisienneregular);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToView(tvJournalTitle,myCustomFont);
            fontUtils.applyFontToView(tvJournalMonth,myCustomFont);
            fontUtils.applyFontToView(tvJournalMessage,myCustomFont);
            fontUtils.applyFontToView(tvJournalDay,myCustomFont);
            fontUtils.applyFontToView(tvJournalYear,myCustomFont);
            fontUtils.applyFontToView(tvJournalLocation,myCustomFont);
            tvJournalDay.setTextSize(16f);
            tvJournalYear.setTextSize(12f);

        } else if(selectedFont.equals("1")){
            Typeface myCustomFont = ResourcesCompat.getFont(itemView.getContext(), R.font.patrick_hand_sc);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToView(tvJournalTitle,myCustomFont);
            fontUtils.applyFontToView(tvJournalMonth,myCustomFont);
            fontUtils.applyFontToView(tvJournalMessage,myCustomFont);
            fontUtils.applyFontToView(tvJournalDay,myCustomFont);
            fontUtils.applyFontToView(tvJournalYear,myCustomFont);
            fontUtils.applyFontToView(tvJournalLocation,myCustomFont);

        } else if( selectedFont.equals("2")) {
            Typeface myCustomFont = ResourcesCompat.getFont(itemView.getContext(), R.font.sofadi_one);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToView(tvJournalTitle,myCustomFont);
            fontUtils.applyFontToView(tvJournalMonth,myCustomFont);
            fontUtils.applyFontToView(tvJournalMessage,myCustomFont);
            fontUtils.applyFontToView(tvJournalDay,myCustomFont);
            fontUtils.applyFontToView(tvJournalYear,myCustomFont);
            fontUtils.applyFontToView(tvJournalLocation,myCustomFont);
        } else if(selectedFont.equals("3")){
            FontUtils fontUtils = new FontUtils();
            Typeface myCustomFont = Typeface.create("sans-serif-condensed", Typeface.NORMAL);
            fontUtils.applyFontToView(tvJournalTitle,myCustomFont);
            fontUtils.applyFontToView(tvJournalMonth,Typeface.SANS_SERIF);
            fontUtils.applyFontToView(tvJournalMessage,Typeface.SANS_SERIF);
            fontUtils.applyFontToView(tvJournalDay,Typeface.SANS_SERIF);
            fontUtils.applyFontToView(tvJournalYear,Typeface.SANS_SERIF);
            fontUtils.applyFontToView(tvJournalLocation,Typeface.SANS_SERIF);
        }  else if( selectedFont.equals("4")) {
            Typeface myCustomFont = ResourcesCompat.getFont(itemView.getContext(), R.font.concert_one);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToView(tvJournalTitle, myCustomFont);
            fontUtils.applyFontToView(tvJournalMonth, myCustomFont);
            fontUtils.applyFontToView(tvJournalMessage, myCustomFont);
            fontUtils.applyFontToView(tvJournalDay, myCustomFont);
            fontUtils.applyFontToView(tvJournalYear, myCustomFont);
            fontUtils.applyFontToView(tvJournalLocation, myCustomFont);
        }
        else if( selectedFont.equals("5")) {
            Typeface myCustomFont = ResourcesCompat.getFont(itemView.getContext(), R.font.oleo_script);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToView(tvJournalTitle, myCustomFont);
            fontUtils.applyFontToView(tvJournalMonth, myCustomFont);
            fontUtils.applyFontToView(tvJournalMessage, myCustomFont);
            fontUtils.applyFontToView(tvJournalDay, myCustomFont);
            fontUtils.applyFontToView(tvJournalYear, myCustomFont);
            fontUtils.applyFontToView(tvJournalLocation, myCustomFont);
        }
        else if( selectedFont.equals("6")) {
            Typeface myCustomFont = ResourcesCompat.getFont(itemView.getContext(), R.font.pt_sans_narrow);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToView(tvJournalTitle, myCustomFont);
            fontUtils.applyFontToView(tvJournalMonth, myCustomFont);
            fontUtils.applyFontToView(tvJournalMessage, myCustomFont);
            fontUtils.applyFontToView(tvJournalDay, myCustomFont);
            fontUtils.applyFontToView(tvJournalYear, myCustomFont);
            fontUtils.applyFontToView(tvJournalLocation, myCustomFont);
        }  else if( selectedFont.equals("7")) {
            Typeface myCustomFont = ResourcesCompat.getFont(itemView.getContext(), R.font.roboto_condensed_light);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToView(tvJournalTitle, myCustomFont);
            fontUtils.applyFontToView(tvJournalMonth, myCustomFont);
            fontUtils.applyFontToView(tvJournalMessage, myCustomFont);
            fontUtils.applyFontToView(tvJournalDay, myCustomFont);
            fontUtils.applyFontToView(tvJournalYear, myCustomFont);
            fontUtils.applyFontToView(tvJournalLocation, myCustomFont);
        }  else if( selectedFont.equals("8")) {
            Typeface myCustomFont = ResourcesCompat.getFont(itemView.getContext(), R.font.shadows_into_light);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToView(tvJournalTitle, myCustomFont);
            fontUtils.applyFontToView(tvJournalMonth, myCustomFont);
            fontUtils.applyFontToView(tvJournalMessage, myCustomFont);
            fontUtils.applyFontToView(tvJournalDay, myCustomFont);
            fontUtils.applyFontToView(tvJournalYear, myCustomFont);
            fontUtils.applyFontToView(tvJournalLocation, myCustomFont);
        } else if( selectedFont.equals("9")) {
            Typeface myCustomFont = ResourcesCompat.getFont(itemView.getContext(), R.font.slabo_13px);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToView(tvJournalTitle, myCustomFont);
            fontUtils.applyFontToView(tvJournalMonth, myCustomFont);
            fontUtils.applyFontToView(tvJournalMessage, myCustomFont);
            fontUtils.applyFontToView(tvJournalDay, myCustomFont);
            fontUtils.applyFontToView(tvJournalYear, myCustomFont);
            fontUtils.applyFontToView(tvJournalLocation, myCustomFont);
        }

        Bitmap bitmap = ((BitmapDrawable)ivJournalPhoto.getDrawable()).getBitmap();
        Palette p = createPaletteSync(bitmap);
        Palette.Swatch vibrantSwatch = p.getVibrantSwatch();



        // Load default colors
        int iconColor = ContextCompat.getColor(itemView.getContext(),
                R.color.background);
        int backgroundColor = ContextCompat.getColor(itemView.getContext(),
                R.color.colorAccent);

    //    int mostdominantColor = vibrantSwatch.getRgb();


        if(vibrantSwatch != null) {
            iconColor = vibrantSwatch.getTitleTextColor();
            backgroundColor = vibrantSwatch.getRgb();
            journalItem.setIconColor(backgroundColor);
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                listener.onItemClick(journalItem);

                 Intent intent = new Intent(itemView.getContext(), DisplayJournal.class);
                 intent.putExtra("journalItems", journalItem);
                 ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) itemView.getContext(), ivJournalPhoto, "journaltrans");
                 itemView.getContext().startActivity(intent, options.toBundle());

            }
        });
    }


    public String formatMonth(String month) throws ParseException {
        SimpleDateFormat monthParse = new SimpleDateFormat("MM");
        SimpleDateFormat monthDisplay = new SimpleDateFormat("MMMM");
        return monthDisplay.format(monthParse.parse(month));
    }

    public Palette createPaletteSync(Bitmap bitmap) {
        Palette p = Palette.from(bitmap).generate();
        return p;
    }


}
