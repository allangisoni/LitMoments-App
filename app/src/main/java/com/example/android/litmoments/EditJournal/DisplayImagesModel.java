package com.example.android.litmoments.EditJournal;

import android.os.Parcel;
import android.os.Parcelable;

public class DisplayImagesModel implements Parcelable {

    private String journalImageView;


    public DisplayImagesModel(){

        this.journalImageView = journalImageView;

    }

    protected DisplayImagesModel(Parcel in) {
        journalImageView = in.readString();
    }

    public static final Creator<DisplayImagesModel> CREATOR = new Creator<DisplayImagesModel>() {
        @Override
        public DisplayImagesModel createFromParcel(Parcel in) {
            return new DisplayImagesModel(in);
        }

        @Override
        public DisplayImagesModel[] newArray(int size) {
            return new DisplayImagesModel[0];
        }
    };

    public String getJournalImageView() {
        return journalImageView;
    }

    public void setJournalImageView(String journalImageView) {
        this.journalImageView = journalImageView;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(journalImageView);
    }
}
