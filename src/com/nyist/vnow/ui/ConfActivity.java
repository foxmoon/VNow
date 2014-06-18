package com.nyist.vnow.ui;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nyist.vnow.R;
import com.nyist.vnow.adapter.VNSdFileAdapter;
import com.nyist.vnow.adapter.VNSdFileAdapter.FileListener;
import com.nyist.vnow.adapter.VNowFileUpdateAdapter;
import com.nyist.vnow.core.VNowApplication;
import com.nyist.vnow.core.VNowCore;
import com.nyist.vnow.dialog.VNowAlertDlg;
import com.nyist.vnow.fragment.VNowFragmentFile;
import com.nyist.vnow.fragment.VNowFragmentPhoto;
import com.nyist.vnow.fragment.VNowFragmentSynPic;
import com.nyist.vnow.struct.FileUpdate;
import com.nyist.vnow.struct.UploadCaptrue;
import com.nyist.vnow.utils.DES;
import com.nyist.vnow.utils.Session;
import com.nyist.vnow.utils.ToastUtil;
import com.vnow.sdk.openapi.EventListener;

/**
 * 接听和聊天界面
 * @author harry
 * @version Creat on 2014-6-18上午8:39:40
 */
public class ConfActivity extends FragmentActivity implements OnClickListener, FileListener {
    private final int CODE_OPEN_LOACLVODEO = 0x01;
    private int STOP_TIME = 0x02;
    private int UPDATE_TIME = 0x03;
    private SurfaceView mLayoutScreen;
    private SurfaceView mLayoutSubScrn;
    private SurfaceHolder mVidPreviewSurfaceHolder = null;
    private SurfaceHolder mVidRemoteSurfaceHolder = null;
    private CheckBox mCheckVideo;
    private CheckBox mCheckAudio;
    private CheckBox mCheckUpLoadVideo;
    private Button mBtnHangUp;
    private Button mBtnUploadCapture;
    private Button mBtnUploadFile;
    private ImageView mImgIcon;
    private TextView mTxtCallName;
    private Chronometer mTxtLastTime;
    private View mP2pCallView;
    private TextView mTxtContactName;
    private Chronometer mTxtCallTime;
    private Button mBtnAccept;
    private Button mBtnRefused;
    private Dialog mDialog;
    private String mCallNum;
    private String mCallName;
    private String mVideoName;
    private VNowCore mCore;
    private MyEventListener mMyListener;
    private boolean mIsActivity = false;
    private VNowFragmentPhoto mPhotoFragment;
    private VNowFragmentFile mFileFragment;
    private VNowFragmentSynPic mSynPicFragment;
    private FragmentManager mFmanager;
    private String mVideoParams;
    private boolean mIsCallin = false;
    // If we try to check the keyguard more than 5 times, just launch the full
    // screen activity.
    private int mKeyguardRetryCount;
    private final int MAX_KEYGUARD_CHECKS = 5;
    private int mTimer = 30;
    private int mStatus = STOP_TIME;
    private Handler mUIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            int code = msg.what;
            if (code == CODE_OPEN_LOACLVODEO) {
                openLocalVideo(true);
            }
            else if (msg.what == UPDATE_TIME) {
                mTimer--;
                if (mTimer < 0) {
                    mBtnRefused.performClick();
                }
                else if (mStatus == UPDATE_TIME) {
                    mUIHandler.sendEmptyMessageDelayed(UPDATE_TIME, 1000);
                }
            }
        }
    };
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            handleScreenOff((KeyguardManager) msg.obj);
        };
    };
    private Handler mMainHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (!ConfActivity.this.isFinishing()) {
                showCallFailedDlg(msg.obj.toString());
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        unLockScreen();
        final View view = View.inflate(this, R.layout.activity_conf, null);
        setContentView(view);
        AlphaAnimation alphaAnim = new AlphaAnimation(0.0f, 1.0f);
        alphaAnim.setDuration(300);
        view.setAnimation(alphaAnim);
        mCore = VNowApplication.newInstance().getCore();
        mMyListener = new MyEventListener();
        mCore.doSetEventListener(mMyListener);
        loadSatrtUI();
        mFmanager = getSupportFragmentManager();
        Intent intent = getIntent();
        mIsCallin = intent.getBooleanExtra("isCallin", false);
        if (mIsCallin) {
            mCore.openRingCalling(true);
            mBtnAccept.setVisibility(View.VISIBLE);
            mCallNum = getIntent().getStringExtra("callNumber");
            mCallName = getIntent().getStringExtra("callName");
            mTxtContactName.setText(mCallName);
            mTxtCallName.setText(getString(R.string.str_conf_title).replace("${NAME}", mCallName));
        }
        else {
            startTimer();
            mCallNum = getIntent().getStringExtra("callNumber");
            mCallName = getIntent().getStringExtra("callName");
            mTxtContactName.setText(mCallName);
            mTxtCallName.setText(getString(R.string.str_conf_title).replace("${NAME}", mCallName));
            if (!mIsActivity)
                mCore.doP2pCall(mCallNum);
            mBtnAccept.setVisibility(View.GONE);
        }
        mVideoParams = Session.newInstance(ConfActivity.this).getVideoParams();
    }

    /**
     * 解锁屏幕
     */
    private void unLockScreen() {
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        // Turn on the screen unless we are being launched from the AlarmAlert
        // subclass.
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        registerReceiver(mScreenOffReceiver, new IntentFilter(
                Intent.ACTION_SCREEN_OFF));
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        if (mIsActivity) {
            mP2pCallView.setVisibility(View.GONE);
            mCore.dosetRemoteVidSurface(mVidRemoteSurfaceHolder.getSurface());
            if (mCheckVideo.isChecked()) {
                mUIHandler.sendEmptyMessageDelayed(CODE_OPEN_LOACLVODEO, 2000);
            }
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mMainHandler.removeMessages(0);
        if (mCheckVideo.isChecked()) {
            openLocalVideo(false);
        }
        mCore.dosetRemoteVidSurface(null);
    }

    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mCore.doP2pHangup();
        distoryChannel();
        unregisterReceiver(mScreenOffReceiver);
        // Remove any of the keyguard messages just in case
        mHandler.removeMessages(0);
        mCore.doRemoveEventListener(mMyListener);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if (null != mFileView && mFileView.getVisibility() == View.VISIBLE) {
            mFileView.setVisibility(View.GONE);
        }
        if (null != mFileUpdateView
                && mFileUpdateView.getVisibility() == View.VISIBLE) {
            ToastUtil.getInstance(ConfActivity.this).showShort("正在传输文件，不能退出！");
        }
        else {
            showExitDlg();
        }
    }

    @SuppressLint("NewApi")
    private void loadSatrtUI() {
        mP2pCallView = findViewById(R.id.layout_2p2_call);
        mTxtContactName = (TextView) mP2pCallView.findViewById(R.id.txt_contact_call_name);
        mTxtCallTime = (Chronometer) mP2pCallView.findViewById(R.id.txt_call_time);
        mBtnRefused = (Button) mP2pCallView.findViewById(R.id.btn_refused);
        mBtnAccept = (Button) mP2pCallView.findViewById(R.id.btn_accept);
        mBtnAccept.setVisibility(View.GONE);
        mTxtCallTime.start();
        mBtnRefused.setOnClickListener(this);
        mBtnAccept.setOnClickListener(this);
        mLayoutSubScrn = (SurfaceView) this.findViewById(R.id.conf_sub_view);
        mVidPreviewSurfaceHolder = mLayoutSubScrn.getHolder();
        mCheckVideo = (CheckBox) findViewById(R.id.conf_open_video_tab);
        mCheckAudio = (CheckBox) findViewById(R.id.conf_open_audio_tab);
        mCheckUpLoadVideo = (CheckBox) findViewById(R.id.conf_upload_video_tab);
        mLayoutScreen = (SurfaceView) this.findViewById(R.id.conf_screen);
        mBtnHangUp = (Button) this.findViewById(R.id.conf_hangup_btn);
        mBtnUploadCapture = (Button) this.findViewById(R.id.conf_upload_capture_tab);
        mBtnUploadFile = (Button) this.findViewById(R.id.conf_upload_file_tab);
        mTxtCallName = (TextView) this.findViewById(R.id.conf_title_txt);
        mImgIcon = (ImageView) this.findViewById(R.id.conf_defult_img);
        mTxtLastTime = (Chronometer) this.findViewById(R.id.conf_last_time);
        mSynPicFragment = (VNowFragmentSynPic) getSupportFragmentManager().findFragmentById(R.id.layout_syn_pic);
        mPhotoFragment = (VNowFragmentPhoto) getSupportFragmentManager().findFragmentById(R.id.layout_right_panle);
        mSynPicFragment.setUIVisibility(View.GONE);
        mPhotoFragment.setUIVisibility(View.GONE);
        mVidRemoteSurfaceHolder = mLayoutScreen.getHolder();
        mCheckVideo.setOnCheckedChangeListener(mCheckListener);
        mCheckAudio.setOnCheckedChangeListener(mCheckListener);
        mCheckUpLoadVideo.setOnCheckedChangeListener(mCheckListener);
        mBtnHangUp.setOnClickListener(this);
        mBtnUploadCapture.setOnClickListener(this);
        mBtnUploadCapture.setEnabled(false);
        mBtnUploadFile.setOnClickListener(this);
    }

    protected void startTimer() {
        mTimer = 30;
        mStatus = UPDATE_TIME;
        mUIHandler.sendEmptyMessage(UPDATE_TIME);
    }

    protected void stopTimer() {
        mStatus = STOP_TIME;
    }

    private void distoryChannel() {
        mCore.doRemoveEventListener(mMyListener);
        mCore.dosetRemoteVidSurface(null);
        if (mCheckVideo.isChecked())
            mCore.doCloseLocalVideo();
        mTxtLastTime.stop();
        ConfActivity.this.finish();
    }

    private void answerCall() {
        stopTimer();
        mP2pCallView.setVisibility(View.GONE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mTxtCallTime.stop();
        mCore.dosetRemoteVidSurface(mVidRemoteSurfaceHolder.getSurface());
        mImgIcon.setVisibility(View.GONE);
        mLayoutSubScrn.bringToFront();
        mTxtLastTime.start();
        mIsActivity = true;
    }

    private void showExitDlg() {
        if (null != mDialog) {
            mDialog.dismiss();
            mDialog = null;
        }
        mDialog = new VNowAlertDlg(this, R.style.navComSettingDialogTheme);
        mDialog.show();
        ((VNowAlertDlg) mDialog).setTitle(R.string.str_alert_tip);
        ((VNowAlertDlg) mDialog).setContent(getString(R.string.str_conf_exit_title));
        ((VNowAlertDlg) mDialog).setOKButton(getString(R.string.str_ok),
                new VNowAlertDlg.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        // TODO Auto-generated method stub
                        mCore.doP2pHangup();
                        distoryChannel();
                    }
                });
        ((VNowAlertDlg) mDialog).setCancelButton(getString(R.string.str_cancel),
                new VNowAlertDlg.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        // TODO Auto-generated method stub
                    }
                });
    }

    public void showCallFailedDlg(String resion) {
        if (!resion.equals("0")) {
            String strContent = "";
            if (resion.equals("7")) {
                strContent = getString(R.string.str_other_offine);
            }
            else if (resion.equals("13") || resion.equals("14")) {
                strContent = getString(R.string.str_other_busy);
            }
            if (null != mDialog) {
                mDialog.dismiss();
                mDialog = null;
            }
            mDialog = new VNowAlertDlg(this, R.style.navComSettingDialogTheme);
            mDialog.show();
            ((VNowAlertDlg) mDialog).setTitle(R.string.str_call_field_resion);
            ((VNowAlertDlg) mDialog).setContent(strContent);
            ((VNowAlertDlg) mDialog).setOKButton(getString(R.string.str_ok),
                    new VNowAlertDlg.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            // TODO Auto-generated method stub
                            mCore.doP2pHangup();
                            distoryChannel();
                            ConfActivity.this.finish();
                        }
                    });
        }
        else {
            ToastUtil.getInstance(ConfActivity.this).showShort(getString(R.string.str_waiting_anser));
        }
    }

    public void openLocalVideo(boolean isOpen) {
        mBtnUploadCapture.setEnabled(isOpen);
        mCheckUpLoadVideo.setEnabled(isOpen);
        if (isOpen) {
            mCore.doOpenLocalVideo(mVidPreviewSurfaceHolder.getSurface(), "0" + mVideoParams);
        }
        else {
            mCore.doCloseLocalVideo();
        }
    }

    private OnCheckedChangeListener mCheckListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // TODO Auto-generated method stub
            int id = buttonView.getId();
            switch (id) {
                case R.id.conf_open_video_tab: {
                    openLocalVideo(isChecked);
                }
                    break;
                case R.id.conf_upload_video_tab: {
                    if (isChecked) {
                        mCore.doStartVdoRecode();
                    }
                    else {
                        mCore.doStopVdoRecode();
                    }
                }
                    break;
                case R.id.conf_open_audio_tab: {
                    if (isChecked) {}
                }
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int id = v.getId();
        switch (id) {
            case R.id.btn_accept: {
                mCore.doP2pAnswer();
                answerCall();
                mCore.openRingCalling(false);
            }
                break;
            case R.id.btn_refused: {
                if (mIsCallin) {
                    mCore.openRingCalling(false);
                }
                mCore.doP2pHangup();
                mTxtCallTime.stop();
                ConfActivity.this.finish();
            }
                break;
            case R.id.conf_hangup_btn: {
                showExitDlg();
            }
                break;
            case R.id.conf_upload_capture_tab: {
                mCore.doCapture();
                mBtnUploadCapture.setEnabled(false);
            }
                break;
            case R.id.conf_upload_file_tab: {
                mSynPicFragment.setUIVisibility();
            }
                break;
        }
    }

    /**
     * web 接口调用回调接口
     * 
     * @author Administrator
     * 
     */
    private class MyEventListener extends EventListener {
        @Override
        public void onResponseApiStatus(String status) {
            // TODO Auto-generated method stub
            super.onResponseApiStatus(status);
            if (status.equals("0")) {
                if (mIsCallin) {
                    mCore.openRingCalling(false);
                }
                mCore.doP2pHangup();
                mTxtCallTime.stop();
                ConfActivity.this.finish();
            }
            else {}
        }

        public void onResponseCall(boolean bSuccess) {}

        public void onResponseHangup(boolean bSuccess) {
            if (mP2pCallView.getVisibility() == View.GONE) {
                distoryChannel();
            }
            else {
                if (mIsCallin) {
                    mCore.openRingCalling(false);
                }
                ConfActivity.this.finish();
            }
        }

        public void onResponseCallFailed(String resion) {
            Message msg = new Message();
            msg.obj = resion;
            mMainHandler.sendMessage(msg);
        }

        public void onAnswerCall(boolean bSuccess) {
            answerCall();
        }

        public void onResponseCallIn(String fromName) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onResponseCapture(String picPath, boolean isSuccsee) {
            // TODO Auto-generated method stub
            if (isSuccsee) {
                mPhotoFragment.flashAdapter();
                if (!mPhotoFragment.isUIVisibility()) {
                    if (mCore.getmListPhotoUpdate().size() > 0) {
                        mCore.doUpLoadFileConf(mCore.getmListPhotoUpdate().get(0).getPhotoPath());
                    }
                    mPhotoFragment.setUIVisibility(View.VISIBLE);
                }
            }
            else {
                ToastUtil.getInstance(ConfActivity.this).showShort(getString(R.string.str_insert_list_fialed));
            }
            mBtnUploadCapture.setEnabled(true);
        }

        @Override
        public void onResponseConfUpLoadFile(String handID, String progress,
                String filePath, boolean isSuccess) {
            if (isSuccess) {
                String oldName = "";
                if (progress.equals("100")) {
                    String fileType = filePath.substring(filePath.lastIndexOf(".") + 1);
                    if (fileType.equals("jpg")) {
                        if (mCore.getmListPhotoUpdate().size() > 0) {
                            oldName = mCore.getmListPhotoUpdate().get(0).getPhotoName();
                            mCore.getmListPhotoUpdate().get(0).setNewPath(filePath);
                        }
                    }
                    else if (fileType.equals("mp4")) {
                        oldName = mVideoName;
                    }
                    mCore.doHttpWebUpLoad(filePath, oldName, "2", "android", mCallNum, fileType);
                }
            }
            else {
                if (mCore.getmListPhotoUpdate().size() > 0) {
                    UploadCaptrue captrue = mCore.getmListPhotoUpdate().get(0);
                    String fileName = captrue.getPhotoName();
                    map.put(fileName,
                            map.get(fileName) == null ? 1 : map.get(fileName) + 1);
                    if (map.get(fileName) <= 1) {
                        ToastUtil.getInstance(ConfActivity.this).showShort(getString(R.string.str_uppic_again));
                        mCore.doUpLoadFileConf(captrue.getPhotoPath());
                    }
                    else {
                        mPhotoFragment.updateImgList();
                        mPhotoFragment.flashAdapter();
                        ToastUtil.getInstance(ConfActivity.this).showShort(getString(R.string.str_uppic_fialed));
                    }
                }
            }
            super.onResponseConfUpLoadFile(handID, progress, filePath, isSuccess);
        }

        @Override
        public void onResponseUploadFileHttp(boolean bSuccess, String resUuid, String fileType) {
            // TODO Auto-generated method stub
            super.onResponseUploadFileHttp(bSuccess, resUuid, fileType);
            if (bSuccess) {
                if (fileType.equals("jpg")) {
                    if (mCore.getmListPhotoUpdate().size() > 0) {
                        UploadCaptrue captrue = mCore.getmListPhotoUpdate().get(0);
                        mCore.doSendTransChannel((new DES()).decrypt(mCallNum), captrue.getNewPath());
                        mPhotoFragment.updateImgList();
                        mPhotoFragment.flashAdapter();
                        if (mCore.getmListPhotoUpdate().size() > 0) {
                            mCore.doUpLoadFileConf(mCore.getmListPhotoUpdate().get(0).getPhotoPath());
                        }
                    }
                }
                else if (fileType.equals("mp4")) {
                    ToastUtil.getInstance(ConfActivity.this).showShort(getString(R.string.str_upvideo_success));
                }
            }
            else {
                if (mCore.getmListPhotoUpdate().size() > 0 && fileType.equals("jpg")) {
                    UploadCaptrue captrue = mCore.getmListPhotoUpdate().get(0);
                    String fileName = captrue.getPhotoName();
                    map.put(fileName,
                            map.get(fileName) == null ? 1 : map.get(fileName) + 1);
                    if (map.get(fileName) <= 1) {
                        ToastUtil.getInstance(ConfActivity.this).showShort(getString(R.string.str_uppic_again));
                        if (null == captrue.getNewPath()) {
                            ToastUtil.getInstance(ConfActivity.this).showShort(getString(R.string.str_uppic_fialed));
                            mPhotoFragment.updateImgList();
                            return;
                        }
                        mCore.doHttpWebUpLoad(captrue.getNewPath(), captrue.getPhotoName(), "2", "android", mCallNum, fileType);
                    }
                    else {
                        mPhotoFragment.updateImgList();
                        ToastUtil.getInstance(ConfActivity.this).showShort(getString(R.string.str_uppic_fialed));
                    }
                }
            }
            if (mCore.getmListPhotoUpdate().size() == 0)
                mPhotoFragment.setUIVisibility(View.GONE);
        }

        @Override
        public void onResponseSynTransport(String contentUrl, String srcID, boolean isSuccess) {
            if (isSuccess) {
                mSynPicFragment.setUIVisibility(View.VISIBLE);
                mSynPicFragment.updateImgList(contentUrl);
            }
        }

        @Override
        public void onResponseVdoRecode(String vdoPath, boolean isSuccsee) {
            // TODO Auto-generated method stub
            if (isSuccsee) {
                ToastUtil.getInstance(ConfActivity.this).showShort(
                        "本地视频已录制完成" + vdoPath);
                mVideoName = vdoPath.substring(vdoPath.lastIndexOf(".") + 1);
                mCore.doUpLoadFileConf(vdoPath);
            }
            else {
                ToastUtil.getInstance(ConfActivity.this).showShort(
                        "本地视频已录制失败！");
            }
        }
    }

    private Map<String, Integer> map = new HashMap<String, Integer>();
    private ListView mFileList;
    private File mCurrentFile;
    private String mSdcardPath;
    private VNSdFileAdapter mFileAdapter;
    private View mFileView;
    private Button mBtnBack;

    private void loadDOCUI() {
        mSdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File root = null;
        if (!TextUtils.isEmpty(mSdcardPath) && new File(mSdcardPath).canRead()) {
            root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        }
        if (null == mFileView) {
            mFileView = findViewById(R.id.layout_right_include);
        }
        mFileView.setVisibility(View.VISIBLE);
        mFileList = (ListView) mFileView.findViewById(R.id.file_list);
        mBtnBack = (Button) mFileView.findViewById(R.id.btn_back);
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                backParent();
            }
        });
        mCurrentFile = root;
        mFileAdapter = new VNSdFileAdapter(this, mCurrentFile, this);
        mFileList.setAdapter(mFileAdapter);
    }

    private void backParent() {
        File current = mFileAdapter.getCurrentParent();
        try {
            if (!current.getCanonicalPath().equals(mSdcardPath)) {
                mCurrentFile = current.getParentFile();
                mFileAdapter.setData(mCurrentFile);
                mFileAdapter.notifyDataSetChanged();
            }
            else {
                ToastUtil.getInstance(ConfActivity.this).showShort( "目前已经在根目录");
            }
        }
        catch (Exception e) {
            // TODO: handle exception
        }
    }

    private void uploadFile(File file) {
        // String fileName = file.getName();
        // String prefix=fileName.substring(fileName.lastIndexOf(".")+1);
        // mCore.doUploadFileHttp(file.getPath(),fileName,"2",mCallNum,prefix,"android");
        mCore.doUpLoadFileConf(file.getPath());
    }

    private VNowFileUpdateAdapter mAdapterFileUpdate;
    private View mFileUpdateView;
    private ListView mListViewUpdate;

    private void loadFileUpdateUI() {
        if (null == mFileUpdateView) {
            mFileUpdateView = findViewById(R.id.layout_file_update);
        }
        mFileUpdateView.setVisibility(View.VISIBLE);
        mListViewUpdate = (ListView) findViewById(R.id.file_update_list);
        if (null == mAdapterFileUpdate) {
            mAdapterFileUpdate = new VNowFileUpdateAdapter(this,
                    mCore.getmListFileUpdate());
            mListViewUpdate.setAdapter(mAdapterFileUpdate);
        }
        else {
            mAdapterFileUpdate.notifyDataSetChanged();
        }
    }

    @Override
    public void onClickFile(File file) {
        // TODO Auto-generated method stub
        FileUpdate update = new FileUpdate();
        update.setmFileName(file.getName());
        update.setmFile(file);
        if (mCore.addUploadFile(update)) {
            if (mCore.getmListFileUpdate().size() == 1) {
                uploadFile(file);
            }
            loadFileUpdateUI();
        }
        else {
            ToastUtil.getInstance(ConfActivity.this).showShort( "该文件已经在上传列表中!");
        }
    }

    private final BroadcastReceiver mScreenOffReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    KeyguardManager km =
                            (KeyguardManager) context.getSystemService(
                                    Context.KEYGUARD_SERVICE);
                    handleScreenOff(km);
                }
            };

    private boolean checkRetryCount() {
        if (mKeyguardRetryCount++ >= MAX_KEYGUARD_CHECKS) {
            Log.d("TAG", "Tried to read keyguard status too many times, bailing...");
            return false;
        }
        return true;
    }

    private void handleScreenOff(final KeyguardManager km) {
        if (!km.inKeyguardRestrictedInputMode() && checkRetryCount()) {
            if (checkRetryCount()) {
                mHandler.sendMessageDelayed(mHandler.obtainMessage(0, km), 500);
            }
        }
        else {}
    }
}
