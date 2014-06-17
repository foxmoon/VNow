package com.nyist.vnow.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.nyist.vnow.R;
import com.nyist.vnow.core.VNowApplication;
import com.nyist.vnow.core.VNowCore;
import com.nyist.vnow.ui.SystemSetActivity;
import com.nyist.vnow.ui.VNMoreActivity;
import com.nyist.vnow.ui.VNowHostActivity;
import com.nyist.vnow.utils.ToastUtil;
import com.vnow.sdk.openapi.EventListener;
import com.vnow.sdk.openapi.IVNowAPI;

public class VNowFragmentMore extends Fragment implements OnClickListener {
    private RelativeLayout mLayoutStore;
    private RelativeLayout mLayoutSet;
    private RelativeLayout mLayoutIntrduce;
    private RelativeLayout mLayoutAbout;
    private VNowCore mCore;
    private MyEventListener mCallBackListener;
    private IVNowAPI mVNowAPI;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mCore = VNowApplication.getInstance().getCore();
        mCallBackListener = new MyEventListener();
        mVNowAPI = IVNowAPI.newInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.vnow_fragment_more, container,
                false);
        initUI(view);
        return view;
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mVNowAPI.setEventListener(mCallBackListener);
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mVNowAPI.removeEventListener(mCallBackListener);
    }

    private void initUI(View view) {
        mLayoutStore = (RelativeLayout) view
                .findViewById(R.id.rlayout_my_store);
        mLayoutSet = (RelativeLayout) view
                .findViewById(R.id.rlayout_app_setting);
        mLayoutIntrduce = (RelativeLayout) view
                .findViewById(R.id.rlayout_recommend_friend);
        mLayoutAbout = (RelativeLayout) view
                .findViewById(R.id.rlayout_more_about);
        mLayoutStore.setOnClickListener(this);
        mLayoutSet.setOnClickListener(this);
        mLayoutIntrduce.setOnClickListener(this);
        mLayoutAbout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int id = v.getId();
        switch (id) {
            case R.id.rlayout_my_store:
                ToastUtil.getInstance(getActivity()).showShort(
                        getString(R.string.str_more_my_store));
                break;
            case R.id.rlayout_app_setting:
                Intent intent = new Intent(getActivity(), SystemSetActivity.class);
                startActivity(intent);
                break;
            case R.id.rlayout_recommend_friend:
                ToastUtil.getInstance(getActivity()).showShort(
                        getString(R.string.str_more_recommend_friend));
                break;
            case R.id.rlayout_more_about:
                Intent moreIntent = new Intent(getActivity(), VNMoreActivity.class);
                startActivity(moreIntent);
                break;
        }
    }

    private class MyEventListener extends EventListener {
        @Override
        public void onResponseLogout(boolean bSuccess) {
            // TODO Auto-generated method stub
            super.onResponseLogout(bSuccess);
            if (bSuccess) {
                ToastUtil.getInstance(getActivity()).showShort("已注销！");
                Intent intent = new Intent(getActivity(),
                        VNowHostActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
            else {
                ToastUtil.getInstance(getActivity()).showShort("注销失败！");
            }
        }
    }
}
