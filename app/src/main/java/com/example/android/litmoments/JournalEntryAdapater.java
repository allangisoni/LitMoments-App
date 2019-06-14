package com.example.android.litmoments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JournalEntryAdapater extends RecyclerView.Adapter<JournalEntryViewHolder> {

private  List<JournalPhotoModel> photolist;
private final Context context;

//private final OnItemClickListener listener;
private  List<JournalPhotoModel> items,selected;

OnClickAction receiver;

        public interface OnClickAction {
                public void onClickAction();
        }
/**
public interface OnItemClickListener {
                void onItemClick(JournalPhotoModel photoItem);
        }
**/


public  JournalEntryAdapater(List<JournalPhotoModel> photolist, Context context){

        this.photolist = photolist;
        this.context = context;
        this.selected = new ArrayList<>();
       // this.listener = listener;

        }
@NonNull
@Override
public JournalEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_list_item, parent, false);
        return new JournalEntryViewHolder(view);
        }

@Override
public void onBindViewHolder(final JournalEntryViewHolder holder, int position) {

        //holder.bind(photolist.get(position), listener);
        final JournalPhotoModel journalPhotoModel = photolist.get(position);
        //holder.tvAuthor.setText(moviz.getReviews().getAuthor());
        Picasso.with(context).load(journalPhotoModel.getJournalImage()).into(holder.ivaddJournalPhoto);

        holder.cbaddJournalCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        if(selected.contains(journalPhotoModel)){
                                selected.remove(journalPhotoModel);
                                unhighlightView(holder);
                                holder.cbaddJournalCheck.setChecked(false);
                        } else {
                                selected.add(journalPhotoModel);
                                highlightView(holder);
                                holder.cbaddJournalCheck.setChecked(true);
                        }
                    receiver.onClickAction();
                }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        if (selected.contains(journalPhotoModel)) {
                                selected.remove(journalPhotoModel);
                                unhighlightView(holder);
                        } else {
                                selected.add(journalPhotoModel);
                                highlightView(holder);
                        }

                        receiver.onClickAction();
                }
        });

        if (selected.contains(journalPhotoModel)) {
                highlightView(holder);
        }
        else {
                unhighlightView(holder);
        }


}

        public void highlightView(JournalEntryViewHolder holder) {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                holder.cbaddJournalCheck.setVisibility(View.VISIBLE);
                holder.cbaddJournalCheck.setChecked(true);

        }

        public void unhighlightView(JournalEntryViewHolder holder) {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
                holder.cbaddJournalCheck.setVisibility(View.INVISIBLE);
                holder.cbaddJournalCheck.setChecked(true);
        }



        public void addAll(List<JournalPhotoModel> items) {
                clearAll(false);
                this.photolist = items;
                notifyDataSetChanged();
        }

        public void clearAll(boolean isNotify) {
                photolist.clear();
                selected.clear();
                if (isNotify) notifyDataSetChanged();
        }

        public void clearSelected() {
                selected.clear();
                notifyDataSetChanged();
        }

        public void selectAll() {
                selected.clear();
                selected.addAll(items);
                notifyDataSetChanged();
        }

        public List<JournalPhotoModel> getSelected() {
                return selected;
        }

        public void setActionModeReceiver(OnClickAction receiver) {
                this.receiver = receiver;
        }

@Override
public int getItemCount() {
        return photolist.size();
}





}
