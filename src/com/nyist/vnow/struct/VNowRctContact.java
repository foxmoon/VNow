package com.nyist.vnow.struct;

public class VNowRctContact {
    private String mStrUserId;
    private String mStrUserName;
    private String mStrContactName;
    private String mStrConPhone;
    private long mCallTime;
    private boolean mIsCallIn;

    public String getmStrUserId() {
        return mStrUserId;
    }

    public void setmStrUserId(String mStrUserId) {
        this.mStrUserId = mStrUserId;
    }

    public String getmStrUserName() {
        return mStrUserName;
    }

    public void setmStrUserName(String mStrUserName) {
        this.mStrUserName = mStrUserName;
    }

    public String getmStrContactName() {
        return mStrContactName;
    }

    public void setmStrContactName(String mStrContactName) {
        this.mStrContactName = mStrContactName;
    }

    public String getmStrConPhone() {
        return mStrConPhone;
    }

    public void setmStrConPhone(String mStrConPhone) {
        this.mStrConPhone = mStrConPhone;
    }

    public long getmCallTime() {
        return mCallTime;
    }

    public void setmCallTime(long mCallTime) {
        this.mCallTime = mCallTime;
    }

    public boolean ismIsCallIn() {
        return mIsCallIn;
    }

    public void setmIsCallIn(boolean mIsCallIn) {
        this.mIsCallIn = mIsCallIn;
    }
}
