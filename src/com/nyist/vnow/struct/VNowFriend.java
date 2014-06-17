package com.nyist.vnow.struct;

import android.graphics.Bitmap;

public class VNowFriend extends CommItem {
    private String mName;
    private String mPhoneNum;
    private Bitmap mPhoto;
    private String mNick;
    private String mHeadUrl;

    public VNowFriend() {
    }

    public VNowFriend(String mName, String mPhoneNum) {
        super();
        this.mName = mName;
        this.mPhoneNum = mPhoneNum;
    }

    public VNowFriend(String mName, String mNick, String mHeadUrl) {
        super();
        this.mName = mName;
        this.mNick = mNick;
        this.mHeadUrl = mHeadUrl;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmPhoneNum() {
        return mPhoneNum;
    }

    public void setmPhoneNum(String mPhoneNum) {
        this.mPhoneNum = mPhoneNum;
    }

    public Bitmap getmPhoto() {
        return mPhoto;
    }

    public void setmPhoto(Bitmap mPhoto) {
        this.mPhoto = mPhoto;
    }

    public String getmNick() {
        return mNick;
    }

    public void setmNick(String mNick) {
        this.mNick = mNick;
    }

    public String getmHeadUrl() {
        return mHeadUrl;
    }

    public void setmHeadUrl(String mHeadUrl) {
        this.mHeadUrl = mHeadUrl;
    }

    @Override
    public String toString() {
        return "AvcFriend [mName=" + mName + ",mPhoneNum=" + mPhoneNum + ", mPhoto=" + mPhoto
                + ", mNick=" + mNick + ", mHeadUrl=" + mHeadUrl + "]";
    }
}
