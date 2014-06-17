package com.nyist.vnow.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.nyist.vnow.R;
import com.nyist.vnow.adapter.VNowColleageAdapter;
import com.nyist.vnow.core.VNowApplication;
import com.nyist.vnow.core.VNowCore;
import com.nyist.vnow.dialog.AvcProgress;
import com.nyist.vnow.dialog.VNowAlertDlg;
import com.nyist.vnow.struct.Colleage;
import com.nyist.vnow.struct.VNowRctContact;
import com.nyist.vnow.ui.ConfActivity;
import com.nyist.vnow.utils.CharacterParser;
import com.nyist.vnow.utils.PinyinComparator;
import com.nyist.vnow.view.SideBar;
import com.nyist.vnow.view.SideBar.OnTouchingLetterChangedListener;
import com.nyist.vnow.view.ViEPullToRefreshListView;
import com.nyist.vnow.view.ViEPullToRefreshListView.OnRefreshListener;
import com.vnow.sdk.openapi.EventListener;
import com.vnow.sdk.openapi.IVNowAPI;

public class VNowFragmentColleague extends Fragment {

	private final int LOAD_COLLEAGUE_FINISH = 0x301;
	private final int LOAD_COLLEAGUE_GET_MORE = 0x302;

	private VNowCore mCore;

	private MyEventListener mCallBackListener;
	private IVNowAPI mVNowAPI;

	private ViEPullToRefreshListView mListviewContact;
	private SideBar mIndexBar;
	private TextView mTxtContactIndex;
	private AvcProgress mProgressLoading;
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
			} else if (msg.what == LOAD_COLLEAGUE_GET_MORE) {
				mListviewContact.onRefreshComplete();
				if(null!=mAdapterColleage)
					mAdapterColleage.notifyDataSetChanged();
			}
		};
	};

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
		mCore = VNowApplication.the().getCore();
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		mCallBackListener = new MyEventListener();
		mVNowAPI = IVNowAPI.createIVNowAPI();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mVNowAPI.setEventListener(mCallBackListener);
		initColleageData();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mVNowAPI.removeEventListener(mCallBackListener);
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		if(null!=mDialog)
			mDialog.dismiss();
		super.onStop();
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
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	/**
	 * the method to init the contacts from phone
	 */
	private void initColleageData() {
		if (mCore.getColleageList().size() <= 0) {
			if (!HadGetColleague) {
				startProgress();
			}			
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (mCore.getColleageList().size() == 0) {
						mCore.doQueryColleagueList();
					}
				}
			}).start();
		} else {
			mMainHandler.sendEmptyMessage(LOAD_COLLEAGUE_FINISH);
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
		} else {
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
			} else {
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
			if(null == mListColleage)
				return;
			filterDateList.clear();
			filterDateList.addAll(mListColleage);
		} else {
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
			// TODO Auto-generated method stub
			if(null == mAdapterColleage)
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
			// TODO Auto-generated method stub
			mCore.doQueryColleagueList();
		}
	};

	
	private void showCallDlg(final Colleage colleage){
		if(null != mDialog){
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
						rctItem.setmStrUserId(mCore.getMySelf().uuid);
						rctItem.setmStrUserName(mCore.getMySelf().name);
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
					public void onClick(DialogInterface dialog, int i) {
						
						// TODO Auto-generated method stub
					}
				});
	}
	private AdapterView.OnItemClickListener mListItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adapter, View view,
				int position, long id) {
			showCallDlg(mListColleage.get(position-1));
		}
	};

	// web 接口回调
	private class MyEventListener extends EventListener {

		@Override
		public void onResponseQueryColleageList(boolean bSuccess, String result) {
			// TODO Auto-generated method stub
			super.onResponseQueryColleageList(bSuccess, result);
			HadGetColleague = true;
			mMainHandler.sendEmptyMessage(LOAD_COLLEAGUE_FINISH);
		}
	}
}
