package com.nyist.vnow.fragment;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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
import android.widget.TextView;

import com.google.myjson.reflect.TypeToken;
import com.nyist.vnow.R;
import com.nyist.vnow.adapter.ConfColleagueSelectAdapter;
import com.nyist.vnow.adapter.VNowColleageAdapter;
import com.nyist.vnow.adapter.ConfColleagueSelectAdapter.OnItemCheckedChangeListener;
import com.nyist.vnow.core.VNowApplication;
import com.nyist.vnow.core.VNowCore;
import com.nyist.vnow.db.ColleageDTOController;
import com.nyist.vnow.db.RefreshManager;
import com.nyist.vnow.dialog.VNowAlertDlg;
import com.nyist.vnow.event.SelectedMember;
import com.nyist.vnow.exception.UrlException;
import com.nyist.vnow.struct.Colleage;
import com.nyist.vnow.struct.ModuleEntity;
import com.nyist.vnow.struct.VnowInfo;
import com.nyist.vnow.struct.User;
import com.nyist.vnow.struct.VNowRctContact;
import com.nyist.vnow.ui.ChoseMemberActivity;
import com.nyist.vnow.ui.ConfActivity;
import com.nyist.vnow.utils.CharacterParser;
import com.nyist.vnow.utils.CommonUtil;
import com.nyist.vnow.utils.DialogUtil;
import com.nyist.vnow.utils.LogTag;
import com.nyist.vnow.utils.NetUtil;
import com.nyist.vnow.utils.PinyinComparator;
import com.nyist.vnow.utils.Session;
import com.nyist.vnow.utils.ToastUtil;
import com.nyist.vnow.view.SideBar;
import com.nyist.vnow.view.SideBar.OnTouchingLetterChangedListener;
import com.nyist.vnow.view.ViEPullToRefreshListView;
import com.nyist.vnow.view.ViEPullToRefreshListView.OnRefreshListener;
import com.stay.AppException;
import com.stay.net.Request;
import com.stay.net.Request.RequestMethod;
import com.stay.net.callback.JsonCallback;
import com.stay.net.callback.StringCallback;
import com.stay.utilities.TextUtil;
import com.vnow.sdk.openapi.EventListener;
import com.vnow.sdk.openapi.IVNowAPI;

import de.greenrobot.event.EventBus;

public class ChoseColleagueFragment extends BaseFragment implements OnItemCheckedChangeListener {
    private VNowCore mCore;
    private ViEPullToRefreshListView mListviewContact;
    private SideBar mIndexBar;
    private TextView mTxtContactIndex;
    private Dialog mProgressLoading;
    private EditText mEditSearch;
    private List<Colleage> mListColleage = null;
    private ConfColleagueSelectAdapter mConfMemberSelectAdapter;
    private VNowAlertDlg mDialog;
    private boolean HadGetColleague = false;
    private ModuleEntity mModuEntity;
    private HashMap<String, ImageView> mSelectMemberHashMap = new HashMap<String,
            ImageView>();
    private ArrayList<String> mSelectedColleagues = new ArrayList<String>();
    private LinearLayout mSelectedMemberLayout;
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;
    int i = 0;
    
