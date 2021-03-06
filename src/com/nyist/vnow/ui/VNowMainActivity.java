package com.nyist.vnow.ui;

import java.util.ArrayList;
import com.nyist.vnow.R;
import com.nyist.vnow.core.VNowApplication;
import com.nyist.vnow.core.VNowCore;
import com.nyist.vnow.event.CoreEvent;
import com.nyist.vnow.fragment.VNowFragmentContacts;
import com.nyist.vnow.fragment.VNowFragmentMore;
import com.nyist.vnow.fragment.VNowFragmentSecretary;
import com.nyist.vnow.fragment.VNowFragmentVNow;
import com.nyist.vnow.listener.CoreCallBack;
import com.nyist.vnow.receiver.CheckNetWorkReceiver;
import com.nyist.vnow.struct.LoginResult;
import com.nyist.vnow.struct.VNowRctContact;
import com.nyist.vnow.utils.CommonUtil;
import com.nyist.vnow.utils.Constants;
import com.nyist.vnow.utils.LogTag;
import com.nyist.vnow.utils.NetUtil;
import com.nyist.vnow.utils.Session;
import com.nyist.vnow.utils.ThreadHelper;
import com.nyist.vnow.utils.ToastUtil;
import com.stay.AppException;
import com.stay.net.Request;
import com.stay.net.Request.RequestMethod;
import com.stay.net.callback.JsonCallback;
import com.vnow.sdk.openapi.EventListener;
import com.vnow.sdk.openapi.IVNowAPI;

import de.greenrobot.event.EventBus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

public class VNowMainActivity extends BaseFragmentActivity implements CoreCallBack {
    private RadioButton mRBtnTab1, mRBtnTab2, mRBtnTab3, mRBtnTab4;
    private Fragment currentFragment;
    private VNowFragmentVNow mFragmentVNow;
    private VNowFragmentContacts mFragmentContacts;
    private VNowFragmentSecretary mFragmentSecretary;
    private VNowFragmentMore mFragmentMore;
    private long mExitTime;
    private VNowCore mCore;
    // private MyEventListener mCallBackListener;
    private int mTabFlag = 0;
    private View mNoNetworkBar;
    private TextView mTopBarText;
    private CheckNetWorkReceiver checkNetWorkReceiver;
    private MyEventListener mCallBackListener;

    @Override
    protected void setContentView() {
        setContentView(R.layout.vnow_main);
    }

    @Override
    protected void initializeData() {
        mCore = VNowApplication.newInstance().getCore();
        mCallBackListener = new MyEventListener();
        mCore.doSetEventListener(mCallBackListener);
        mCore.setCoreListener(this);
        int soundValue = Session.newInstance(VNowMainActivity.this).getVideoSound();
        mCore.doechoDelaySet(soundValue);
    }

