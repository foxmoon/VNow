package com.nyist.vnow.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nyist.vnow.R;
import com.nyist.vnow.adapter.ConfPhoneContactSelectAdapter;
import com.nyist.vnow.adapter.ConfPhoneContactSelectAdapter.OnItemCheckedChangeListener;
import com.nyist.vnow.adapter.VNowPhoneContactAdapter;
import com.nyist.vnow.core.VNowApplication;
import com.nyist.vnow.core.VNowCore;
import com.nyist.vnow.dialog.VNowAlertDlg;
import com.nyist.vnow.struct.Colleage;
import com.nyist.vnow.struct.ModuleEntity;
import com.nyist.vnow.struct.VNowFriend;
import com.nyist.vnow.struct.VNowRctContact;
import com.nyist.vnow.struct.VnowInfo;
import com.nyist.vnow.ui.ChoseMemberActivity;
import com.nyist.vnow.ui.ConfActivity;
import com.nyist.vnow.utils.CharacterParser;
import com.nyist.vnow.utils.DES;
import com.nyist.vnow.utils.DialogUtil;
import com.nyist.vnow.utils.PinyinComparator;
import com.nyist.vnow.view.SideBar;
import com.nyist.vnow.view.SideBar.OnTouchingLetterChangedListener;

import de.greenrobot.event.EventBus;

public class ChosePhoneContactFragment extends BaseFragment implements OnItemCheckedChangeListener {
    private final int LOAD_FRIENDS_FINISH = 0x301;
    private ListView mListviewContact;
    private SideBar mIndexBar;
    private TextView mTxtContactIndex;
    private Dialog mProgressLoading;
    private EditText mEditSearch;
    private List<VNowFriend> mListContacts = null;
    private List<VNowFriend> mFilterDateList = null;
    private ConfPhoneContactSelectAdapter mAdapterContacts;
    private VNowAlertDlg mDialog;
    private ModuleEntity mModuEntity;
    private ArrayList<String> mSelectedContacts = new ArrayList<String>();
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
                    mAdapterContacts = new ConfPhoneContactSelectAdapter(
                            getActivity(), mListContacts, mSelectedContacts);
                    mListviewContact.setAdapter(mAdapterContacts);
                    mAdapterContacts.setOnItemCheckedChangeListener(ChosePhoneContactFragment.this);
                }
            }
        };
    };

    public ChosePhoneContactFragment() {}

    public static BaseFragment newInstance(ModuleEntity moduleEntity) {
        ChosePhoneContactFragment fragment = new ChosePhoneContactFragment();
        Bundle args = new Bundle();
        args.putSerializable(VnowInfo.VNOW_INFO, moduleEntity);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vnow_fragment_phone_contacts,
                container, false);
        mListContacts = new ArrayList<VNowFriend>();
        initPhoneContactsShowUI(view);
        return view;
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModuEntity = (ModuleEntity) getArguments().getSerializable(
                VnowInfo.VNOW_INFO);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
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
//            startProgress();
            new Thread(new Runnable() {
                @Override
                public void run() {
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
            mProgressLoading = DialogUtil.createLoadingDialog(getActivity(), getString(R.string.str_contact_loading), true);
        }
        mProgressLoading.show();
    }

    /**
     * the method to stop the progressbar when loaded data
     */
    private void stopProgress() {
        if (null != mProgressLoading) {
            mProgressLoading.dismiss();
            mProgressLoading = null;
        }
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
            if (!TextUtils.isEmpty(s)) {
                int position = mAdapterContacts.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mListviewContact.setSelection(position);
                }
            }
        }
    };
    private LinearLayout mSelectedMemberLayout;
    private HashMap<String, ImageView> mSelectMemberHashMap = new HashMap<String,
            ImageView>();

    @Override
    public void fetchObjectData() {
        mCore = VNowApplication.newInstance().getCore();
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        mFilterDateList = new ArrayList<VNowFriend>();
        initPhoneData();
    }

    @Override
    public void OnCheckedChange(final CompoundButton checkBox, final int position, final String path, boolean isChecked) {
        mSelectedMemberLayout = ((ChoseMemberActivity) getActivity()).getmSelectedMemberLayout();
        if (isChecked) {
            if (!mSelectMemberHashMap.containsKey(path)) {
                mSelectedContacts.add(path);
                EventBus.getDefault().post(path);
                final HorizontalScrollView mScrollView = ((ChoseMemberActivity) getActivity()).getmScrollView();
                ImageView mSelectedUserPhoto = (ImageView) LayoutInflater
                        .from(getActivity())
                        .inflate(R.layout.choose_imageview, mSelectedMemberLayout, false);
                mSelectedMemberLayout.addView(mSelectedUserPhoto);
                mSelectedMemberLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int off = mSelectedMemberLayout.getMeasuredWidth() - mScrollView.getWidth();
                        if (off > 0) {
                            mScrollView.smoothScrollTo(off, 0);
                        }
                    }
                }, 100);
                mSelectMemberHashMap.put(path, mSelectedUserPhoto);
                mSelectedUserPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkBox.setChecked(false);
                        removePath(path);
                    }
                });
                // mConfirm.setText("确定" + "(" + mSelectedContacts.size() +
                // ")");
                // mConfirm.setEnabled(true);
            }
        }
        else {
            removePath(path);
        }
    }

    private boolean removePath(String path)
    {
        if (mSelectMemberHashMap.containsKey(path))
        {
            mSelectedMemberLayout.removeView(mSelectMemberHashMap.get(path));
            mSelectMemberHashMap.remove(path);
            removeOneData(mSelectedContacts, path);
            EventBus.getDefault().post(path);
            return true;
        }
        else
        {
            return false;
        }
    }

    private void removeOneData(ArrayList<String> arrayList,
            String path)
    {
        for (int i = 0; i < arrayList.size(); i++)
        {
            if (arrayList.get(i).equals(path))
            {
                arrayList.remove(i);
                return;
            }
        }
    }
}
