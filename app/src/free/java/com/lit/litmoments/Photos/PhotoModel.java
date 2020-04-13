package com.lit.litmoments.Photos;

public class PhotoModel {

    private String imageUrl;

    private String journalKey;

    public PhotoModel(){

    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getJournalKey() {
        return journalKey;
    }

    public void setJournalKey(String journalKey) {
        this.journalKey = journalKey;
    }
}