    @Override
    protected void initializeViews() {
        initTopBar();
        mRBtnTab1 = (RadioButton) findViewById(R.id.rbtn_main_tab1);
        mRBtnTab2 = (RadioButton) findViewById(R.id.rbtn_main_tab2);
        mRBtnTab3 = (RadioButton) findViewById(R.id.rbtn_main_tab3);
        mRBtnTab4 = (RadioButton) findViewById(R.id.rbtn_main_tab4);
        mRBtnTab1.setOnClickListener(mBtnChangeViewClickListener);
        mRBtnTab2.setOnClickListener(mBtnChangeViewClickListener);
        mRBtnTab3.setOnClickListener(mBtnChangeViewClickListener);
        mRBtnTab4.setOnClickListener(mBtnChangeViewClickListener);
        mFragmentVNow = new VNowFragmentVNow();
        currentFragment = mFragmentVNow;
        // if (null == mFragmentVNow) {
        // mFragmentVNow = new VNowFragmentVNow();
        // }
        // if (null == mFragmentContacts) {
        // mFragmentContacts = new VNowFragmentContacts();
        // }
        // if (null == mFragmentSecretary) {
        // mFragmentSecretary = new VNowFragmentSecretary();
        // }
        // if (null == mFragmentMore) {
        // mFragmentMore = new VNowFragmentMore();
        // }
        // if (mTabFlag == 0) {
        // currentFragment = mFragmentVNow;
        // }
        // else if (mTabFlag == 1) {
        // currentFragment = mFragmentContacts;
        // }
        // else if (mTabFlag == 2) {
        // currentFragment = mFragmentSecretary;
        // }
        // else if (mTabFlag == 3) {
        // currentFragment = mFragmentMore;
        // }
        addFragmentToMain(currentFragment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerCheckNetWorkReceiver();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(checkNetWorkReceiver);
    }

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - mExitTime) > 1500) {
            ToastUtil.getInstance(VNowMainActivity.this).showShort(getString(R.string.str_click_exit));
            mExitTime = System.currentTimeMillis();
        }
        else {
//            VNowApplication.newInstance().getCore().doLogout();
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        // mCore.unSetCoreListener();
        super.onDestroy();
    }

    /**
     * 初始化topbar
     */
    private void initTopBar() {
        mNoNetworkBar = (Button) findViewById(R.id.mNoNetworkBar);
        mTopBarText = (TextView) findViewById(R.id.mTopBarText);
        mTopBarText.setText(getText(R.string.str_tab_vnow));
        mNoNetworkBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                NetUtil.openNetSetting(VNowMainActivity.this);
            }
        });
    }

    // will be called on the main thread
    public void onEventMainThread(CheckNetWorkReceiver.NetworkConnectionChanged event) {
        LogTag.d("in onEventMainThread! ", ThreadHelper.getThreadInfo());
        if (NetUtil.checkNet(VNowMainActivity.this)) {
            mNoNetworkBar.setVisibility(View.GONE);
        }
        else {
            mNoNetworkBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 监测网络状态
     */
    private void registerCheckNetWorkReceiver() {
        checkNetWorkReceiver = new CheckNetWorkReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.NET_CONNECTIVITY_CHANGE);
        intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        registerReceiver(checkNetWorkReceiver, intentFilter);
    }

    private void addFragmentToMain(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flayout_main_child_fragment_container, fragment);
        ft.commitAllowingStateLoss();
    }

    /**
     * the listener of the bottom buttons in the main fragment is click or not
     */
    private View.OnClickListener mBtnChangeViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.rbtn_main_tab1: {
                    // mTabFlag = 0;
                    mTopBarText.setText(getText(R.string.str_tab_vnow));
                    if (currentFragment instanceof VNowFragmentVNow) {
                        return;
                    }
                    if (null == mFragmentVNow) {
                        mFragmentVNow = new VNowFragmentVNow();
                    }
                    currentFragment = mFragmentVNow;
                    addFragmentToMain(currentFragment);
                }
                    break;
                case R.id.rbtn_main_tab2: {
                    // mTabFlag = 1;
                    mTopBarText.setText(getText(R.string.str_tab_contacts));
                    if (currentFragment instanceof VNowFragmentContacts) {
                        return;
                    }
                    if (null == mFragmentContacts) {
                        mFragmentContacts = new VNowFragmentContacts();
                    }
                    currentFragment = mFragmentContacts;
                    addFragmentToMain(currentFragment);
                }
                    break;
                case R.id.rbtn_main_tab3: {
                    // mTabFlag = 2;
                    mTopBarText.setText(getText(R.string.str_tab_apps));
                    if (currentFragment instanceof VNowFragmentSecretary) {
                        return;
                    }
                    if (null == mFragmentSecretary) {
                        mFragmentSecretary = new VNowFragmentSecretary();
                    }
                    currentFragment = mFragmentSecretary;
                    addFragmentToMain(currentFragment);
                }
                    break;
                case R.id.rbtn_main_tab4: {
                    // mTabFlag = 3;
                    mTopBarText.setText(getText(R.string.str_tab_more));
                    if (currentFragment instanceof VNowFragmentMore) {
                        return;
                    }
                    if (null == mFragmentMore) {
                        mFragmentMore = new VNowFragmentMore();
                    }
                    currentFragment = mFragmentMore;
                    addFragmentToMain(currentFragment);
                }
                    break;
            }
        }
    };

    @Override
    public void onCoreCallBack(CoreEvent event) {
        switch (event) {
            case EVENT_LOGOUT_TASK: {
                VNowMainActivity.this.finish();
            }
                break;
        }
    }

    // web 接口回调
    private class MyEventListener extends EventListener {
        public void onResponseApiStatus(String status) {
            if (status.equals("0")) {
                String mediaServerAddress = Session.newInstance(VNowMainActivity.this).getMediaServerAddress();
                if (!TextUtils.isEmpty(mediaServerAddress)) {
                    String phone = Session.newInstance(VNowMainActivity.this).getUserPhone();
                    String password = Session.newInstance(VNowMainActivity.this).getPassWord();
                    if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(password)) {
                        mCore.dispatchMediaServer(phone, password);
                    }
                }
            }
            else if (status.equals("1")) {
                    Log.e("status", status);
            }
            else if (status.equals("2")) {
                ToastUtil.getInstance(VNowMainActivity.this).showShort(getString(R.string.media_login_success));
            }
            else if (status.equals("5") || status.equals("7")) {
                Intent intent = new Intent(VNowMainActivity.this,
                        ConfActivity.class);
                if (mCore.getRctContact().size() > 0) {
                    VNowRctContact rctItem = mCore.getRctContact().get(0);
                    intent.putExtra("callNumber", rctItem.getmStrConPhone());
                    intent.putExtra("callName", rctItem.getmStrContactName());
                    intent.putExtra("isCallin", false);
                    startActivity(intent);
                }
            }
        }
    }
}
