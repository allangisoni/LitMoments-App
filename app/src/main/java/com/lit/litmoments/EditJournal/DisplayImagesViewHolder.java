package com.lit.litmoments.EditJournal;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.lit.litmoments.R;

public class DisplayImagesViewHolder extends RecyclerView.ViewHolder {

    public ImageView ivJournalPhoto;
    public CheckBox cbaddJournalCheck;

    public DisplayImagesViewHolder(View itemView) {
        super(itemView);
        ivJournalPhoto = itemView.findViewById(R.id.ivJournalPhoto);
        cbaddJournalCheck = itemView.findViewById(R.id.chJournalCheck);

    }

}
