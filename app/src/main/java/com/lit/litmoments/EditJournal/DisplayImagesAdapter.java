package com.lit.litmoments.EditJournal;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lit.litmoments.R;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.lit.litmoments.AddJournal.AddJournalImageSlider;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DisplayImagesAdapter extends RecyclerView.Adapter<DisplayImagesViewHolder> {

    private List<DisplayImagesModel> photolist;
    private final Context context;
    int file;

    private  List<DisplayImagesModel> items,selected;

    DisplayImagesAdapter.OnClickAction receiver;

    public interface OnClickAction {
        public void onClickAction();
    }




    public  DisplayImagesAdapter(List<DisplayImagesModel> photolist, Context context){

        this.photolist = photolist;
        this.context = context;
        this.selected = new ArrayList<>();

    }
    @NonNull
    @Override
    public DisplayImagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_list_item, parent, false);
        return new DisplayImagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DisplayImagesViewHolder holder , int position) {


        final DisplayImagesModel displayImagesModel = photolist.get(position);
        //holder.tvAuthor.setText(moviz.getReviews().getAuthor());
      //  Picasso.with(context).load(displayImagesModel.getJournalImageView()).into(holder.ivJournalPhoto);

        Picasso.with(context).load(displayImagesModel.getJournalImageView()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.ic_mesut).into(holder.ivJournalPhoto, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(context).load(displayImagesModel.getJournalImageView()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.ic_mesut).into(holder.ivJournalPhoto, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                    }
                });
            }
        });


        holder.cbaddJournalCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selected.contains(displayImagesModel)){
                    selected.remove(displayImagesModel);
                    unhighlightView(holder);
                    holder.cbaddJournalCheck.setChecked(false);
                } else {
                    selected.add(displayImagesModel);
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

                for (DisplayImagesModel image : photolist) {

                    arrimageFile.add(image.getJournalImageView());
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
                if (selected.contains(displayImagesModel)) {
                    selected.remove(displayImagesModel);
                    unhighlightView(holder);
                } else {
                    selected.add(displayImagesModel);
                    highlightView(holder);
                }

                receiver.onClickAction();
                return true;
            }
        });

        if (selected.contains(displayImagesModel)) {
            highlightView(holder);
        }
        else {
            unhighlightView(holder);
        }


        ViewGroup.LayoutParams lp = holder.ivJournalPhoto.getLayoutParams();
        if (lp instanceof FlexboxLayoutManager.LayoutParams) {
            FlexboxLayoutManager.LayoutParams flexboxLp = (FlexboxLayoutManager.LayoutParams)lp;
            flexboxLp.setFlexGrow(1.0f);        }

    }


    public void highlightView(DisplayImagesViewHolder holder) {
        holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        holder.cbaddJournalCheck.setVisibility(View.VISIBLE);
        holder.cbaddJournalCheck.setChecked(true);

    }

    public void unhighlightView(DisplayImagesViewHolder holder) {
        holder.itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        holder.cbaddJournalCheck.setVisibility(View.INVISIBLE);
        holder.cbaddJournalCheck.setChecked(false);
    }

    public void unhighlightViewOnCancel(DisplayImagesViewHolder holder) {
        holder.itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        holder.cbaddJournalCheck.setVisibility(View.INVISIBLE);
        holder.cbaddJournalCheck.setChecked(false);
    }


    public void addAll(List<DisplayImagesModel> items) {
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

    public List<DisplayImagesModel> getSelected() {
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
