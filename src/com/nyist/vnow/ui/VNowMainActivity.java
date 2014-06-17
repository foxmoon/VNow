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
import com.nyist.vnow.utils.CommonUtil;
import com.vnow.sdk.openapi.IVNowAPI;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.vnow_main);
        mCore = VNowApplication.the().getCore();
        if (null != savedInstanceState) {
            CommonUtil.set_httpUrl(VNowApplication.the().getSetting(
                    getString(R.string.setting_media_server_ip),
                    getString(R.string.setting_defult_server_ip)));
            mTabFlag = savedInstanceState.getInt("tabFlag");
            ArrayList<String> list = savedInstanceState
                    .getStringArrayList("user");
            mCore.getMySelf().uuid = list.get(0);
            mCore.getMySelf().type = list.get(1);
            mCore.getMySelf().phone = list.get(2);
            mCore.getMySelf().password = list.get(3);
            mCore.getMySelf().name = list.get(4);
            mCore.getMySelf().company_code = list.get(5);
            VNowApplication
                    .the()
                    .getCore()
                    .doLogin(mCore.getMySelf().phone,
                            mCore.getMySelf().password, true);
        }
        initUI();
        mCore.setCoreListener(this);
        int soundValue = VNowApplication.the().getSetting(getString(R.string.setting_video_sound_red), 240);
        mCore.doechoDelaySet(soundValue);
        mCore.checkUpVersion(this, true);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if ((System.currentTimeMillis() - mExitTime) > 1500) {
            VNowApplication.the().showToast(getString(R.string.str_click_exit));
            mExitTime = System.currentTimeMillis();
        }
        else {
            VNowApplication.the().getCore().doLogout();
            // VNowApplication.the().destroyCore();
            // System.exit(0);
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        // mCore.unSetCoreListener();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        outState.putInt("tabFlag", mTabFlag);
        ArrayList<String> list = new ArrayList<String>();
        list.add(mCore.getMySelf().uuid);
        list.add(mCore.getMySelf().type);
        list.add(mCore.getMySelf().phone);
        list.add(mCore.getMySelf().password);
        list.add(mCore.getMySelf().name);
        list.add(mCore.getMySelf().company_code);
        outState.putStringArrayList("user", list);
    }

    private void initUI() {
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
            // TODO Auto-generated method stub
            int id = v.getId();
            switch (id) {
                case R.id.rbtn_main_tab1: {
                    mTabFlag = 0;
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
        // TODO Auto-generated method stub
        switch (event) {
            case EVENT_LOGOUT_TASK: {
                VNowMainActivity.this.finish();
            }
                break;
        }
    }
}
