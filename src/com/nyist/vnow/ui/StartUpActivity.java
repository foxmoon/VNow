package com.nyist.vnow.ui;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.nyist.vnow.R;
import com.nyist.vnow.core.VNowApplication;
import com.nyist.vnow.view.WaverTextView;

public class StartUpActivity extends Activity{
	private final int EDIT_TYPE_SELECTED = 1;
	private final int EDIT_TYPE_NO_SELECTED = 2;
	private RelativeLayout mLayoutApp;
	private AsyncTask<Void, Void, Void> mInitTask;
	private IndexThread mLoadThread;
	private int mItemCount = 6;
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case EDIT_TYPE_SELECTED:
				break;
			case EDIT_TYPE_NO_SELECTED:
//				mImgLoadBall.setImageResource(msg.arg1);
				break;
			}
			super.handleMessage(msg);
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		final View view = View.inflate(this, R.layout.vnow_activity_startup, null);
		setContentView(view);
		Intent i = new Intent(); 
//		i.setClass(this, LocationService.class); 
		startService(i); 

		AlphaAnimation alphaAnim = new AlphaAnimation(0.0f, 1.0f);
		alphaAnim.setDuration(2000);
		view.setAnimation(alphaAnim);
	        	loadSatrtUI();
		 checkNet();
	}
	 
	private void checkNet() {
		// TODO Auto-generated method stub
	}
	
	private void loadSatrtUI(){
//		mImgLoadBall = (ImageView) findViewById(R.id.loading_widget_ball);
		mLoadThread = new IndexThread();
        mLoadThread.start();
		mInitTask=new AsyncTask<Void, Void, Void>(){
			@Override
			protected Void doInBackground(Void... params) {
				try {
					Thread.sleep(2500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
//				SDHelper.getPath(Const.PIC_CACH_PATH);
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				if(!VNowApplication.the().getSetting("islogin",false)){
					Intent intent=new Intent(StartUpActivity.this,VNowHostActivity.class);
					StartUpActivity.this.startActivity(intent);
//					overridePendingTransition(R.anim.translucent_enter, R.anim.translucent_exit);
					StartUpActivity.this.finish();
				}else{
					Intent intent=new Intent(StartUpActivity.this,VNowMainActivity.class);
					StartUpActivity.this.startActivity(intent);
//					overridePendingTransition(R.anim.translucent_enter, R.anim.translucent_exit);
					StartUpActivity.this.finish();
				}
			}
			
		};
		mInitTask.execute();
	}
	
	 class IndexThread extends Thread
	    {
	    	boolean flag = true;
	    	@Override
		     public void run()
		     {
	    		Message msg;
	    		while(flag)
	    		{
	    			for(int i= 0 ; i < mItemCount ; i++)
	    			{
	    				SystemClock.sleep(200);
	    			}
	    		}
	    		
		     }
	    }

}