    public static BaseFragment newInstance(ModuleEntity moduleEntity) {
        ChoseColleagueFragment fragment = new ChoseColleagueFragment();
        Bundle args = new Bundle();
        args.putSerializable(VnowInfo.VNOW_INFO, moduleEntity);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vnow_fragment_colleague,
                container, false);
        initColleageContactsShowUI(view);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModuEntity = (ModuleEntity) getArguments().getSerializable(
                VnowInfo.VNOW_INFO);
    }

    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        
        // EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // EventBus.getDefault().unregister(this);
    }

    @Override
    public void onStop() {
        if (null != mDialog)
            mDialog.dismiss();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopProgress();
    }

    /**
     * the method to init add friends show view
     */
    private void initColleageContactsShowUI(View view) {
        mListviewContact = (ViEPullToRefreshListView) view
                .findViewById(R.id.listview_add_friends);
        mIndexBar = (SideBar) view.findViewById(R.id.sidebar_add_friends);
        mEditSearch = (EditText) view
                .findViewById(R.id.edit_text_contacts_search);
        mTxtContactIndex = (TextView) view.findViewById(R.id.dialog);
        mTxtContactIndex.setVisibility(View.GONE);
        mIndexBar.setTextView(mTxtContactIndex);
        mListviewContact.setVisibility(View.GONE);
        mIndexBar.setVisibility(View.GONE);
        mListviewContact.setonRefreshListener(mRefreshListener);
        // 设置右侧触摸监听
        mIndexBar
                .setOnTouchingLetterChangedListener(mTouchingLetterChangedListener);
        // 根据输入框输入值的改变来过滤搜索
        mEditSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
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
    private void initColleageData() {
        if (NetUtil.checkNet(getActivity())) {
            String colleagueUrl;
            try {
                colleagueUrl = mCore.getColleagueUrl();
            }
            catch (UrlException e) {
                ToastUtil.getInstance(getActivity()).showShort(e.getMessage());
                return;
            }
            LogTag.e("colleagueUrl", colleagueUrl);
            Request request = new Request(colleagueUrl, RequestMethod.GET);
            request.setCallback(new JsonCallback<ArrayList<Colleage>>() {
                private String uuid = Session.newInstance(getActivity()).getUuid();
                @Override
                public ArrayList<Colleage> onPreRequest() {
                    // 判断是否需要刷新，如果要刷新就返回null
                    if (RefreshManager.getInstance().needRefresh(Colleage.class)) {
                        LogTag.d("needRefresh：", "needRefresh");
                        return null;
                    }
                    ArrayList<Colleage> entities = ColleageDTOController.queryAll(uuid);
                    if (TextUtil.isValidate(entities)) {
                        stopProgress();
                    }
                    LogTag.d("onPreRequest：", "" + (TextUtil.isValidate(entities) ? entities.size() : 0));
                    return entities;
                }

                // 回调子线程的onPreHandle方法
                @Override
                public ArrayList<Colleage> onPreHandle(ArrayList<Colleage> t) {
                    // 存入数据库
                    ColleageDTOController.addOrUpdate(t, uuid);
                    return super.onPreHandle(t);
                }

                @Override
                public void onSuccess(ArrayList<Colleage> result) {
                    stopProgress();
                    mListviewContact.setVisibility(View.VISIBLE);
                    mIndexBar.setVisibility(View.VISIBLE);
                    mListColleage.clear();
                    if (result.size() > 0) {
                        // mListColleage.addAll(result);
                        mListColleage.addAll(filledData(result));
                        // 根据a-z进行排序源数据
                        Collections.sort(mListColleage, pinyinComparator);
                        mConfMemberSelectAdapter = new ConfColleagueSelectAdapter(getActivity(),
                                mListColleage, mSelectedColleagues);
                        mListviewContact.setAdapter(mConfMemberSelectAdapter);
                        mConfMemberSelectAdapter.setOnItemCheckedChangeListener(ChoseColleagueFragment.this);
                    }
                    mListviewContact.onRefreshComplete();
                }

                @Override
                public void onFailure(AppException result) {
                    LogTag.e("onFailure", result.toString());
                    stopProgress();
                }

                @Override
                public void onPreExecute() {
                    startProgress();
              
                    LogTag.e("onPreExecute()", i++);
                }
            }.setReturnType(new TypeToken<ArrayList<Colleage>>() {}.getType()));
            request.execute();
        }
        else {
            ToastUtil.getInstance(getActivity()).showShort(getString(R.string.no_network));
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
    private List<Colleage> filledData(List<Colleage> list) {
        List<Colleage> mSortList = new ArrayList<Colleage>();
        for (int i = 0; i < list.size(); i++) {
            Colleage friend = new Colleage();
            friend.setG_name(list.get(i).getG_name());
            friend.setG_phone(list.get(i).getG_phone());
            // 汉字转换成拼音
            String pinyin = characterParser.getSelling(list.get(i).getG_name());
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
        List<Colleage> filterDateList = new ArrayList<Colleage>();
        if (TextUtils.isEmpty(filterStr)) {
            if (null == mListColleage)
                return;
            filterDateList.clear();
            filterDateList.addAll(mListColleage);
        }
        else {
            filterDateList.clear();
            for (Colleage friend : mListColleage) {
                String name = friend.getG_name();
                if (name.indexOf(filterStr.toString()) != -1
                        || characterParser.getSelling(name).startsWith(
                                filterStr.toString())) {
                    filterDateList.add(friend);
                }
            }
        }
        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        if (null != mConfMemberSelectAdapter) {
            mConfMemberSelectAdapter.updateListView(filterDateList);
        }
    }

    OnTouchingLetterChangedListener mTouchingLetterChangedListener = new OnTouchingLetterChangedListener() {
        @Override
        public void onTouchingLetterChanged(String s) {
            if (null == mConfMemberSelectAdapter)
                return;
            int position = mConfMemberSelectAdapter.getPositionForSection(s.charAt(0));
            if (position != -1) {
                mListviewContact.setSelection(position);
            }
        }
    };
    private OnRefreshListener mRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh() {}
    };

    @Override
    public void fetchObjectData() {
        mCore = VNowApplication.newInstance().getCore();
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        mListColleage = new ArrayList<Colleage>();
        initColleageData();
    }

    @Override
    public void OnCheckedChange(final CompoundButton checkBox, final int position, final String path, boolean isChecked) {
        mSelectedMemberLayout = ((ChoseMemberActivity) getActivity()).getmSelectedMemberLayout();
        if (isChecked) {
            if (!mSelectMemberHashMap.containsKey(path)) {
                mSelectedColleagues.add(path);
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
                // mConfirm.setText("确定" + "(" + mSelectedColleagues.size() +
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
            removeOneData(mSelectedColleagues, path);
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
