package com.example.android.litmoments.AddJournal;


import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

public class JournalPhotoModel implements Parcelable {


    private Uri journalImageView;


    public JournalPhotoModel(){

        this.journalImageView=journalImageView;
    }

    protected JournalPhotoModel(Parcel in) {
        journalImageView = in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Creator<JournalPhotoModel> CREATOR = new Creator<JournalPhotoModel>() {
        @Override
        public JournalPhotoModel createFromParcel(Parcel in) {
            return new JournalPhotoModel(in);
        }

        @Override
        public JournalPhotoModel[] newArray(int size) {
            return new JournalPhotoModel[size];
        }
    };

    public Uri getJournalImage() {
        return journalImageView;
    }

    public void setJournalImage(Uri journalImageView) {
        this.journalImageView = journalImageView;
    }


    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(journalImageView, flags);
    }
}
