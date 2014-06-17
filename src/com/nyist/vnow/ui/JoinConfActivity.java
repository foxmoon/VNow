package com.nyist.vnow.ui;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

import com.nyist.vnow.R;
import com.nyist.vnow.adapter.VNConfListAdapter;
import com.nyist.vnow.core.VNowApplication;
import com.nyist.vnow.core.VNowCore;
import com.nyist.vnow.struct.VNConfItem;

public class JoinConfActivity extends Activity implements OnClickListener{
	private ImageButton mBtnBack;
	private ListView mLvConf;
	private VNConfListAdapter mAdapter;
	private List<VNConfItem> mListConfs;
	private VNowCore mCore;
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		final View view = View.inflate(this, R.layout.fragment_joinconf, null);
//		view.setBackgroundColor(Color.WHITE);
		setContentView(view);
		AlphaAnimation alphaAnim = new AlphaAnimation(0.0f, 1.0f);
		alphaAnim.setDuration(300);
		view.setAnimation(alphaAnim);
		mCore = VNowApplication.the().getCore();
	    loadSatrtUI();
	}
	 
	
	
	private void loadSatrtUI(){
		mBtnBack = (ImageButton) findViewById(R.id.btn_back);
		mLvConf = (ListView) findViewById(R.id.custom_list);
		mListConfs = mCore.getConfItems();
		if(null == mListConfs){
			return;
		}
		mAdapter = new VNConfListAdapter(this, mListConfs);
		mLvConf.setAdapter(mAdapter);
		mLvConf.setOnItemClickListener(mItemClickListener);
		mBtnBack.setOnClickListener(this);
	}
	
	private OnItemClickListener mItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
//			mCore.setmCurrentConfInfo(mListConfs.get(arg2));
//			Intent openCameraIntent = new Intent(JoinConfActivity.this,
//					ConfActivity.class);
//			startActivity(openCameraIntent);
		}
	};
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.btn_back:{
			JoinConfActivity.this.finish();
		}
		break;
		}
	}
}
