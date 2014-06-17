package com.nyist.vnow.ui;

import com.nyist.vnow.R;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.app.Activity;

public class BackConfActivity extends Activity implements OnClickListener{
	private ImageButton mBtnBack;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		final View view = View.inflate(this, R.layout.fragment_rollbackconf, null);
//		view.setBackgroundColor(Color.WHITE);
		setContentView(view);
		AlphaAnimation alphaAnim = new AlphaAnimation(0.0f, 1.0f);
		alphaAnim.setDuration(300);
		view.setAnimation(alphaAnim);
	    loadSatrtUI();
	}
	 
	
	
	private void loadSatrtUI(){
		mBtnBack = (ImageButton) findViewById(R.id.btn_back);
		mBtnBack.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.btn_back:{
			BackConfActivity.this.finish();
		}
		break;
		}
	}
}
