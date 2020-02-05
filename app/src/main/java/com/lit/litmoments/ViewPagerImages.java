package com.lit.litmoments;

public class ViewPagerImages {

    private  String journalImagePath;

    public ViewPagerImages(){

    }

    public ViewPagerImages( String journalImagePath){
     this.journalImagePath = journalImagePath;

    }

    public String getJournalImagePath() {
        return journalImagePath;
    }

    public void setJournalImagePath(String journalImagePath) {
        this.journalImagePath = journalImagePath;
    }
}
