package com.nyist.vnow.ui;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

import com.nyist.vnow.R;
import com.nyist.vnow.core.VNowApplication;
import com.nyist.vnow.core.VNowCore;
import com.nyist.vnow.utils.CommonUtil;
import com.vnow.sdk.openapi.EventListener;

public class VNowVideoSigninActivity extends Activity {
    private static final int CAMERA_FACING_BACK = 0;
    private static final int CAMERA_FACING_FRONT = 1;
    // video thumbnail
    private static final int UPDATE_RECORD_STATE = 2;
    private static final int SIGNIN_PIC_START = 3;
    private static final int SIGNIN_VDO_START = 4;
    private static final int RECORD_TOTAL_TIME = 8;
    private static final int REMAIN_TIME_TO_REMIND = 8;
    private static final int UPLOAD_FINISHED = 0X500;
    private boolean mIsRcdingLight = true;
    private int mCurRecordTimeIndex = 0;
    private Timer mVideoRecordTimer;
    private TimerTask mVideoRecordTimerTask;
    // video record
    private File mRcdMediaFile;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private LinearLayout mLLayoutOperators;
    private LinearLayout mLLayoutVideo;
    private LinearLayout mLLayoutPicture;
    private RelativeLayout mRlayoutPreview;
    private TextView mTxtRcdRemainTime;
    private TextView mTxtRcdTime;
    private ImageView mImgRecording;
    private Button mBtnVideo;
    private Button mBtnPicture;
    private Button mBtnFinishPic;
    private Button mBtnFinishVideo;
    private ImageView mBtnSwitchCamera;
    private ProgressDialog mProcessImgProgressDialog;
    private EditText mEditRemark;
    private Camera mCamera;
    private CallBack mCallBack;
    private Parameters mParameters;
    Size mPictureSize = null;
    Size mPreviewSize = null;
    Size mVideoSize = null;
    int[] mPreviewFpsRange = null;
    private boolean mIsPreview = false;
    private int mPreviewHeight = 0;
    private int mPreviewWidth = 0;
    private MediaRecorder mMediaRecorder;
    private boolean mIsFrontCamera = false;
    private boolean mIsStartRcd;
    private boolean mIsShowFrame;
    private boolean mIsTakePic = false;
    private VNowCore mCore;
    private MyEventListener mCallBackListener;
    private String _oldName;
    private String _remark;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == UPDATE_RECORD_STATE && mIsStartRcd) {
                mTxtRcdRemainTime.setVisibility(View.INVISIBLE);
                mRlayoutPreview.setVisibility(View.VISIBLE);
                mCurRecordTimeIndex++;
                if (mIsRcdingLight) {
                    mImgRecording
                            .setImageResource(R.drawable.img_record_normal);
                    mIsRcdingLight = false;
                }
                else {
                    mImgRecording.setImageResource(R.drawable.img_recording);
                    mIsRcdingLight = true;
                }
                int time = mCurRecordTimeIndex / 2;
                if (time >= 0 && time <= 9) {
                    mTxtRcdTime.setText("0:0" + time);
                }
                else {
                    mTxtRcdTime.setText("0:" + time);
                }
                if ((time > RECORD_TOTAL_TIME - REMAIN_TIME_TO_REMIND)
                        && (time < RECORD_TOTAL_TIME)) {
                    mTxtRcdRemainTime.setVisibility(View.VISIBLE);
                    mTxtRcdRemainTime.setText("还剩  "
                            + (RECORD_TOTAL_TIME - time) + "  秒");
                }
                else if (time > RECORD_TOTAL_TIME) {
                    startOrStopRecord();
                    mImgRecording
                            .setImageResource(R.drawable.img_record_normal);
                }
            }
            else if (msg.what == UPLOAD_FINISHED) {
                if (null != mProcessImgProgressDialog) {
                    mProcessImgProgressDialog.dismiss();
                    mProcessImgProgressDialog = null;
                }
                if (mLLayoutVideo.getVisibility() == View.VISIBLE) {
                    mBtnFinishVideo.setVisibility(View.VISIBLE);
                    mImgRecording.setVisibility(View.GONE);
                }
            }
            else if (msg.what == SIGNIN_PIC_START) {
                if (!mIsTakePic) {
                    mCamera.autoFocus(mAutoFocusCallback);// 自动对焦
                    mBtnPicture.setEnabled(false);
                    mIsTakePic = true;
                    mBtnFinishPic.setText("拍照完成上传");
                }
                else {
                    finish();
                }
            }
            else if (msg.what == SIGNIN_VDO_START) {
                startOrStopRecord();
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vnow_video_sign_in);
        mCore = VNowApplication.the().getCore();
        mCallBackListener = new MyEventListener();
        mLLayoutOperators = (LinearLayout) findViewById(R.id.llayout_operators);
        mLLayoutPicture = (LinearLayout) findViewById(R.id.llayout_picture_operators);
        mLLayoutVideo = (LinearLayout) findViewById(R.id.llayout_video_operators);
        mBtnVideo = (Button) findViewById(R.id.btn_video_sign_in);
        mBtnPicture = (Button) findViewById(R.id.btn_picture_sign_in);
        mBtnFinishPic = (Button) findViewById(R.id.btn_upload_picture_finish);
        mBtnFinishVideo = (Button) findViewById(R.id.btn_upload_video_finish);
        mImgRecording = (ImageView) findViewById(R.id.btn_record_video);
        mBtnSwitchCamera = (ImageView) findViewById(R.id.img_switch_camera);
        mTxtRcdRemainTime = (TextView) findViewById(R.id.txt_rcd_remain_time);
        mTxtRcdTime = (TextView) findViewById(R.id.txt_record_time);
        mRlayoutPreview = (RelativeLayout) findViewById(R.id.rlayout_camera_preview);
        mSurfaceView = (SurfaceView) findViewById(R.id.surface_camera);
        mEditRemark = (EditText) findViewById(R.id.sign_remark_edit);
        mSurfaceHolder = mSurfaceView.getHolder();
        mCallBack = new CallBack();
        mSurfaceHolder.addCallback(mCallBack);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mBtnVideo.setOnClickListener(mBtnOnClickListener);
        mBtnPicture.setOnClickListener(mBtnOnClickListener);
        mImgRecording.setOnClickListener(mBtnOnClickListener);
        mBtnFinishPic.setOnClickListener(mBtnOnClickListener);
        mBtnFinishVideo.setOnClickListener(mBtnOnClickListener);
        mBtnSwitchCamera.setOnClickListener(mBtnOnClickListener);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mCore.doSetEventListener(mCallBackListener);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mCore.doRemoveEventListener(mCallBackListener);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mIsStartRcd || mIsTakePic) {
                VNowApplication.the().showToast("正在签到，不能退出！");
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * the method to init camera before use
     */
    private void initCamera() {
        if (mIsPreview) {
            mCamera.stopPreview();// stopCamera();
        }
        if (null != mCamera) {
            try {
                /* Camera Service settings */
                mParameters = mCamera.getParameters();
                // parameters.setFlashMode("off");
                mParameters.setPictureFormat(PixelFormat.JPEG);
                mParameters.setPreviewFormat(PixelFormat.YCbCr_420_SP);
                List<Size> pictureSizes = mCamera.getParameters()
                        .getSupportedPictureSizes();
                List<Size> previewSizes = mCamera.getParameters()
                        .getSupportedPreviewSizes();
                List<Size> videoSizes = mCamera.getParameters()
                        .getSupportedVideoSizes();
                List<Integer> previewFormats = mCamera.getParameters()
                        .getSupportedPreviewFormats();
                List<Integer> previewFrameRates = mCamera.getParameters()
                        .getSupportedPreviewFrameRates();
                List<int[]> previewFpsRange = mCamera.getParameters()
                        .getSupportedPreviewFpsRange();
                Size perfectPicSize = mCamera.new Size(1024, 768);
                Size perfectPreSize = mCamera.new Size(1024, 768);
                Size perfectVidSize = mCamera.new Size(640, 480);
                int[] perfectFpsRange = new int[2];
                perfectFpsRange[0] = 30000;
                perfectFpsRange[1] = 30000;
                mPictureSize = getPerfectSize(pictureSizes, perfectPicSize);
                mPreviewSize = getPerfectSize(previewSizes, perfectPreSize);
                mVideoSize = getPerfectSize(videoSizes, perfectVidSize);
                mPreviewFpsRange = getPerfectFpsRange(previewFpsRange, perfectFpsRange);
                if (mPictureSize != null) {
                    mParameters.setPictureSize(mPictureSize.width, mPictureSize.height);
                }
                else {
                    mParameters.setPictureSize(1024, 768);
                }
                if (mPreviewSize != null) {
                    mParameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
                }
                else {
                    mParameters.setPreviewSize(1024, 768);
                }
                if (mPreviewFpsRange != null) {
                    mParameters.setPreviewFpsRange(mPreviewFpsRange[0], mPreviewFpsRange[1]);
                }
                setCameraDisplayOrientation(mCamera);
                mCamera.setParameters(mParameters);
                mCamera.startPreview();
                mIsPreview = true;
                Camera.Size csize = mCamera.getParameters().getPreviewSize();
                mPreviewHeight = csize.height;
                mPreviewWidth = csize.width;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * the method to start or stop record when you press record key
     */
    private void startOrStopRecord() {
        mTxtRcdTime.setVisibility(View.VISIBLE);
        if (!mIsStartRcd) {
            mIsStartRcd = true;
            if (mRcdMediaFile != null) {
                File rcdFile = new File(mRcdMediaFile.getAbsolutePath());
                rcdFile.delete();
            }
            autoFocus();
            switchCamera(mSurfaceView, mIsFrontCamera);
            beginMediaRecorder();
            mVideoRecordTimerTask = new TimerTask() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(UPDATE_RECORD_STATE);
                }
            };
            mVideoRecordTimer = new Timer();
            mVideoRecordTimer.schedule(mVideoRecordTimerTask, 0, 500);
        }
        else {
            mTxtRcdRemainTime.setVisibility(View.GONE);
            mIsStartRcd = false;
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            mCurRecordTimeIndex = 0;
            mIsRcdingLight = true;
            if (null != mVideoRecordTimer) {
                mVideoRecordTimer.cancel();
                mVideoRecordTimer = null;
            }
            if (null != mVideoRecordTimerTask) {
                mVideoRecordTimerTask = null;
            }
            uploadFile(mRcdMediaFile);
        }
    }

    /**
     * the method to begin media record
     */
    public void beginMediaRecorder() {
        try {
            mRcdMediaFile = File.createTempFile("vnow_video_sign", ".mp4",
                    CommonUtil.getVideoDir());
            // String fileName = "vnow_video_sign.mp4";
            // mRcdMediaFile = new File(getFilesDir(), fileName);
            // 设置自动对焦
            Parameters param = mCamera.getParameters();
            List<String> list = param.getSupportedFocusModes();
            if (list != null) {
                for (int nIndex = 0; nIndex < list.size(); nIndex++) {
                    if (list.get(nIndex).equals(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                        param.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                        mCamera.setParameters(param);
                        break;
                    }
                }
            }
            mCamera.unlock();
            if (null == mMediaRecorder) {
                mMediaRecorder = new MediaRecorder();
            }
            mMediaRecorder.setCamera(mCamera);
            mMediaRecorder.setOrientationHint(90);
            mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            if (mVideoSize != null) {
                mMediaRecorder.setVideoSize(mVideoSize.width, mVideoSize.height);
            }
            else {
                mMediaRecorder.setVideoSize(640, 480);
            }
            // mMediaRecorder.setVideoFrameRate(30);
            mMediaRecorder.setVideoEncodingBitRate(2 * 1000 * 1000);
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mMediaRecorder.setMaxDuration(0);
            mMediaRecorder.setOutputFile(mRcdMediaFile.getAbsolutePath());
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * the method to set camera display orientation by configuration
     * 
     * @param camera
     */
    public void setCameraDisplayOrientation(Camera camera) {
        Camera.Parameters parameters = mCamera.getParameters();
        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            parameters.set("orientation", "portrait");
            parameters.set("rotation", 90);
            mCamera.setDisplayOrientation(90);
        }
        else {
            parameters.set("orientation", "landscape");
            mCamera.setDisplayOrientation(0);
        }
    }

    /**
     * the method to switch camera orientation,front or back
     * 
     * @param surfaceView
     * @param isFrontCamera
     */
    public void switchCamera(SurfaceView surfaceView, boolean isFrontCamera) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        int cameraId = isFrontCamera ? CAMERA_FACING_FRONT : CAMERA_FACING_BACK;// CAMERA_FACING_FRONT
        mCamera = Camera.open(cameraId);
        mParameters = mCamera.getParameters();
        // parameters.setFlashMode("off");
        mParameters.setPictureFormat(PixelFormat.JPEG);
        mParameters.setPreviewFormat(PixelFormat.YCbCr_420_SP);
        List<Size> pictureSizes = mCamera.getParameters()
                .getSupportedPictureSizes();
        List<Size> previewSizes = mCamera.getParameters()
                .getSupportedPreviewSizes();
        List<Size> videoSizes = mCamera.getParameters()
                .getSupportedVideoSizes();
        List<Integer> previewFormats = mCamera.getParameters()
                .getSupportedPreviewFormats();
        List<Integer> previewFrameRates = mCamera.getParameters()
                .getSupportedPreviewFrameRates();
        List<int[]> previewFpsRange = mCamera.getParameters()
                .getSupportedPreviewFpsRange();
        Size perfectPicSize = mCamera.new Size(1024, 768);
        Size perfectPreSize = mCamera.new Size(1024, 768);
        Size perfectVidSize = mCamera.new Size(640, 480);
        int[] perfectFpsRange = new int[2];
        perfectFpsRange[0] = 30000;
        perfectFpsRange[1] = 30000;
        mPictureSize = getPerfectSize(pictureSizes, perfectPicSize);
        mPreviewSize = getPerfectSize(previewSizes, perfectPreSize);
        mVideoSize = getPerfectSize(videoSizes, perfectVidSize);
        mPreviewFpsRange = getPerfectFpsRange(previewFpsRange, perfectFpsRange);
        if (mPictureSize != null) {
            mParameters.setPictureSize(mPictureSize.width, mPictureSize.height);
        }
        else {
            mParameters.setPictureSize(1024, 768);
        }
        if (mPreviewSize != null) {
            mParameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        }
        else {
            mParameters.setPreviewSize(1024, 768);
        }
        if (mPreviewFpsRange != null) {
            mParameters.setPreviewFpsRange(mPreviewFpsRange[0], mPreviewFpsRange[1]);
        }
        setCameraDisplayOrientation(mCamera);
        mCamera.setParameters(mParameters);
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showProcessImgProgressDialog(String msg) {
        mProcessImgProgressDialog = new ProgressDialog(
                VNowVideoSigninActivity.this);
        mProcessImgProgressDialog.setCancelable(true);
        mProcessImgProgressDialog.setTitle(msg);
        mProcessImgProgressDialog.setMessage("请稍后....");
        mProcessImgProgressDialog.show();
    }

    private void uploadPic(File file) {
        _oldName = file.getName();
        _remark = mEditRemark.getText().toString().trim();
        mCore.doUpLoadFileConf(file.getPath());
        showProcessImgProgressDialog("正在签到");
    }

    private void uploadFile(File file) {
        _oldName = file.getName();
        _remark = mEditRemark.getText().toString().trim();
        mCore.doUpLoadFileConf(file.getPath());
        showProcessImgProgressDialog("正在签到");
    }

    private Size getPerfectSize(List<Size> listSize, Size perfectSize) {
        Size size = null;
        if (listSize == null || perfectSize == null) {
            return size;
        }
        if (listSize.size() == 1) {
            size = listSize.get(0);
            return size;
        }
        int nPerfectSize = perfectSize.width * perfectSize.height;
        int nFirstSize = listSize.get(0).width * listSize.get(0).height;
        int nLastSize = listSize.get(listSize.size() - 1).width * listSize.get(listSize.size() - 1).height;
        if (nFirstSize > nLastSize) {
            int nIndex = listSize.size() - 1;
            size = listSize.get(nIndex);
            for (; nIndex > 0; nIndex--) {
                if (listSize.get(nIndex).width * listSize.get(nIndex).height <= nPerfectSize) {
                    size = listSize.get(nIndex);
                }
                else {
                    return size;
                }
            }
        }
        else {
            int nIndex = 0;
            size = listSize.get(nIndex);
            for (; nIndex < listSize.size(); nIndex++) {
                if (listSize.get(nIndex).width * listSize.get(nIndex).height <= nPerfectSize) {
                    size = listSize.get(nIndex);
                }
                else {
                    return size;
                }
            }
        }
        return size;
    }

    private int[] getPerfectFpsRange(List<int[]> listRange, int[] perfectRange) {
        int[] range = new int[2];
        range[0] = 0;
        range[1] = 0;
        if (listRange.size() == 0) {
            return null;
        }
        if (listRange.size() == 1) {
            int[] rng = listRange.get(0);
            range[0] = rng[0];
            range[1] = rng[1];
            return range;
        }
        else
        {
            for (int nIndex = 0; nIndex < listRange.size(); nIndex++) {
                int[] rng = listRange.get(nIndex);
                if (rng[0] == perfectRange[0] && rng[1] == perfectRange[1]) {
                    range[0] = rng[0];
                    range[1] = rng[1];
                    return range;
                }
                if (rng[1] == perfectRange[1]) {
                    range[0] = rng[0];
                    range[1] = rng[1];
                }
            }
            if (range[0] != 0 && range[1] != 0) {
                return range;
            }
        }
        return null;
    }

    /**
     * the method to set camera display orientation
     * 
     * @param camera
     */
    private void setDisplayOrientation(Camera camera) {
        try {
            Method method = Camera.class.getMethod("setDisplayOrientation",
                    int.class);
            if (method != null) {
                method.invoke(camera, 90);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class CallBack implements SurfaceHolder.Callback {
        @Override
        public void surfaceChanged(SurfaceHolder arg0, int format, int width,
                int height) {
            // TODO Auto-generated method stub
            mPreviewHeight = height;
            mPreviewWidth = width;
            initCamera();
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            mSurfaceHolder = holder;
            if (null == mCamera) {
                mCamera = Camera.open();
            }
            try {
                setDisplayOrientation(mCamera);
                mCamera.setPreviewDisplay(holder);
            }
            catch (IOException e) {
                if (mCamera != null) {
                    mCamera.stopPreview();
                    mCamera.release();
                    mCamera = null;
                }
                e.printStackTrace();
            }
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);
                mCamera.startPreview();
            }
            catch (Exception ex) {
                if (null != mCamera) {
                    mCamera.release();
                    mCamera = null;
                }
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            if (null != mCamera) {
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                mIsPreview = false;
                mCamera.release();
                mCamera = null;
            }
        }
    }

    /**
     * Create a file name for the icon photo using current time.
     */
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'vnow_sign_pic'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    /**
     * the method to auto focus for the camera if you touch screen
     * 
     * @param v
     * @param event
     */
    public void autoFocus() {
        if (mIsShowFrame) {
            return;
        }
        final ImageView imageView = new ImageView(this);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.lcamera_focus_frame1);
        imageView.setImageBitmap(bitmap);
        LayoutParams params = new RelativeLayout.LayoutParams(
                bitmap.getWidth(), bitmap.getHeight());
        params.leftMargin = (int) (mRlayoutPreview.getWidth() / 2 - bitmap.getWidth() / 2);
        params.topMargin = (int) (mRlayoutPreview.getHeight() / 2 - bitmap.getHeight() / 2);
        mRlayoutPreview.addView(imageView, params);
        imageView.setVisibility(View.VISIBLE);
        ScaleAnimation animation = new ScaleAnimation(1, 0.5f, 1, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        animation.setDuration(300);
        animation.setFillAfter(true);
        animation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(final Animation animation) {
                imageView.setImageResource(R.drawable.lcamera_focus_frame2);
                new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(400);
                            VNowVideoSigninActivity.this
                                    .runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            imageView
                                                    .setImageResource(R.drawable.lcamera_focus_frame3);
                                        }
                                    });
                            Thread.sleep(200);
                            VNowVideoSigninActivity.this
                                    .runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            imageView.clearAnimation();
                                            imageView.setVisibility(View.GONE);
                                            mIsShowFrame = false;
                                        }
                                    });
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    };
                }.start();
            }
        });
        imageView.startAnimation(animation);
        mIsShowFrame = true;
    }

    private PictureCallback mPictureCallback = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
            File file = new File(CommonUtil.getImagePath() + getPhotoFileName());
            BufferedOutputStream bos;
            try {
                bos = new BufferedOutputStream(new FileOutputStream(file));
                bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);// 将图片压缩到流中
                bos.flush();// 输出
                bos.close();// 关闭
                uploadPic(file);
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // String fileName = getPhotoFileName();
            // writeFileData(fileName, data);
            // uploadPic(new File(fileName));
            mBtnPicture.setEnabled(true);
            mIsTakePic = false;
            mCamera.stopPreview();
            mCamera.startPreview();
        }
    };
    private AutoFocusCallback mAutoFocusCallback = new AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            // TODO Auto-generated method stub
            autoFocus();
            Camera.Parameters params = mCamera.getParameters();
            params.setPictureFormat(PixelFormat.JPEG);
            List<Size> sizes = params.getSupportedPictureSizes();
            /*
             * Size size = null; if(sizes.size()>0){ size =
             * sizes.get(sizes.size()-1); } params.setPictureSize(size.width,
             * size.height);
             */
            if (mPictureSize != null) {
                params.setPictureSize(mPictureSize.width, mPictureSize.height);
            }
            else {
                params.setPictureSize(640, 480);
            }
            mCamera.setParameters(params);
            mCamera.takePicture(null, null, mPictureCallback);
        }
    };
    private View.OnClickListener mBtnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int id = v.getId();
            if (id == R.id.btn_picture_sign_in) {
                mEditRemark.setVisibility(View.GONE);
                mLLayoutOperators.setVisibility(View.GONE);
                mLLayoutPicture.setVisibility(View.VISIBLE);
                mBtnFinishPic.setVisibility(View.GONE);
                mBtnFinishPic.setText("拍照签到");
                mHandler.sendMessageDelayed(mHandler.obtainMessage(SIGNIN_PIC_START), 100);
            }
            else if (id == R.id.btn_video_sign_in) {
                mEditRemark.setVisibility(View.GONE);
                mLLayoutOperators.setVisibility(View.GONE);
                mLLayoutVideo.setVisibility(View.VISIBLE);
                mBtnFinishVideo.setVisibility(View.GONE);
                mHandler.sendMessageDelayed(mHandler.obtainMessage(SIGNIN_VDO_START), 100);
            }
            else if (id == R.id.btn_record_video) {
                // startOrStopRecord();
            }
            else if (id == R.id.btn_upload_video_finish) {
                finish();
            }
            else if (id == R.id.btn_upload_picture_finish) {
                if (!mIsTakePic) {
                    mCamera.autoFocus(mAutoFocusCallback);// 自动对焦
                    mBtnPicture.setEnabled(false);
                    mIsTakePic = true;
                    mBtnFinishPic.setText("拍照完成上传");
                }
                else {
                    finish();
                }
            }
            else if (id == R.id.img_switch_camera) {
                mIsFrontCamera = !mIsFrontCamera;
                switchCamera(mSurfaceView, mIsFrontCamera);
            }
        }
    };

    // web 接口回调
    private class MyEventListener extends EventListener {
        @Override
        public void onResponseUploadFileHttp(boolean bSuccess, String resUuid, String fileType) {
            // TODO Auto-generated method stub
            super.onResponseUploadFileHttp(bSuccess, resUuid, fileType);
            if (bSuccess) {
                mHandler.sendEmptyMessage(UPLOAD_FINISHED);
                mBtnPicture.setEnabled(true);
                VNowApplication.the().showToast("签到成功！");
                finish();
            }
            else {
                mHandler.sendEmptyMessage(UPLOAD_FINISHED);
                VNowApplication.the().showToast("签到失败！");
            }
            mBtnFinishPic.setVisibility(View.VISIBLE);
        }

        @Override
        public void onResponseConfUpLoadFile(String handID, String progress,
                String filePath, boolean isSuccess) {
            // TODO Auto-generated method stub
            if (isSuccess) {
                if (progress.equals("100")) {
                    String fileType = filePath.substring(filePath.lastIndexOf(".") + 1);
                    mCore.doHttpWebUpLoad(filePath, _oldName, "1", _remark, "1", fileType);
                    // mCore.doUploadFileHttp(filePath,file.getName(),"2",mShareID,"jpg","android");
                }
            }
            else {
                VNowApplication.the().showToast("签到失败！");
            }
            super.onResponseConfUpLoadFile(handID, progress, filePath, isSuccess);
        }
    }
}
