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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.nyist.vnow.R;
import com.nyist.vnow.adapter.VNowGroupAdapter;
import com.nyist.vnow.adapter.VNowGroupAdapter.DelListener;
import com.nyist.vnow.core.VNowApplication;
import com.nyist.vnow.core.VNowCore;
import com.nyist.vnow.dialog.AvcProgress;
import com.nyist.vnow.dialog.VNowGroupDlg;
import com.nyist.vnow.struct.Friend;
import com.nyist.vnow.struct.Group;
import com.nyist.vnow.ui.VNowGroupItemActivity;
import com.nyist.vnow.utils.CharacterParser;
import com.nyist.vnow.utils.PinyinComparator;
import com.nyist.vnow.view.ViEPullToRefreshListView;
import com.nyist.vnow.view.ViEPullToRefreshListView.OnRefreshListener;
import com.vnow.sdk.openapi.EventListener;
import com.vnow.sdk.openapi.IVNowAPI;

public class VNowFragmentGroup extends Fragment implements DelListener {

	private final int LOAD_GROUP_FINISH = 0x301;
	private final int LOAD_GROUP_GET_MORE = 0x302;
	private final int DEL_GROUP_SUCCESS = 0x303;
	private final int DEL_GROUP_FIELD = 0x304;
	private final int CREATE_GROUP_SUCCESS = 0x305;
	private final int CREATE_GROUP_FAILED = 0x306;
	private final int MODIFY_GROUP_SUCCESS = 0x307;
	private final int MODIFY_GROUP_FAILED = 0x308;

	private VNowCore mCore;

	private MyEventListener mCallBackListener;
	private IVNowAPI mVNowAPI;
	private ViEPullToRefreshListView mListviewGroup;
	private CheckBox mCheckDelMode;
	private AvcProgress mProgressLoading;
	private Button mBtnCreateGrp;
	private Button mBtnEditGrp;

	private List<Group> mListGroup = null;
	
	private boolean HadGetGroup = false;

	private String _delUuId;
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;

	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;

	private VNowGroupAdapter mAdapterGroup;

