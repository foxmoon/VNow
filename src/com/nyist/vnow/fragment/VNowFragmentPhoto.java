package com.nyist.vnow.fragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.nyist.vnow.R;
import com.nyist.vnow.adapter.ImageAdapter;
import com.nyist.vnow.core.VNowApplication;
import com.nyist.vnow.core.VNowCore;
import com.nyist.vnow.struct.UploadCaptrue;

public class VNowFragmentPhoto extends Fragment {
    private ListView mListPhoto;
    private VNowCore mCore;
    private List<UploadCaptrue> photos;
    private ImageAdapter mAdapter;
    // private String filePath=Environment.getExternalStorageDirectory()+"/tmp";
    private View mMyView;

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        mMyView = inflater.inflate(R.layout.vnow_list_photo, container, false);
        mCore = VNowApplication.the().getCore();
        initUI();
        return mMyView;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        mCore.getmListPhotoUpdate().clear();
        photos.clear();
        photos = null;
        super.onDestroy();
    }

    private void initUI() {
        mListPhoto = (ListView) mMyView.findViewById(R.id.photo_upload_list);
        photos = new ArrayList<UploadCaptrue>();
        initImg();
        mAdapter = new ImageAdapter(getActivity(), photos);
        mListPhoto.setAdapter(mAdapter);
    }

    public void setUIVisibility(int visiable) {
        mMyView.setVisibility(visiable);
    }

    public void setUIVisibility() {
        if (mMyView.getVisibility() == View.GONE) {
            mMyView.setVisibility(View.VISIBLE);
        }
        else {
            mMyView.setVisibility(View.GONE);
        }
    }

    public boolean isUIVisibility() {
        if (mMyView.getVisibility() == View.GONE) {
            return false;
        }
        else {
            return true;
        }
    }

    public void flashAdapter() {
        photos = mCore.getmListPhotoUpdate();
        if (null != mAdapter) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public void updateImgList() {
        if (mCore.getmListPhotoUpdate().size() > 0) {
            UploadCaptrue captrue = mCore.getmListPhotoUpdate().get(0);
            String name = captrue.getPhotoName();
            captrue.remove();
            mCore.getmListPhotoUpdate().remove(0);
        }
        if (null != mAdapter) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void initImg() {
        // TODO Auto-generated method stub
        photos = mCore.getmListPhotoUpdate();
    }
}
