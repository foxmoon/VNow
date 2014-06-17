package com.nyist.vnow.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import com.nyist.vnow.R;
import com.nyist.vnow.core.VNowApplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

public class UpdateSoftManager {
    private final int DOWNLOADING = 1;
    private final int DOWNLOAD_FINISH = 2;
    private final int APK_EXIST = 3;
    private final String mLocalSoftPackageName = "com.nyist.vnow";
    private String mDownloadVersionConfigUrl;
    private HashMap<String, String> mHashMapParseDetails;
    private String mSaveFileDir;
    private int mProgressDownload;
    private boolean mIsCancelUpadte = false;
    private Context mContext;
    private ProgressBar mProgressBarDownload;
    private Dialog mDownloadTipDialog;
    private int mLocalVersionCode;
    private int mServerVersionCode;
    private Dialog mDialog;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWNLOADING:
                    mProgressBarDownload.setProgress(mProgressDownload);
                    break;
                case DOWNLOAD_FINISH:
                    installApk();
                    break;
                case APK_EXIST:
                    ToastUtil.getInstance(mContext).showShort(mContext.getString(R.string.str_apk_already_exist));
                    installApk();
                default:
                    break;
            }
        };
    };

    public UpdateSoftManager(Context context) {
        this.mContext = context;
        this.mDownloadVersionConfigUrl = "http://" + CommonUtil._svrIP +
                "/vnow_android_app_update.xml";
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            mSaveFileDir = Environment.getExternalStorageDirectory() + "/"
                    + "vnowmsg";
        }
        else {
            Toast.makeText(mContext, mContext.getString(R.string.str_external_store_no_find), Toast.LENGTH_LONG).show();
        }
    }

    public void checkUpdate(boolean auto) {
        if (isNeedUpdate()) {
            showNoticeDialog();
        }
        else {
            if (!auto)
                Toast.makeText(mContext, R.string.soft_update_no, Toast.LENGTH_LONG)
                        .show();
        }
    }

    private boolean isNeedUpdate() {
        mLocalVersionCode = getLocalSoftVersionCode(mContext);
        mServerVersionCode = getServerSoftVersionName();
        if (mServerVersionCode == mLocalVersionCode)
            return false;
        if (mServerVersionCode > mLocalVersionCode) {
            return true;
        }
        return false;
    }

    private int getLocalSoftVersionCode(Context context) {
        int versionCode = 0;
        try {
            versionCode = context.getPackageManager().getPackageInfo(
                    mLocalSoftPackageName, 0).versionCode;
        }
        catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    private int getServerSoftVersionName() {
        InputStream inStream = null;
        try {
            inStream = new FileInputStream(new File(
                    Environment.getExternalStorageDirectory() + "/"
                            + "vnowmsg/version_config.xml"));
        }
        catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            mHashMapParseDetails = PullParseXmlUtil.getServerInfo(inStream);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (null == mHashMapParseDetails) {
            return 0;
        }
        else {
            int versioncode = 0;
            if (null != mHashMapParseDetails.get("version")) {
                versioncode = Integer.parseInt(mHashMapParseDetails.get("version"));
            }
            return versioncode;
        }
    }

    private void showNoticeDialog() {
        AlertDialog.Builder builder = new Builder(mContext);
        builder.setTitle(R.string.soft_update_title);
        builder.setMessage(mContext.getString(R.string.str_more_version) + mLocalVersionCode
                + "\n" + mContext.getString(R.string.str_new_version) + mHashMapParseDetails.get("info"));
        // LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
        // LayoutParams.WRAP_CONTENT);
        builder.setPositiveButton(R.string.soft_update_updatebtn,
                new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        showDownloadDialog();
                    }
                });
        builder.setNegativeButton(R.string.soft_update_later,
                new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        dialog = null;
                    }
                });
        mDialog = builder.create();
        mDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
                // TODO Auto-generated method stub
                mDialog = null;
            }
        });
        mDialog.show();
    }

    private void showDownloadDialog() {
        AlertDialog.Builder builder = new Builder(mContext, R.style.progress_dialog);
        builder.setTitle(R.string.soft_updating);
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.softupdate_progress, null);
        mProgressBarDownload = (ProgressBar) v
                .findViewById(R.id.update_progress);
        builder.setView(v);
        builder.setNegativeButton(R.string.str_cancel,
                new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mIsCancelUpadte = true;
                    }
                });
        mDownloadTipDialog = builder.create();
        mDownloadTipDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
                // TODO Auto-generated method stub
                mDownloadTipDialog = null;
            }
        });
        mDownloadTipDialog.setCanceledOnTouchOutside(false);
        mDownloadTipDialog.show();
        downloadApk();
    }

    public void downloadApk() {
        new downloadApkThread().start();
    }

    public void downloadVersionConfig() {
        downloadVersionConfigFileFromNet(mDownloadVersionConfigUrl,
                "version_config.xml");
    }

    private class downloadApkThread extends Thread {
        @Override
        public void run() {
            downloadFileFromNet(
                    mHashMapParseDetails.get("url"),
                    mHashMapParseDetails.get("url")
                            .substring(
                                    mHashMapParseDetails.get("url")
                                            .lastIndexOf("/") + 1,
                                    mHashMapParseDetails.get("url").length()));
        }
    };

    public void downloadFileFromNet(String downloadUrl, String dstFileName) {
        try {
            URL url = new URL(downloadUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            int length = conn.getContentLength();
            InputStream is = conn.getInputStream();
            File file = new File(mSaveFileDir);
            if (!file.exists()) {
                file.mkdir();
            }
            for (File cFile : file.listFiles()) {
                if (cFile.getName().equals(dstFileName)) {
                    if (cFile.length() == length) {
                        mDownloadTipDialog.dismiss();
                        mHandler.sendEmptyMessage(APK_EXIST);
                        return;
                    }
                    else {
                        cFile.delete();
                        break;
                    }
                }
            }
            File apkFile = new File(mSaveFileDir, dstFileName);
            FileOutputStream fos = new FileOutputStream(apkFile);
            int count = 0;
            byte buf[] = new byte[1024];
            do {
                int numread = is.read(buf);
                count += numread;
                mProgressDownload = (int) (((float) count / length) * 100);
                mHandler.sendEmptyMessage(DOWNLOADING);
                if (numread <= 0) {
                    mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                    break;
                }
                fos.write(buf, 0, numread);
            } while (!mIsCancelUpadte);
            fos.close();
            is.close();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        mDownloadTipDialog.dismiss();
    }

    public void downloadVersionConfigFileFromNet(String downloadUrl,
            String dstFileName) {
        try {
            URL url = new URL(downloadUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            File file = new File(mSaveFileDir);
            if (!file.exists()) {
                file.mkdir();
            }
            File apkFile = new File(mSaveFileDir, dstFileName);
            FileOutputStream fos = new FileOutputStream(apkFile);
            byte buf[] = new byte[1024];
            int num = 0;
            while ((num = is.read(buf)) > 0) {
                fos.write(buf, 0, num);
            }
            fos.close();
            is.close();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void installApk() {
        File apkfile = new File(mSaveFileDir, "/" + mHashMapParseDetails.get("url")
                .substring(mHashMapParseDetails.get("url").lastIndexOf("/") + 1,
                        mHashMapParseDetails.get("url").length()));
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");
        mContext.startActivity(i);
    }
}