	private Handler mMainHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LOAD_GROUP_FINISH:{
				mListviewGroup.setVisibility(View.VISIBLE);
				mListGroup.clear();
				mListGroup.addAll(mCore.getGroupList(true,""));
				if (mListGroup.size() > 0) {
					if(null != mAdapterGroup){
						mAdapterGroup.notifyDataSetChanged();
					}
				}else{
					if(null != mAdapterGroup){
						mAdapterGroup.notifyDataSetChanged();
						mCheckDelMode.setChecked(false);
				}
				}
				mListviewGroup.onRefreshComplete();
			}
				break;
			case DEL_GROUP_SUCCESS: {
				mCore.doQueryGroupList();
				VNowApplication.the().showToast(
						getString(R.string.str_del_group_success));
			}
				break;
			case DEL_GROUP_FIELD: {
				VNowApplication.the().showToast(
						getString(R.string.str_del_group_field));
			}
				break;
			case CREATE_GROUP_SUCCESS: {
				mCore.doQueryGroupList();
				VNowApplication.the().showToast(
						getString(R.string.str_create_group_success));
			}
				break;
			case CREATE_GROUP_FAILED: {
				VNowApplication.the().showToast(
						getString(R.string.str_create_group_failed));
			}
				break;
			case LOAD_GROUP_GET_MORE: {
				mListviewGroup.onRefreshComplete();
				mAdapterGroup.notifyDataSetChanged();
			}
				break;
			case MODIFY_GROUP_SUCCESS: {
				mCore.doQueryGroupList();
				VNowApplication.the().showToast(
						getString(R.string.str_modify_group_success));
			}
				break;
			case MODIFY_GROUP_FAILED: {
				VNowApplication.the().showToast(
						getString(R.string.str_modify_group_failed));
			}
				break;
			}

		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.vnow_fragment_group, container,
				false);
		mListGroup = new ArrayList<Group>();
		initGroupContactsShowUI(view);
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
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initGroupData();
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

	/**
	 * the method to init add friends show view
	 */
	private void initGroupContactsShowUI(View view) {
		mListviewGroup = (ViEPullToRefreshListView) view
				.findViewById(R.id.group_list_view);
		mBtnCreateGrp = (Button) view.findViewById(R.id.btn_create_group);
		mBtnEditGrp = (Button) view.findViewById(R.id.btn_modify_group);
		mCheckDelMode = (CheckBox) view.findViewById(R.id.check_del_group);
		mCheckDelMode.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					mCheckDelMode.setText(R.string.str_cancel_delete_contact);
				}else{
					mCheckDelMode.setText(R.string.str_delete_contact);
				}
				if(null!=mAdapterGroup){
					mAdapterGroup.setDelable(isChecked);
					mAdapterGroup.notifyDataSetChanged();
				}
				
			}
		});
		mAdapterGroup = new VNowGroupAdapter(getActivity(),
				mListGroup,VNowFragmentGroup.this);
		mListviewGroup.setAdapter(mAdapterGroup);
		mListviewGroup.setOnItemClickListener(mListItemClickListener);
		mListviewGroup.setOnItemLongClickListener(mItemLongClickListener);
		mBtnCreateGrp.setOnClickListener(mBtnOnClickListener);
		mBtnEditGrp.setOnClickListener(mBtnOnClickListener);
	}

	/**
	 * the method to init the contacts from phone
	 */
	private void initGroupData() {
		if (mCore.getGroupList().size() <= 0) {
			// startProgress();
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (mCore.getGroupList().size() == 0) {
						mCore.doQueryGroupList();
					}
				}
			}).start();
		} else {
			mMainHandler.sendEmptyMessageDelayed(LOAD_GROUP_FINISH,200);
		}
	}
	
	private void showEditGrpDlg(final Group group) {
		final VNowGroupDlg dlg = new VNowGroupDlg(getActivity(),
				R.style.navComSettingDialogTheme);
		dlg.show();
		dlg.setEditGrpName(group.getName());
		dlg.setEditGrpDesc(group.getmPyName());
		dlg.setOKButton(getString(R.string.str_modify),
				new VNowGroupDlg.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int i) {
						// TODO Auto-generated method stub
						if (dlg.getGrpName().equals("")) {
							VNowApplication.the().showToast(
									getString(R.string.str_modify_not_null));
							return;
						} else {
							mCore.doModifyGroup(dlg.getGrpName(), group.getUuid());
						}
					}
				});
		dlg.setCancelButton(getString(R.string.str_dlg_cancel),
				new VNowGroupDlg.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int i) {
						// TODO Auto-generated method stub

					}
				});
	}

	private void showGrpDlg() {
		final VNowGroupDlg dlg = new VNowGroupDlg(getActivity(),
				R.style.navComSettingDialogTheme);
		dlg.show();
		dlg.setOKButton(getString(R.string.str_dlg_create),
				new VNowGroupDlg.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int i) {
						// TODO Auto-generated method stub
						if (dlg.getGrpName().equals("")) {
							VNowApplication.the().showToast(
									getString(R.string.str_grp_name_not_null));
							return;
						} else {
							mCore.doCreateGroup(dlg.getGrpName());
						}
					}
				});
		dlg.setCancelButton(getString(R.string.str_dlg_cancel),
				new VNowGroupDlg.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int i) {
						// TODO Auto-generated method stub

					}
				});
	}

	/**
	 * the method to show recent chat message dialog when you long clik recent
	 * chat listview item
	 * 
	 * @param longClickPos
	 */
	private void showItemLongClickDialog(final int longClickPos) {
		final Group grp = mListGroup.get(longClickPos);
		String[] choices = new String[1];
		choices[0] = getString(R.string.str_modify);

		new AlertDialog.Builder(getActivity()).setTitle(grp.getName())
				.setItems(choices, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0: {
							showEditGrpDlg(grp);
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
//	private List<Group> filledData(List<Group> list) {
//		List<Group> mSortList = new ArrayList<Group>();
//
//		for (int i = 0; i < list.size(); i++) {
//			Group grp = new Group();
//			grp.setName(list.get(i).getName());
//			// 汉字转换成拼音
//			String pinyin = characterParser.getSelling(list.get(i).getName());
//			String sortString = pinyin.substring(0, 1).toUpperCase();
//			grp.setmPyName(pinyin);
//			grp.setA_uuid(list.get(i).getA_uuid());
//			grp.setCreatetime(list.get(i).getCreatetime());
//			grp.setHead(list.get(i).getHead());
//			grp.setName(list.get(i).getName());
//			grp.setParentId(list.get(i).getParentId());
//			grp.setPhone(list.get(i).getPhone());
//			grp.setType(list.get(i).getType());
//			grp.setUpdatenum(list.get(i).getUpdatenum());
//			grp.setUuid(list.get(i).getUuid());
//			// 正则表达式，判断首字母是否是英文字母
//			if (sortString.matches("[A-Z]")) {
//				grp.setmSortLetters(sortString.toUpperCase());
//			} else {
//				grp.setmSortLetters("#");
//			}
//			if (grp.getType().equals("1")) {
//				mSortList.add(grp);
//			}
//		}
//		return mSortList;
//
//	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<Group> filterDateList = new ArrayList<Group>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = mListGroup;
		} else {
			filterDateList.clear();
			for (Group friend : mListGroup) {
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
		mAdapterGroup.updateListView(filterDateList);
	}

	private AdapterView.OnItemClickListener mListItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adapter, View view,
				int position, long id) {
			Intent intent = new Intent(getActivity(),
					VNowGroupItemActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("g_uuid", mListGroup.get(position - 1).getUuid());
			bundle.putString("g_name", mListGroup.get(position - 1).getName());
			intent.putExtras(bundle);
			startActivity(intent);
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

	private View.OnClickListener mBtnOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();
			if (id == R.id.btn_create_group) {
				showGrpDlg();
			} else if (id == R.id.btn_modify_group) {

			}
		}
	};

	private OnRefreshListener mRefreshListener = new OnRefreshListener() {

		@Override
		public void onRefresh() {
			// TODO Auto-generated method stub
			mCore.doQueryGroupList();
		}
	};

	// web 接口回调
	private class MyEventListener extends EventListener {
		public void onResponseQueryGroupList(boolean bSuccess, String jsonResult) {
			// TODO Auto-generated method stub
			super.onResponseQueryGroupList(bSuccess, jsonResult);
			mMainHandler.sendEmptyMessageDelayed(LOAD_GROUP_FINISH,200);

		}

		@Override
		public void onResponseCreateGroup(boolean bSuccess) {
			// TODO Auto-generated method stub
			super.onResponseCreateGroup(bSuccess);
			if (bSuccess) {
				mMainHandler.sendEmptyMessage(CREATE_GROUP_SUCCESS);
			} else {
				mMainHandler.sendEmptyMessage(CREATE_GROUP_FAILED);
			}
		}
		
		@Override
		public void onResponseModifyGroup(boolean bSuccess) {
			// TODO Auto-generated method stub
			super.onResponseModifyGroup(bSuccess);
			if (bSuccess) {
				mMainHandler.sendEmptyMessage(MODIFY_GROUP_SUCCESS);
			} else {
				mMainHandler.sendEmptyMessage(MODIFY_GROUP_FAILED);
			}
		}

		public void onResponseDelGroup(boolean bSuccess) {
			// TODO Auto-generated method stub
			if (bSuccess) {
				mMainHandler.sendEmptyMessage(DEL_GROUP_SUCCESS);
				mCore.deleteDBGroupItem(_delUuId,1);
			} else {
				mMainHandler.sendEmptyMessage(DEL_GROUP_FIELD);
			}
			_delUuId = null;
		}
	}

	@Override
	public void onDelGroup(String uuid) {
		// TODO Auto-generated method stub
		mCore.doDelGroup(uuid);
		_delUuId = uuid;
	}

}
