package com.lit.litmoments.Photos;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.lit.litmoments.AddJournal.JournalEntryModel;
import com.lit.litmoments.R;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosViewHolder> {

    private Context mContext;
    private List<PhotoModel> mPhotoModelList;

    private final OnItemClickListener listener;

    public interface OnItemClickListener{
        void onItemClick (PhotoModel photoModel);
    }

    public PhotosAdapter(Context context, List<PhotoModel> photoModelList, OnItemClickListener listener){
        this.mContext = context;
        this.mPhotoModelList = photoModelList;
        this.listener = listener;
    }



    @NonNull
    @Override
    public PhotosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.photo_item_list, parent, false);
        return new PhotosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotosViewHolder holder, int position) {
        PhotoModel model = mPhotoModelList.get(position);
        holder.bind(model, listener);


    }

    @Override
    public int getItemCount() {
        return mPhotoModelList.size();
    }
}
