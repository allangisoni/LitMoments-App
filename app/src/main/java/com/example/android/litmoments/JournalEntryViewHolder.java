package com.example.android.litmoments;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class JournalEntryViewHolder extends RecyclerView.ViewHolder {

    public ImageView ivJournalPhoto;

    public JournalEntryViewHolder(View itemView) {
        super(itemView);
        ivJournalPhoto = itemView.findViewById(R.id.ivJournalPhoto);

    }

    public void bind(final JournalPhotoModel photoitem, final JournalEntryAdapater.OnItemClickListener listener) {

        Picasso.with(itemView.getContext()).load(photoitem.getJournalImage()).into(ivJournalPhoto);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                listener.onItemClick(photoitem);

                //Intent intent = new Intent(itemView.getContext(), DetailsActivity.class);
                //intent.putExtra("movieDetails", movizitem);
                //itemView.getContext().startActivity(intent);
            }
        });
    }
}

