package com.eng.arab.translator.androidtranslator.model;

/**
 * Created by keir on 11/1/2016.
 */

public class HomeButtonsModel {
    String title;
    String details;
    int photoId;

    public int getPhotoId() {
        return photoId;
    }

    public String getDetails() {
        return details;
    }

    public String getTitle() {
        return title;
    }

    public HomeButtonsModel(String title, String details, int photoId) {
        this.title = title;
        this.details = details;
        this.photoId = photoId;
    }
}
