package com.nyist.vnow.ui;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.VideoView;

import com.nyist.vnow.R;
import com.nyist.vnow.core.VNowApplication;
import com.nyist.vnow.core.VNowCore;
import com.nyist.vnow.utils.LogTag;
import com.nyist.vnow.utils.Session;
import com.vnow.sdk.openapi.EventListener;

public class ConfenceActivity extends BaseActivity {
    private SurfaceView mVideoViewOne;
    private SurfaceView mVideoViewTwo;
    private VideoView mVideoViewThree;
    private VideoView mVideoViewFour;
    private VNowCore mCore;
    private MyEventListener mEventListener;
    private String mVideoParams;
    private SurfaceHolder mVidOneSurfaceHolder;
    private SurfaceHolder mVidTwoSurfaceHolder;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCore.doP2pAnswer();
        answerCall();
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_confence);
    }

    @Override
    protected void initializeData() {
        creatChannel();
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
        mVideoViewOne = (SurfaceView) findViewById(R.id.mVideoViewOne);
        mVideoViewTwo = (SurfaceView) findViewById(R.id.mVideoViewTwo);
        mButton = (Button) findViewById(R.id.mButton);
        mButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openLocalVideo(true);
            }
        });
//        mVideoViewThree = (VideoView) findViewById(R.id.mVideoViewThree);
//        mVideoViewFour = (VideoView) findViewById(R.id.mVideoViewFour);
        mVidOneSurfaceHolder = mVideoViewOne.getHolder();
        mVidTwoSurfaceHolder = mVideoViewTwo.getHolder();
        LogTag.e("initializeViews", "initializeViews");
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
        openLocalVideo(false);
        mCore.dosetRemoteVidSurface(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        openLocalVideo(true);
        mCore.dosetRemoteVidSurface(mVidTwoSurfaceHolder.getSurface());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destoryChannel();
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
            answerCall();
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
        mCore.dosetRemoteVidSurface(mVidTwoSurfaceHolder.getSurface());
    }
}
