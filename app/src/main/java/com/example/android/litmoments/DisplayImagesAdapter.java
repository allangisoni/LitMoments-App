package com.example.android.litmoments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class DisplayImagesAdapter extends RecyclerView.Adapter<DisplayImagesViewHolder> {

    private final List<DisplayImagesModel> photolist;
    private final Context context;
    int file;

    private final DisplayImagesAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(DisplayImagesModel photoItem);
    }



    public  DisplayImagesAdapter(List<DisplayImagesModel> photolist, Context context, DisplayImagesAdapter.OnItemClickListener listener){

        this.photolist = photolist;
        this.context = context;
        this.listener = listener;

    }
    @NonNull
    @Override
    public DisplayImagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_list_item, parent, false);
        return new DisplayImagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DisplayImagesViewHolder holder ,int position) {

        holder.bind(photolist.get(position), listener);
        //JournalPhotoModel journalPhotoModel = photolist.get(position);
        //holder.tvAuthor.setText(moviz.getReviews().getAuthor());
    }

    @Override
    public int getItemCount() {
        return photolist.size();
    }
}
