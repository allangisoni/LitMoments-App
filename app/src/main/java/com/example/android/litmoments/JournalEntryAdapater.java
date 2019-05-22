package com.example.android.litmoments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class JournalEntryAdapater extends RecyclerView.Adapter<JournalEntryViewHolder> {

private final List<JournalPhotoModel> photolist;
private final Context context;

private final OnItemClickListener listener;

public interface OnItemClickListener {
                void onItemClick(JournalPhotoModel photoItem);
        }



public  JournalEntryAdapater(List<JournalPhotoModel> photolist, Context context, OnItemClickListener listener){

        this.photolist = photolist;
        this.context = context;
        this.listener = listener;

        }
@NonNull
@Override
public JournalEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_list_item, parent, false);
        return new JournalEntryViewHolder(view);
        }

@Override
public void onBindViewHolder(@NonNull JournalEntryViewHolder holder, int position) {

        holder.bind(photolist.get(position), listener);
        //JournalPhotoModel journalPhotoModel = photolist.get(position);
        //holder.tvAuthor.setText(moviz.getReviews().getAuthor());
        }

@Override
public int getItemCount() {
        return photolist.size();
}
}
