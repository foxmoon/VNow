package com.nyist.vnow.struct;

import java.io.File;

import android.graphics.Bitmap;

public class UploadCaptrue {
    private String photoName;
    private String photoPath;
    private String newPath;
    private Bitmap bmpPhoto;

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getNewPath() {
        return newPath;
    }

    public void setNewPath(String newPath) {
        this.newPath = newPath;
    }

    public Bitmap getBmpPhoto() {
        return bmpPhoto;
    }

    public void setBmpPhoto(Bitmap bmpPhoto) {
        this.bmpPhoto = bmpPhoto;
    }

    public void remove() {
        File file = new File(photoPath);
        if (file.exists())
            file.delete();
    }
}