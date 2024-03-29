package com.example.android.litmoments.Main;

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;


import com.lit.litmoments.AddJournal.JournalEntryModel;
import com.lit.litmoments.R;

import java.util.ArrayList;
import java.util.List;

public class JournalMainAdapter extends RecyclerView.Adapter<JournalViewHolder> implements Filterable {

    private final List<JournalEntryModel> journallist;
    private  List<JournalEntryModel> filteredJournallist;
    private final Context context;

    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(JournalEntryModel journalEntryModel);
    }



    public  JournalMainAdapter(List<JournalEntryModel> journallist, Context context, OnItemClickListener listener){

        this.journallist = journallist;
        this.context = context;
        this.listener = listener;
        this.filteredJournallist = journallist;

    }
    @NonNull
    @Override
    public JournalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int orientation = context.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // In landscape
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.journal_list_item, parent, false);
            return new JournalViewHolder(view);
        } else {
            // In portrait
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.journal_list_item, parent, false);
            return new JournalViewHolder(view);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull JournalViewHolder holder, int position) {

        holder.bind(filteredJournallist.get(position), listener);
        //JournalPhotoModel journalPhotoModel = photolist.get(position);
        //holder.tvAuthor.setText(moviz.getReviews().getAuthor());
    }


    @Override
    public int getItemCount() {
        return filteredJournallist.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredJournallist = journallist;
                } else {
                    List<JournalEntryModel> filteredList = new ArrayList<>();
                    for (JournalEntryModel row : journallist) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getJournalTitle().toLowerCase().contains(charString.toLowerCase()) || row.getJournalMessage().toLowerCase().contains(charString.toLowerCase()) ||
                        row.getJournalDate().contains(charString) || row.getJournalLocation().toLowerCase().contains(charString.toLowerCase()) ) {
                            filteredList.add(row);
                        }
                    }

                    filteredJournallist = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredJournallist;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredJournallist= (ArrayList<JournalEntryModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


}
