package com.lit.litmoments.AddJournal;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.lit.litmoments.R;

import java.util.List;

public class JournalEntryViewHolder extends RecyclerView.ViewHolder {

    public ImageView ivaddJournalPhoto;
    public CheckBox cbaddJournalCheck;
    private List<JournalPhotoModel> items, selected;
    JournalEntryAdapater journalEntryAdapater;

    public JournalEntryViewHolder(View itemView) {
        super(itemView);
        ivaddJournalPhoto = itemView.findViewById(R.id.ivJournalPhoto);
        cbaddJournalCheck = itemView.findViewById(R.id.chJournalCheck);

    }
}

