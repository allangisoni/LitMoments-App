package com.example.android.litmoments;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class JournalViewHolder  extends RecyclerView.ViewHolder{

    public TextView tvJournalMonth,tvJournalDay, tvJournalYear,  tvJournalTitle, tvJournalMessage, tvJournalLocation;

    public ImageView ivJournalPhoto;

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

        try{
        if(journalItem.getJournalImagePath().get(0) != null) {
             Picasso.with(itemView.getContext()).load(journalItem.getJournalImagePath().get(0)).placeholder(R.drawable.ic_mesut).error(R.drawable.ic_journalfinal).into(ivJournalPhoto);
         }
         else {
             Picasso.with(itemView.getContext()).load(R.drawable.ic_mesut).placeholder(R.drawable.ic_mesut).error(R.drawable.ic_mesut).into(ivJournalPhoto);
         } }

         catch (Exception e){
             Picasso.with(itemView.getContext()).load(R.drawable.ic_mesut).placeholder(R.drawable.ic_mesut).error(R.drawable.ic_mesut).into(ivJournalPhoto);
        }

        // tvJournalDate.setText(journalItem.getJournalDate());
       // Toast.makeText(AddJournalEntry.this, monthName, Toast.LENGTH_LONG).show();

        String myDate = journalItem.getJournalDate();
        if(!myDate.isEmpty()) {
            String monthName, day, year;
            monthName = myDate.substring(5,7);
            try {
                monthName = formatMonth(monthName);

                Toast.makeText(itemView.getContext(), monthName, Toast.LENGTH_LONG).show();
            } catch (Exception e) {

            }
            day = myDate.substring(7, 9);
            year = myDate.substring(0, 4);

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

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                listener.onItemClick(journalItem);

                 Intent intent = new Intent(itemView.getContext(), DisplayJournal.class);
                 intent.putExtra("journalItems", journalItem);
                 itemView.getContext().startActivity(intent);
            }
        });
    }


    public String formatMonth(String month) throws ParseException {
        SimpleDateFormat monthParse = new SimpleDateFormat("MM");
        SimpleDateFormat monthDisplay = new SimpleDateFormat("MMMM");
        return monthDisplay.format(monthParse.parse(month));
    }
}
