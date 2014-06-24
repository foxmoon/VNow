package com.nyist.vnow.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.nyist.vnow.R;
import com.nyist.vnow.adapter.VNowPhoneContactAdapter;
import com.nyist.vnow.core.VNowApplication;
import com.nyist.vnow.core.VNowCore;
import com.nyist.vnow.dialog.AvcProgress;
import com.nyist.vnow.dialog.VNowAlertDlg;
import com.nyist.vnow.struct.Colleage;
import com.nyist.vnow.struct.VNowFriend;
import com.nyist.vnow.struct.VNowRctContact;
import com.nyist.vnow.ui.ConfActivity;
import com.nyist.vnow.utils.CharacterParser;
import com.nyist.vnow.utils.DES;
import com.nyist.vnow.utils.PinyinComparator;
import com.nyist.vnow.view.SideBar;
import com.nyist.vnow.view.SideBar.OnTouchingLetterChangedListener;

public class VNowFragmentPhoneContacts extends BaseFragment {
    private final int LOAD_FRIENDS_FINISH = 0x301;
    private ListView mListviewContact;
    private SideBar mIndexBar;
    private TextView mTxtContactIndex;
    private AvcProgress mProgressLoading;
    private EditText mEditSearch;
    private List<VNowFriend> mListContacts = null;
    private List<VNowFriend> mFilterDateList = null;
    private VNowPhoneContactAdapter mAdapterContacts;
    private VNowAlertDlg mDialog;
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;
    private VNowCore mCore;
    private Handler mMainHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == LOAD_FRIENDS_FINISH) {
                stopProgress();
                mListviewContact.setVisibility(View.VISIBLE);
                mIndexBar.setVisibility(View.VISIBLE);
                mListContacts.clear();
                if (mCore.getmListPhoneContacts().size() > 0) {
                    mListContacts.addAll(filledData(mCore.getmListPhoneContacts()));
                    // 根据a-z进行排序源数据
                    Collections.sort(mListContacts, pinyinComparator);
                    mAdapterContacts = new VNowPhoneContactAdapter(
                            getActivity(), mListContacts);
                    mListviewContact.setAdapter(mAdapterContacts);
                    mListviewContact
                            .setOnItemClickListener(mListItemClickListener);
                    mListviewContact
                            .setOnItemLongClickListener(mItemLongClickListener);
                }
            }
        };
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vnow_fragment_phone_contacts,
                container, false);
        mListContacts = new ArrayList<VNowFriend>();
        mAdapterContacts = new VNowPhoneContactAdapter(
                getActivity(), mListContacts);
        initPhoneContactsShowUI(view);
        return view;
    };

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mCore = VNowApplication.newInstance().getCore();
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        mFilterDateList = new ArrayList<VNowFriend>();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        initPhoneData();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        stopProgress();
    }

    /**
     * the method to init add friends show view
     */
    private void initPhoneContactsShowUI(View view) {
        mListviewContact = (ListView) view
                .findViewById(R.id.listview_add_friends);
        mIndexBar = (SideBar) view.findViewById(R.id.sidebar_add_friends);
        mEditSearch = (EditText) view.findViewById(R.id.edit_text_contacts_search);
        mTxtContactIndex = (TextView) view.findViewById(R.id.dialog);
        mTxtContactIndex.setVisibility(View.GONE);
        mIndexBar.setTextView(mTxtContactIndex);
        mListviewContact.setVisibility(View.GONE);
        mIndexBar.setVisibility(View.GONE);
        // 设置右侧触摸监听
        mIndexBar
                .setOnTouchingLetterChangedListener(mTouchingLetterChangedListener);
        // 根据输入框输入值的改变来过滤搜索
        mEditSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    /**
     * the method to init the contacts from phone
     */
    private void initPhoneData() {
        if (mCore.getmListPhoneContacts().size() <= 0) {
            startProgress();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    mCore.getPhoneContacts();
                    if (mCore.getmListPhoneContacts().size() == 0) {
                        mCore.getSIMContacts();
                    }
                    mMainHandler.sendEmptyMessage(LOAD_FRIENDS_FINISH);
                }
            }).start();
        }
        else {
            mMainHandler.sendEmptyMessage(LOAD_FRIENDS_FINISH);
        }
    }

    /**
     * the method to start the progressbar when load data
     */
    private void startProgress() {
        if (null == mProgressLoading) {
            mProgressLoading = new AvcProgress(getActivity(),
                    R.style.navSettingDialogTheme);
            mProgressLoading.show();
            mProgressLoading.setCancelable(true);
            mProgressLoading.startProgress(getString(R.string.str_contact_loading));
        }
        else {
            mProgressLoading.stopProgress();
            mProgressLoading = null;
        }
    }

    /**
     * the method to stop the progressbar when loaded data
     */
    private void stopProgress() {
        if (null != mProgressLoading) {
            mProgressLoading.stopProgress();
            mProgressLoading = null;
        }
    }

    /**
     * the method to show recent chat message dialog when you long clik recent
     * chat listview item
     * 
     * @param longClickPos
     */
    private void showItemLongClickDialog(final int longClickPos) {
        final VNowFriend item = mListContacts.get(longClickPos);
        String[] choices = new String[1];
        choices[0] = getString(R.string.str_delete_contact);
        new AlertDialog.Builder(getActivity()).setTitle(item.getmName())
                .setItems(choices, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: {
                                mListContacts.remove(longClickPos);
                                mAdapterContacts.notifyDataSetChanged();
                            }
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
    }

    /**
     * 为ListView填充数据
     * 
     * @param objects
     * @return
     */
    private List<VNowFriend> filledData(List<VNowFriend> list) {
        List<VNowFriend> mSortList = new ArrayList<VNowFriend>();
        if (null != list) {
            for (int i = 0; i < list.size(); i++) {
                VNowFriend friend = new VNowFriend();
                friend.setmName(list.get(i).getmName());
                friend.setmHeadUrl(list.get(i).getmHeadUrl());
                friend.setmNick(list.get(i).getmNick());
                friend.setmPhoneNum(list.get(i).getmPhoneNum());
                friend.setmPhoto(list.get(i).getmPhoto());
                // 汉字转换成拼音
                String pinyin = characterParser.getSelling(list.get(i).getmName());
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
        }
        return mSortList;
    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     * 
     * @param filterStr
     */
    private void filterData(String filterStr) {
        if (TextUtils.isEmpty(filterStr)) {
            mFilterDateList.clear();
            mFilterDateList.addAll(mListContacts);
        }
        else {
            mFilterDateList.clear();
            for (VNowFriend friend : mListContacts) {
                String name = friend.getmName();
                if (name.indexOf(filterStr.toString()) != -1
                        || characterParser.getSelling(name).startsWith(
                                filterStr.toString())) {
                    mFilterDateList.add(friend);
                }
            }
        }
        // 根据a-z进行排序
        Collections.sort(mFilterDateList, pinyinComparator);
        if (null != mAdapterContacts)
            mAdapterContacts.updateListView(mFilterDateList);
    }

    OnTouchingLetterChangedListener mTouchingLetterChangedListener = new OnTouchingLetterChangedListener() {
        @Override
        public void onTouchingLetterChanged(String s) {
            // TODO Auto-generated method stub
            if (!TextUtils.isEmpty(s)) {
                int position = mAdapterContacts.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mListviewContact.setSelection(position);
                }
            }
        }
    };

    private void showCallDlg(final VNowFriend locContact) {
        if (null != mDialog) {
            mDialog.dismiss();
            mDialog = null;
        }
        mDialog = new VNowAlertDlg(getActivity(), R.style.navComSettingDialogTheme);
        mDialog.show();
        mDialog.setTitle(R.string.str_alert_tip);
        mDialog.setContent(getString(R.string.str_call_title).replace("${CALL_NUM}", locContact.getmName()));
        mDialog.setOKButton(getString(R.string.str_call),
                new VNowAlertDlg.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent(getActivity(), ConfActivity.class);
                        if (mEditSearch.getText().toString().trim().length() == 0) {
                            intent.putExtra("callNumber", (new DES()).encrypt(locContact.getmPhoneNum()));
                            intent.putExtra("callName", locContact.getmName());
                        }
                        else {
                            intent.putExtra("callNumber", (new DES()).encrypt(locContact.getmPhoneNum()));
                            intent.putExtra("callName", locContact.getmName());
                        }
                        intent.putExtra("isCallin", false);
                        VNowRctContact rctItem = new VNowRctContact();
                        rctItem.setmStrUserId(mCore.getmUser().uuid);
                        rctItem.setmStrUserName(mCore.getmUser().name);
                        rctItem.setmStrConPhone((new DES()).encrypt(locContact.getmPhoneNum()));
                        rctItem.setmStrContactName(locContact.getmName());
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
//            showCallDlg(mListContacts.get(position - 1));
            mAdapterContacts.addToConference(position - 1);
        }
    };
    private AdapterView.OnItemLongClickListener mItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapter, View view,
                int position, long id) {
            // TODO Auto-generated method stub
            showItemLongClickDialog(position - 1);
            return false;
        }
    };

    @Override
    public void fetchObjectData() {}
}
