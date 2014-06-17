package com.nyist.vnow.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.nyist.vnow.R;
import com.nyist.vnow.core.VNowApplication;
import com.nyist.vnow.utils.Constants;


/**
 * @author harry
 * @version Creat on 2014-6-17下午12:02:42
 */
public abstract class BaseActivity extends Activity {
	private boolean isStartActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (VNowApplication.mAppState == -1) {
			restartApplication();
		}else {
		    requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView();
			initializeData();
			initializeViews();
		}
	}
	
	protected void setContentView(int layoutResID,boolean isShowTitle){
		setContentView(layoutResID);
	}
	
	protected abstract void setContentView();
	protected abstract void initializeData();
	protected abstract void initializeViews();
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	protected void restartApplication() {
		finish();
		Intent intent = new Intent(getApplicationContext(), StartUpActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(Constants.RESTART_APP, true);
		startActivity(intent);
	}
	
	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		isStartActivity = true;
		overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
		isStartActivity = true;
		overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
	}

	@Override
	public void finish() {
		super.finish();
		if (!isStartActivity) {
			overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
		}
	}
	
}
