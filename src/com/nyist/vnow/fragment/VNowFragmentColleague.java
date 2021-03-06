package com.nyist.vnow.fragment;

import java.util.ArrayList;
import java.util.Collections;
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
import android.widget.EditText;
import android.widget.TextView;

import com.google.myjson.reflect.TypeToken;
import com.nyist.vnow.R;
import com.nyist.vnow.adapter.VNowColleageAdapter;
import com.nyist.vnow.core.VNowApplication;
import com.nyist.vnow.core.VNowCore;
import com.nyist.vnow.db.ColleageDTOController;
import com.nyist.vnow.db.RefreshManager;
import com.nyist.vnow.dialog.VNowAlertDlg;
import com.nyist.vnow.exception.UrlException;
import com.nyist.vnow.struct.Colleage;
import com.nyist.vnow.struct.ModuleEntity;
import com.nyist.vnow.struct.User;
import com.nyist.vnow.struct.VNowRctContact;
import com.nyist.vnow.struct.VnowInfo;
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

public class VNowFragmentColleague extends BaseFragment {
    private final int LOAD_COLLEAGUE_FINISH = 0x301;
    private final int LOAD_COLLEAGUE_GET_MORE = 0x302;
    private VNowCore mCore;
    // private MyEventListener mCallBackListener;
    private ViEPullToRefreshListView mListviewContact;
    private SideBar mIndexBar;
    private TextView mTxtContactIndex;
    private Dialog mProgressLoading;
    private EditText mEditSearch;
    private List<Colleage> mListColleage = null;
    private VNowColleageAdapter mAdapterColleage;
    private VNowAlertDlg mDialog;
    private boolean HadGetColleague = false;
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;
    private Handler mMainHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == LOAD_COLLEAGUE_FINISH) {
                mListviewContact.setVisibility(View.VISIBLE);
                mIndexBar.setVisibility(View.VISIBLE);
                mListColleage.clear();
                stopProgress();
                if (mCore.getColleageList().size() > 0) {
                    mListColleage.addAll(filledData(mCore.getColleageList()));
                    // 根据a-z进行排序源数据
                    Collections.sort(mListColleage, pinyinComparator);
                    mAdapterColleage = new VNowColleageAdapter(getActivity(),
                            mListColleage);
                    mListviewContact.setAdapter(mAdapterColleage);
                    mListviewContact
                            .setOnItemClickListener(mListItemClickListener);
                }
                mListviewContact.onRefreshComplete();
            }
            else if (msg.what == LOAD_COLLEAGUE_GET_MORE) {
                mListviewContact.onRefreshComplete();
                if (null != mAdapterColleage)
                    mAdapterColleage.notifyDataSetChanged();
            }
        };
    };
    private ModuleEntity mModuEntity;

    public static BaseFragment newInstance(ModuleEntity moduleEntity) {
        VNowFragmentColleague fragment = new VNowFragmentColleague();
        Bundle args = new Bundle();
        args.putSerializable(VnowInfo.VNOW_INFO, moduleEntity);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.vnow_fragment_colleague,
                container, false);
        mListColleage = new ArrayList<Colleage>();
        initColleageContactsShowUI(view);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mModuEntity = (ModuleEntity) getArguments().getSerializable(
                VnowInfo.VNOW_INFO);
        mCore = VNowApplication.newInstance().getCore();
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
       
        // mCallBackListener = new MyEventListener();
        // mVNowAPI = IVNowAPI.newInstance();
    }

    @Override
    public void onResume() {
        super.onResume();
        // mCore.doSetEventListener(mCallBackListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        // mCore.doRemoveEventListener(mCallBackListener);
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
            request = new Request(colleagueUrl, RequestMethod.GET);
            request.setCallback(new JsonCallback<ArrayList<Colleage>>() {
                private String uuid = Session.newInstance(getActivity()).getUuid();

                @Override
                public ArrayList<Colleage> onPreRequest() {
                    // 判断是否需要刷新，如果要刷新就返回null
                    if (RefreshManager.getInstance().needRefresh(Colleage.class)) {
                        LogTag.d("needRefresh：", "needRefresh");
                        return null;
                    }
                    // 如果不需要刷新，则查询数据库
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
                        mListColleage.addAll(filledData(result));
                        // 根据a-z进行排序源数据
                        Collections.sort(mListColleage, pinyinComparator);
                        mAdapterColleage = new VNowColleageAdapter(getActivity(),
                                mListColleage);
                        mListviewContact.setAdapter(mAdapterColleage);
                        mListviewContact
                                .setOnItemClickListener(mListItemClickListener);
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
                }
            }.setReturnType(new TypeToken<ArrayList<Colleage>>() {}.getType()));
            request.execute();
        }
        else {
            ToastUtil.getInstance(getActivity()).showShort(getString(R.string.no_network));
        }
        // if (mCore.getColleageList().size() <= 0) {
        // if (!HadGetColleague) {
        // startProgress();
        // }
        // new Thread(new Runnable() {
        // @Override
        // public void run() {
        // // TODO Auto-generated method stub
        // if (mCore.getColleageList().size() == 0) {
        // if (NetUtil.checkNet(getActivity())) {
        // // mCore.doQueryColleagueList();
        //
        // }
        // else {
        // ToastUtil.getInstance(getActivity()).showShort(getString(R.string.no_network));
        // }
        // }
        // }
        // }).start();
        // }
        // else {
        // mMainHandler.sendEmptyMessage(LOAD_COLLEAGUE_FINISH);
        // }
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
        if (null != mAdapterColleage) {
            mAdapterColleage.updateListView(filterDateList);
        }
    }

    OnTouchingLetterChangedListener mTouchingLetterChangedListener = new OnTouchingLetterChangedListener() {
        @Override
        public void onTouchingLetterChanged(String s) {
            if (null == mAdapterColleage)
                return;
            int position = mAdapterColleage.getPositionForSection(s.charAt(0));
            if (position != -1) {
                mListviewContact.setSelection(position);
            }
        }
    };
    private OnRefreshListener mRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh() {
            request.execute();
        }
    };

    private void showCallDlg(final Colleage colleage) {
        if (null != mDialog) {
            mDialog.dismiss();
            mDialog = null;
        }
        mDialog = new VNowAlertDlg(getActivity(), R.style.navComSettingDialogTheme);
        mDialog.show();
        mDialog.setTitle(R.string.str_alert_tip);
        mDialog.setContent(getString(R.string.str_call_title).replace("${CALL_NUM}", colleage.getG_name()));
        mDialog.setOKButton(getString(R.string.str_call),
                new VNowAlertDlg.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent(getActivity(), ConfActivity.class);
                        intent.putExtra("callNumber", colleage.getG_phone());
                        intent.putExtra("callName", colleage.getG_name());
                        intent.putExtra("isCallin", false);
                        VNowRctContact rctItem = new VNowRctContact();
                        rctItem.setmStrUserId(mCore.getUuid());
                        rctItem.setmStrUserName(mCore.getUserName());
                        rctItem.setmStrConPhone(colleage.getG_phone());
                        rctItem.setmStrContactName(colleage.getG_name());
                        rctItem.setmCallTime(System.currentTimeMillis());
                        rctItem.setmIsCallIn(false);
                        mCore.insertCallHistory(rctItem);
                        startActivity(intent);
                    }
                });
        mDialog.setCancelButton(getString(R.string.str_cancel),
                new VNowAlertDlg.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {}
                });
    }

    private AdapterView.OnItemClickListener mListItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapter, View view,
                int position, long id) {
            showCallDlg(mListColleage.get(position - 1));
        }
    };
    private Request request;

    // // web 接口回调
    // private class MyEventListener extends EventListener {
    // @Override
    // public void onResponseQueryColleageList(boolean bSuccess, String result)
    // {
    // super.onResponseQueryColleageList(bSuccess, result);
    // HadGetColleague = true;
    // mMainHandler.sendEmptyMessage(LOAD_COLLEAGUE_FINISH);
    // }
    // }
    @Override
    public void fetchObjectData() {
        initColleageData();
    }
}
