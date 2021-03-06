package com.nyist.vnow.ui;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.VideoView;

import com.nyist.vnow.R;
import com.nyist.vnow.core.VNowApplication;
import com.nyist.vnow.core.VNowCore;
import com.nyist.vnow.fragment.VNowFragmentContacts;
import com.nyist.vnow.utils.LogTag;
import com.nyist.vnow.utils.Session;
import com.vnow.sdk.openapi.EventListener;

public class ConfenceActivity extends BaseFragmentActivity implements OnClickListener, OnCheckedChangeListener {
//    private SurfaceView mVideoViewOne;
//    private SurfaceView mVideoViewTwo;
    private VNowCore mCore;
    private MyEventListener mEventListener;
    private String mVideoParams;
    private SurfaceHolder mVidOneSurfaceHolder;
    private SurfaceHolder mVidTwoSurfaceHolder;
    private CheckBox mInvite;
    private VNowFragmentContacts mInviteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mCore.doP2pAnswer();
//        answerCall();
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_confence);
    }

    @Override
    protected void initializeData() {
//        creatChannel();
        LogTag.e("mVideoParams", mVideoParams);
    }

    /**
     * 部署视频通话环境
     */
    private void creatChannel() {
        mCore = VNowApplication.newInstance().getCore();
        mEventListener = new MyEventListener();
        mCore.doSetEventListener(mEventListener);
        mVideoParams = Session.newInstance(ConfenceActivity.this).getVideoParams();
    }

    @Override
    protected void initializeViews() {
       mInvite = (CheckBox) findViewById(R.id.mInvite);
       mInviteFragment = (VNowFragmentContacts) getSupportFragmentManager().findFragmentById(R.id.mInviteFragment);
       mInvite.setOnCheckedChangeListener(this);
       mInviteFragment.setUIVisibility(View.INVISIBLE);
    }

    public void openLocalVideo(boolean isOpen) {
        if (isOpen) {
            mCore.doOpenLocalVideo(mVidOneSurfaceHolder.getSurface(), "0" + mVideoParams);
        }
        else {
            mCore.doCloseLocalVideo();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        openLocalVideo(false);
//        mCore.dosetRemoteVidSurface(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        openLocalVideo(true);
//        mCore.dosetRemoteVidSurface(mVidTwoSurfaceHolder.getSurface());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        destoryChannel();
        finish();
    }

    /**
     * 销毁视频通话环境
     */
    private void destoryChannel() {
        mCore.doP2pHangup();
        mCore.doRemoveEventListener(mEventListener);
        mCore.dosetRemoteVidSurface(null);
        mCore.doCloseLocalVideo();
    }

    private class MyEventListener extends EventListener {
        @Override
        public void onResponseCall(boolean bSuccess) {
            super.onResponseCall(bSuccess);
        }

        @Override
        public void onResponseCallFailed(String resion) {
            super.onResponseCallFailed(resion);
        }

        @Override
        public void onAnswerCall(boolean bSuccess) {
//            answerCall();
        }

        @Override
        public void onResponseCallIn(String fromName) {
            super.onResponseCallIn(fromName);
        }
    }

    /**
     * 
     */
    private void answerCall() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        mCore.dosetRemoteVidSurface(mVidTwoSurfaceHolder.getSurface());
    }

    @Override
    public void onClick(View v) {
//        initInviteFragment();
        mInviteFragment.setUIVisibility(View.VISIBLE);
        
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            mInviteFragment.setUIVisibility(View.VISIBLE);
        }
        else {
            mInviteFragment.setUIVisibility(View.INVISIBLE);
        }
    }
}
