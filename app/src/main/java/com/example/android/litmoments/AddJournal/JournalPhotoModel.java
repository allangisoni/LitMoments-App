package com.example.android.litmoments.AddJournal;


import android.net.Uri;

import java.io.File;

public class JournalPhotoModel {


    private Uri journalImageView;


    public JournalPhotoModel(){

        this.journalImageView=journalImageView;
    }

    public Uri getJournalImage() {
        return journalImageView;
    }

    public void setJournalImage(Uri journalImageView) {
        this.journalImageView = journalImageView;
    }
}
