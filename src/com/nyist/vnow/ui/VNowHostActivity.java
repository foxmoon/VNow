package com.nyist.vnow.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

import com.nyist.vnow.R;
import com.nyist.vnow.core.VNowApplication;
import com.nyist.vnow.core.VNowCore;
import com.nyist.vnow.fragment.VNowFragmentLogin;
import com.nyist.vnow.fragment.VNowFragmentRegist;
import com.nyist.vnow.fragment.VNowFragmentSetSvr;
import com.nyist.vnow.struct.User;
import com.nyist.vnow.struct.VNowRctContact;
import com.nyist.vnow.utils.ActionEvent;
import com.nyist.vnow.utils.CommonUtil;
import com.vnow.sdk.openapi.EventListener;

public class VNowHostActivity extends FragmentActivity {
	private ActionEvent mCurrentEvent;
	private VNowFragmentLogin mLoginFragment;
	private VNowFragmentRegist mRegistFragment;
	private VNowFragmentSetSvr mSetSvrFragment;
	private FragmentManager mFmanager;
	private MyEventListener mCallBackListener;
	private VNowCore mCore;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.vnow_host);
		mCore = VNowApplication.the().getCore();
		CommonUtil.set_httpUrl(VNowApplication.the().getSetting(getString(R.string.setting_media_server_ip),getString(R.string.setting_defult_server_ip)));
		initUI();
	}

	private void initUI() {
		mLoginFragment = new VNowFragmentLogin();
		mRegistFragment = new VNowFragmentRegist();
		mSetSvrFragment = new VNowFragmentSetSvr();
		mFmanager = getSupportFragmentManager();
		addFragment(mLoginFragment);
		mCurrentEvent = ActionEvent.ACTION_LOGIN;
		mCallBackListener = new MyEventListener();
		mCore.doSetEventListener(mCallBackListener);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mCore.doRemoveEventListener(mCallBackListener);
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (mCurrentEvent == ActionEvent.ACTION_REGIST) {
			addFragment(mLoginFragment);
			mCurrentEvent = ActionEvent.ACTION_LOGIN;
		} else if (mCurrentEvent == ActionEvent.ACTION_SETSVR) {
			addFragment(mLoginFragment);
			mCurrentEvent = ActionEvent.ACTION_LOGIN;
		} else {
			VNowApplication.the().getCore().doLogout();
			// VNowApplication.the().destroyCore();
			
			System.exit(0);
		}
	}

	private void addFragment(Fragment fragment) {
		FragmentTransaction ft = mFmanager.beginTransaction();
		ft.replace(R.id.host_fragment_plane, fragment);
		ft.commitAllowingStateLoss();
	}

	public void actionToFragment(ActionEvent event){
		switch (event) {
		case ACTION_REGIST:{
			addFragment(mRegistFragment);
			mCurrentEvent = ActionEvent.ACTION_REGIST;
		}
			break;
		case ACTION_LOGIN:{
			addFragment(mLoginFragment);
			mCurrentEvent = ActionEvent.ACTION_LOGIN;
		}
			break;
		case ACTION_SETSVR: {
			addFragment(mSetSvrFragment);
			mCurrentEvent = ActionEvent.ACTION_SETSVR;
		}
			break;
		}
	}
	public void actionLogin(){
		String strPhone = VNowApplication.the().getSetting(getString(R.string.setting_login_user_phone),null);
		String strpws = VNowApplication.the().getSetting(getString(R.string.setting_login_user_pwd),null);
		if(null!= strPhone&&null!=strpws){
			VNowApplication.the().getCore().doLogin(strPhone, strpws, true);
		}else{
			mCore.checkUpVersion(this, true);
		}
	}

	//web 接口回调
	private class MyEventListener extends EventListener {
		public void onResponseRegister(boolean bSuccess, String uuid) {
			if (bSuccess) {
				VNowApplication.the().showToast(
						getString(R.string.str_regist_success));
				VNowApplication
						.the()
						.getCore()
						.doLogin(
								VNowApplication.the().getCore().getMySelf().phone,
								VNowApplication.the().getCore().getMySelf().password,
								true);
			} else
				VNowApplication.the().showToast(
						getString(R.string.str_regist_field));
		}

		public void onResponseLogin(boolean bSuccess, User user) {
			if (bSuccess) {
				Intent intent = new Intent();
				intent.setClass(VNowHostActivity.this, VNowMainActivity.class);
				if (null != VNowApplication.the().getCore().getApiStatus()) {
					startActivity(intent);
					VNowHostActivity.this.finish();
					VNowApplication.the().showToast(
							getString(R.string.str_login_success));
				}
			} else {
				mLoginFragment.loginError();
				VNowApplication.the().showToast(
						getString(R.string.str_login_field));
				mCore.checkUpVersion(VNowHostActivity.this, true);
			}
		}

		public void onResponseLogout(boolean bSuccess) {

		}

		public void onResponseApiStatus(String status) {
			// TODO Auto-generated method stub
			if (status.equals("0") || status.equals("2")) {
				actionLogin();
			} else if (status.equals("1")) {
				Intent intent = new Intent();
				intent.setClass(VNowHostActivity.this, VNowMainActivity.class);
				if (null != VNowApplication.the().getCore().getApiStatus()) {
					startActivity(intent);
					VNowHostActivity.this.finish();
					VNowApplication.the().showToast(
							getString(R.string.str_login_success));
				}
			} else if (status.equals("5") || status.equals("7")) {
				Intent intent = new Intent(VNowHostActivity.this,
						ConfActivity.class);
				if(mCore.getRctContact().size()>0){
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
