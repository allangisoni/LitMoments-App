package com.example.android.litmoments.AddJournal;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.litmoments.R;
import com.google.android.flexbox.FlexboxLayoutManager;
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
                        ArrayList<String> arrimageFile = new ArrayList<>();

                        for (JournalPhotoModel image : photolist) {

                                arrimageFile.add(image.getJournalImage().toString());
                        }
                        Intent intent = new Intent(view.getContext(), AddJournalImageSlider.class);
                        // intent.putExtra("displayjournal", journalEntry.getJournalImagePath()));
                        intent.putStringArrayListExtra("journalImageFiles", arrimageFile);
                        Log.d("filepath", arrimageFile.get(0));
                        view.getContext().startActivity(intent);
                }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                        if (selected.contains(journalPhotoModel)) {
                                selected.remove(journalPhotoModel);
                                unhighlightView(holder);
                        } else {
                                selected.add(journalPhotoModel);
                                highlightView(holder);
                        }

                        receiver.onClickAction();
                        return true;
                }
        });

        if (selected.contains(journalPhotoModel)) {
                highlightView(holder);
        }
        else {
                unhighlightView(holder);
        }


        ViewGroup.LayoutParams lp = holder.ivaddJournalPhoto.getLayoutParams();
        if (lp instanceof FlexboxLayoutManager.LayoutParams) {
                FlexboxLayoutManager.LayoutParams flexboxLp = (FlexboxLayoutManager.LayoutParams)lp;
                flexboxLp.setFlexGrow(1.0f);        }

}

        public void highlightView(JournalEntryViewHolder holder) {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                holder.cbaddJournalCheck.setVisibility(View.VISIBLE);
                holder.cbaddJournalCheck.setChecked(true);

        }

        public void unhighlightView(JournalEntryViewHolder holder) {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
                holder.cbaddJournalCheck.setChecked(false);
                holder.cbaddJournalCheck.setVisibility(View.INVISIBLE);

        }

        public void unhighlightViewOnCancel(JournalEntryViewHolder holder) {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
                holder.cbaddJournalCheck.setChecked(false);
                holder.cbaddJournalCheck.setVisibility(View.GONE);

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
