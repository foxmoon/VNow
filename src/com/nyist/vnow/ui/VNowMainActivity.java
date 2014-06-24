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
import com.nyist.vnow.utils.CommonUtil;
import com.nyist.vnow.utils.Constants;
import com.nyist.vnow.utils.LogTag;
import com.nyist.vnow.utils.NetUtil;
import com.nyist.vnow.utils.Session;
import com.nyist.vnow.utils.ThreadHelper;
import com.nyist.vnow.utils.ToastUtil;
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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

public class VNowMainActivity extends FragmentActivity implements CoreCallBack {
    private RadioButton mRBtnTab1, mRBtnTab2, mRBtnTab3, mRBtnTab4;
    private Fragment currentFragment;
    private VNowFragmentVNow mFragmentVNow;
    private VNowFragmentContacts mFragmentContacts;
    private VNowFragmentSecretary mFragmentSecretary;
    private VNowFragmentMore mFragmentMore;
    private long mExitTime;
    private VNowCore mCore;
    // private MyEventListener mCallBackListener;
    private IVNowAPI mVNowAPI;
    private String filePath = Environment.getExternalStorageDirectory() + "/tmp/12.jpg";
    private int mTabFlag = 0;
    private View mNoNetworkBar;
    private TextView mTopBarText;
    private CheckNetWorkReceiver checkNetWorkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.vnow_main);
        mCore = VNowApplication.newInstance().getCore();
        if (null != savedInstanceState) {
            CommonUtil.set_httpUrl(Session.newInstance(this).getServiceIp());
            mTabFlag = savedInstanceState.getInt("tabFlag");
            ArrayList<String> list = savedInstanceState
                    .getStringArrayList("user");
            mCore.getmUser().uuid = list.get(0);
            mCore.getmUser().type = list.get(1);
            mCore.getmUser().phone = list.get(2);
            mCore.getmUser().password = list.get(3);
            mCore.getmUser().name = list.get(4);
            mCore.getmUser().company_code = list.get(5);
            VNowApplication
                    .newInstance()
                    .getCore()
                    .doLogin(mCore.getmUser().phone,
                            mCore.getmUser().password, true);
        }
        initUI();
        mCore.setCoreListener(this);
        int soundValue = Session.newInstance(VNowMainActivity.this).getVideoSound();
        mCore.doechoDelaySet(soundValue);
        mCore.checkUpVersion(this, true);
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
        // TODO Auto-generated method stub
        if ((System.currentTimeMillis() - mExitTime) > 1500) {
            ToastUtil.getInstance(VNowMainActivity.this).showShort(getString(R.string.str_click_exit));
            mExitTime = System.currentTimeMillis();
        }
        else {
            VNowApplication.newInstance().getCore().doLogout();
            // VNowApplication.the().destroyCore();
            // System.exit(0);
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        // mCore.unSetCoreListener();
        super.onDestroy();
       
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tabFlag", mTabFlag);
        ArrayList<String> list = new ArrayList<String>();
        list.add(mCore.getmUser().uuid);
        list.add(mCore.getmUser().type);
        list.add(mCore.getmUser().phone);
        list.add(mCore.getmUser().password);
        list.add(mCore.getmUser().name);
        list.add(mCore.getmUser().company_code);
        outState.putStringArrayList("user", list);
    }

    private void initUI() {
        initTopBar();
        mRBtnTab1 = (RadioButton) findViewById(R.id.rbtn_main_tab1);
        mRBtnTab2 = (RadioButton) findViewById(R.id.rbtn_main_tab2);
        mRBtnTab3 = (RadioButton) findViewById(R.id.rbtn_main_tab3);
        mRBtnTab4 = (RadioButton) findViewById(R.id.rbtn_main_tab4);
        mRBtnTab1.setOnClickListener(mBtnChangeViewClickListener);
        mRBtnTab2.setOnClickListener(mBtnChangeViewClickListener);
        mRBtnTab3.setOnClickListener(mBtnChangeViewClickListener);
        mRBtnTab4.setOnClickListener(mBtnChangeViewClickListener);
        if (null == mFragmentVNow) {
            mFragmentVNow = new VNowFragmentVNow();
        }
        if (null == mFragmentContacts) {
            mFragmentContacts = new VNowFragmentContacts();
        }
        if (null == mFragmentSecretary) {
            mFragmentSecretary = new VNowFragmentSecretary();
        }
        if (null == mFragmentMore) {
            mFragmentMore = new VNowFragmentMore();
        }
        if (mTabFlag == 0) {
            currentFragment = mFragmentVNow;
        }
        else if (mTabFlag == 1) {
            currentFragment = mFragmentContacts;
        }
        else if (mTabFlag == 2) {
            currentFragment = mFragmentSecretary;
        }
        else if (mTabFlag == 3) {
            currentFragment = mFragmentMore;
        }
        addFragmentToMain(currentFragment);
        // mCore.doQueryColleagueList(0);
        // mCore.doQueryGroupList(0);
        // mCore.doQueryFriendList(0);
        // mCore.doCreateGroup("huaping");
        // mCore.doCreateGroup("huaping1");
        // mCore.doCreateGroup("huaping2");
        // mCore.doCreateGroup("huaping3");
        // mCore.doCreateGroup("huaping4");
        // mCore.doAddFriend("15036154376","jason");
        // mCore.doModifyFrient("13000000000", "myFriend",
        // "896BE589FFA82078003E129C07C0D7D0");
        // mCore.doDelFriend("13526833092", "897B1D0DFFA8207801D603A1A9ACEAEA");
        // mCore.doDelFriend("15036154376","8984AF7DFFA8207800FC5F7343BF53E7");
        // mCore.doModifyGroup("tongxue","89862BB4FFA820780052BF8E5EA2103D");
        // mCore.doDelGroup("899EB83EFFA8207800C3EB2550868CD7");
        // mCore.doAddGroupUser("89498221FFA8207801E8F7D917A4A86C", "lisi",
        // "15036154376");
        // mCore.doDelGroupUser("15036154376",
        // "89498221FFA8207801E8F7D917A4A86C");
        // Bitmap bm = BitmapFactory.decodeFile(filePath,opts);
        // Bitmap bm = BitmapFactory.decodeStream(finstrm);
        // FileInputStream istrm = new FileInputStream(mfile);
        // FileOutputStream ostrm = new FileOutputStream(mfile);
        // istrm.read(buffer);
        // mfile.get
        // mCore.doUpLoadPhoto(photodata);
        // mCore.dotestUpload(filePath);
        // System.out.println("new String(baos.toByteArray())--->"+new
        // String(baos.toByteArray()));
        // File mfile=new File(filePath);
        //
        // ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // File file = new File(filePath);
        // System.out.println(file.length());
        // Bitmap bm = BitmapFactory.decodeFile(filePath);
        // bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        // String photodata = new
        // String(Base64.encode(baos.toByteArray(),Base64.DEFAULT));
        // mCore.doUploadFileHttp(photodata,file.getName(),"1","1","jpg");
        // mCore.doGetMyselfInfo();
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
                    mTabFlag = 0;
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
                    mTabFlag = 1;
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
                    mTabFlag = 2;
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
                    mTabFlag = 3;
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
}
