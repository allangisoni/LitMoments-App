package com.example.android.litmoments.AddJournal;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JournalEntryModel implements Parcelable {

    private  String journalDate, journalLocation, journalWeather, journalMood, journalTitle, journalMessage, month, day, key;
    private List<String> journalImagePath = new ArrayList<>();

    public JournalEntryModel(String journalDate, String journalLocation, String journalWeather, String journalMood, String journalTitle, String journalMessage, String month, String day, ArrayList<String>journalImagePath, String key){

        this.journalDate = journalDate;
        this.journalLocation= journalLocation;
        this.journalWeather = journalWeather;
        this.journalMood = journalMood;
        this.journalTitle= journalTitle;
        this.journalMessage = journalMessage;
        this.month = month;
        this.day = day;
        this.journalImagePath = journalImagePath;
        this.key = key;
    }

    public JournalEntryModel(String journalDate, String journalLocation, String journalWeather, String journalMood, String journalTitle, String journalMessage, String month, String day, String key){

        this.journalDate = journalDate;
        this.journalLocation= journalLocation;
        this.journalWeather = journalWeather;
        this.journalMood = journalMood;
        this.journalTitle= journalTitle;
        this.journalMessage = journalMessage;
        this.month = month;
        this.day = day;
        this.key = key;
    }
    public JournalEntryModel(){

    }

    public String getJournalDate() {
        return journalDate;
    }

    public void setJournalDate(String journalDate) {
        this.journalDate = journalDate;
    }

    public String getJournalLocation() {
        return journalLocation;
    }

    public void setJournalLocation(String journalLocation) {
        this.journalLocation = journalLocation;
    }


    public String getJournalWeather() {
        return journalWeather;
    }

    public void setJournalWeather(String journalWeather) {
        this.journalWeather = journalWeather;
    }

    public String getJournalMood() {
        return journalMood;
    }

    public void setJournalMood(String journalMood) {
        this.journalMood = journalMood;
    }

    public String getJournalTitle() {
        return journalTitle;
    }

    public void setJournalTitle(String journalTitle) {
        this.journalTitle = journalTitle;
    }

    public String getJournalMessage() {
        return journalMessage;
    }

    public void setJournalMessage(String journalMessage) {
        this.journalMessage = journalMessage;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public List<String> getJournalImagePath() {
        return journalImagePath;
    }

    public void setJournalImagePath(List<String> journalImagePath) {
        this.journalImagePath = journalImagePath;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private JournalEntryModel(Parcel parcel){

        journalDate = parcel.readString();
        journalLocation = parcel.readString();
        journalWeather = parcel.readString();
        journalMood = parcel.readString();
        journalTitle = parcel.readString();
        journalMessage = parcel.readString();
        key = parcel.readString();
        journalImagePath = new ArrayList();
        parcel.readList(this.journalImagePath, Integer.class.getClassLoader());



        //read and set saved values from parcel
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(journalDate);
        dest.writeString(journalLocation);
        dest.writeString(journalWeather);
        dest.writeString(journalMood);
        dest.writeString(journalTitle);
        dest.writeString(journalMessage);
        dest.writeString(key);
        dest.writeList(journalImagePath);


    }




    public static final  Parcelable.Creator<JournalEntryModel> CREATOR = new Parcelable.Creator<JournalEntryModel>(){

        @Override
        public JournalEntryModel createFromParcel(Parcel source) {
            return new JournalEntryModel(source);
        }

        @Override
        public JournalEntryModel[] newArray(int size) {
            return new JournalEntryModel[0];
        }
    };
}
