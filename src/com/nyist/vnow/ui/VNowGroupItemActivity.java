package com.nyist.vnow.ui;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.nyist.vnow.R;
import com.nyist.vnow.adapter.VNowGroupUserAdapter;
import com.nyist.vnow.adapter.VNowGroupUserAdapter.DelGroupUserListener;
import com.nyist.vnow.core.VNowApplication;
import com.nyist.vnow.core.VNowCore;
import com.nyist.vnow.dialog.AvcProgress;
import com.nyist.vnow.dialog.VNowAlertDlg;
import com.nyist.vnow.dialog.VNowEditFriendDlg;
import com.nyist.vnow.struct.Colleage;
import com.nyist.vnow.struct.Group;
import com.nyist.vnow.struct.VNowRctContact;
import com.nyist.vnow.utils.CharacterParser;
import com.nyist.vnow.utils.PinyinComparator;
import com.nyist.vnow.utils.ToastUtil;
import com.nyist.vnow.view.ViEPullToRefreshListView;
import com.vnow.sdk.openapi.EventListener;
import com.vnow.sdk.openapi.IVNowAPI;

public class VNowGroupItemActivity extends Activity implements DelGroupUserListener {
    private final int LOAD_GROUP_USER_FINISH = 0x301;
    private final int ADD_GROUP_USER_SUCCESS = 0x302;
    private final int ADD_GROUP_USER_FAILED = 0x303;
    private final int DEL_GROUP_USER_SUCCESS = 0x304;
    private final int DEL_GROUP_USER_FAILED = 0x305;
    private VNowCore mCore;
    private MyEventListener mCallBackListener;
    private IVNowAPI mVNowAPI;
    private ViEPullToRefreshListView mListviewGroupUser;
    private CheckBox mCheckDelMode;
    private AvcProgress mProgressLoading;
    private ImageView mBtnAddGrpUser;
    private ImageView mBtnBack;
    private TextView mTxtGrpName;
    private VNowAlertDlg mDialog;
    private List<Group> mListGrpUser = null;
    private String _delPhone;
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;
    private VNowGroupUserAdapter mAdapterGrpUser;
    private String mGrpUuid;
    private String mGrpName;
    private Handler mMainHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case LOAD_GROUP_USER_FINISH: {
                    mListviewGroupUser.setVisibility(View.VISIBLE);
                    mListGrpUser.clear();
                    if (mCore.getGroupList(false, mGrpUuid).size() > 0) {
                        mListGrpUser.addAll(filledData(mCore.getGroupList(false, mGrpUuid)));
                        Collections.sort(mListGrpUser, pinyinComparator);
                        if (null != mAdapterGrpUser) {
                            mAdapterGrpUser.notifyDataSetChanged();
                        }
                    }
                    else {
                        if (null != mAdapterGrpUser) {
                            mAdapterGrpUser.updateListView(mListGrpUser);
                            mAdapterGrpUser.notifyDataSetChanged();
                            mCheckDelMode.setChecked(false);
                        }
                    }
                    mListviewGroupUser.onRefreshComplete();
                }
                    break;
                case ADD_GROUP_USER_SUCCESS: {
                    mCore.doQueryGroupList();
                    ToastUtil.showShort(VNowGroupItemActivity.this, R.string.str_add_grp_user_success);
                }
                    break;
                case ADD_GROUP_USER_FAILED: {
                    ToastUtil.showShort(VNowGroupItemActivity.this,
                            R.string.str_add_grp_user_failed);
                }
                    break;
                case DEL_GROUP_USER_SUCCESS: {
                    mCore.doQueryGroupList();
                    ToastUtil.showShort(VNowGroupItemActivity.this,
                            R.string.str_del_group_success);
                }
                    break;
                case DEL_GROUP_USER_FAILED: {
                    ToastUtil.showShort(VNowGroupItemActivity.this, R.string.str_del_group_field);
                }
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vnow_activity_group_user);
        mCore = VNowApplication.getInstance().getCore();
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        mCallBackListener = new MyEventListener();
        mListGrpUser = new ArrayList<Group>();
        mVNowAPI = IVNowAPI.createIVNowAPI();
        initGroupUserUI();
        initGrpUser();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mVNowAPI.setEventListener(mCallBackListener);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mVNowAPI.removeEventListener(mCallBackListener);
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        if (null != mDialog)
            mDialog.dismiss();
        super.onStop();
    }

    /**
     * the method to init add friends show view
     */
    private void initGroupUserUI() {
        mListviewGroupUser = (ViEPullToRefreshListView) findViewById(R.id.listview_add_friends);
        mBtnAddGrpUser = (ImageView) findViewById(R.id.btn_add_group_user);
        mBtnBack = (ImageView) findViewById(R.id.btn_back);
        mTxtGrpName = (TextView) findViewById(R.id.txt_group_name);
        mCheckDelMode = (CheckBox) findViewById(R.id.check_del_group_user);
        mCheckDelMode.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    mCheckDelMode.setText(R.string.str_cancel_delete_contact);
                }
                else {
                    mCheckDelMode.setText(R.string.str_delete_contact);
                }
                if (null != mAdapterGrpUser) {
                    mAdapterGrpUser.setDelable(isChecked);
                    mAdapterGrpUser.notifyDataSetChanged();
                }
            }
        });
        mAdapterGrpUser = new VNowGroupUserAdapter(VNowGroupItemActivity.this,
                mListGrpUser, VNowGroupItemActivity.this);
        mListviewGroupUser.setAdapter(mAdapterGrpUser);
        mListviewGroupUser.setOnItemClickListener(mListItemClickListener);
        mBtnAddGrpUser.setOnClickListener(mBtnOnClickListener);
        mBtnBack.setOnClickListener(mBtnOnClickListener);
        mGrpUuid = getIntent().getExtras().getString("g_uuid");
        mGrpName = getIntent().getExtras().getString("g_name");
        mTxtGrpName.setText(mGrpName);
    }

    /**
     * the method to init the contacts from phone
     */
    private void initGrpUser() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                mListGrpUser.clear();
                for (Group grp : mCore.getGroupList()) {
                    if (grp.getParentId().equals(mGrpUuid)
                            && grp.getType().equals("2")) {
                        mListGrpUser.add(grp);
                    }
                }
                mMainHandler.sendEmptyMessageDelayed(LOAD_GROUP_USER_FINISH, 200);
            }
        }).start();
    }

    /**
     * 为ListView填充数据
     * 
     * @param objects
     * @return
     */
    private List<Group> filledData(List<Group> list) {
        List<Group> mSortList = new ArrayList<Group>();
        for (int i = 0; i < list.size(); i++) {
            Group friend = new Group();
            friend.setName(list.get(i).getName());
            friend.setA_uuid(list.get(i).getA_uuid());
            friend.setParentId(list.get(i).getParentId());
            friend.setPhone(list.get(i).getPhone());
            // 汉字转换成拼音
            String pinyin = characterParser.getSelling(list.get(i).getName());
            String sortString = pinyin.substring(0, 1).toUpperCase();
            friend.setmPyName(pinyin);
            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                friend.setmSortLetters(sortString.toUpperCase());
            }
            else {
                friend.setmSortLetters("#");
            }
            mSortList.add(friend);
        }
        return mSortList;
    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     * 
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<Group> filterDateList = new ArrayList<Group>();
        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = mListGrpUser;
        }
        else {
            filterDateList.clear();
            for (Group friend : mListGrpUser) {
                String name = friend.getName();
                if (name.indexOf(filterStr.toString()) != -1
                        || characterParser.getSelling(name).startsWith(
                                filterStr.toString())) {
                    filterDateList.add(friend);
                }
            }
        }
        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        mAdapterGrpUser.updateListView(filterDateList);
    }

    private void showAddGrpUserDlg() {
        final VNowEditFriendDlg dlg = new VNowEditFriendDlg(this,
                R.style.navComSettingDialogTheme);
        dlg.show();
        dlg.setTitle(getString(R.string.str_add_grp_user));
        dlg.setOKButton(getString(R.string.str_add),
                new VNowEditFriendDlg.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        // TODO Auto-generated method stub
                        if (dlg.getFriendName().equals("")
                                || dlg.getFriendPhone().equals("")) {
                            ToastUtil.showShort(VNowGroupItemActivity.this, R.string.str_modify_not_null);
                            return;
                        }
                        else {
                            mCore.doAddGroupUser(mGrpUuid,
                                    URLEncoder.encode(dlg.getFriendName()),
                                    dlg.getFriendPhone());
                        }
                    }
                });
        dlg.setCancelButton(getString(R.string.str_dlg_cancel),
                new VNowEditFriendDlg.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        // TODO Auto-generated method stub
                    }
                });
    }

    private void showCallDlg(final Group group) {
        if (null != mDialog) {
            mDialog.dismiss();
            mDialog = null;
        }
        mDialog = new VNowAlertDlg(this, R.style.navComSettingDialogTheme);
        mDialog.show();
        mDialog.setTitle(R.string.str_alert_tip);
        mDialog.setContent(getString(R.string.str_call_title).replace("${CALL_NUM}", group.getName()));
        mDialog.setOKButton(getString(R.string.str_call),
                new VNowAlertDlg.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent(VNowGroupItemActivity.this, ConfActivity.class);
                        intent.putExtra("callNumber", group.getPhone());
                        intent.putExtra("callName", group.getName());
                        intent.putExtra("isCallin", false);
                        VNowRctContact rctItem = new VNowRctContact();
                        rctItem.setmStrUserId(mCore.getMySelf().uuid);
                        rctItem.setmStrUserName(mCore.getMySelf().name);
                        rctItem.setmStrConPhone(group.getPhone());
                        rctItem.setmStrContactName(group.getName());
                        rctItem.setmCallTime(System.currentTimeMillis());
                        rctItem.setmIsCallIn(false);
                        mCore.insertCallHistory(rctItem);
                        startActivity(intent);
                    }
                });
        mDialog.setCancelButton(getString(R.string.str_cancel),
                new VNowAlertDlg.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        // TODO Auto-generated method stub
                    }
                });
    }

    private AdapterView.OnItemClickListener mListItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapter, View view,
                int position, long id) {
            showCallDlg(mListGrpUser.get(position - 1));
        }
    };
    private View.OnClickListener mBtnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int id = v.getId();
            if (id == R.id.btn_add_group_user) {
                showAddGrpUserDlg();
            }
            else if (id == R.id.btn_back) {
                finish();
            }
        }
    };

    // web 接口回调
    private class MyEventListener extends EventListener {
        public void onResponseQueryGroupList(boolean bSuccess, String jsonResult) {
            // TODO Auto-generated method stub
            super.onResponseQueryGroupList(bSuccess, jsonResult);
            initGrpUser();
        }

        @Override
        public void onResponseAddGroupUser(boolean bSuccess, int reason) {
            // TODO Auto-generated method stub
            super.onResponseAddGroupUser(bSuccess, reason);
            if (bSuccess) {
                mMainHandler.sendEmptyMessage(ADD_GROUP_USER_SUCCESS);
            }
            else if (reason == -1) {
                ToastUtil.showShort(VNowGroupItemActivity.this, R.string.str_add_grp_user_error_nobody);
            }
            else
                mMainHandler.sendEmptyMessage(ADD_GROUP_USER_FAILED);
        }

        @Override
        public void onResponseDelGroupUser(boolean bSuccess) {
            // TODO Auto-generated method stub
            super.onResponseDelGroupUser(bSuccess);
            if (bSuccess) {
                mCore.deleteDBGroupItem(_delPhone, 2);
                mMainHandler.sendEmptyMessage(DEL_GROUP_USER_SUCCESS);
            }
            else {
                mMainHandler.sendEmptyMessage(DEL_GROUP_USER_FAILED);
            }
            _delPhone = null;
        }
    }

    @Override
    public void onDelGroup(String phone, String g_uuid) {
        // TODO Auto-generated method stub
        _delPhone = phone;
        mCore.doDelGroupUser(phone, g_uuid);
    }
}
