package com.nyist.vnow.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.nyist.vnow.R;
import com.nyist.vnow.core.VNowApplication;
import com.nyist.vnow.core.VNowCore;

public class SystemSetActivity extends Activity implements OnClickListener{
	private ImageButton mBtnBack;
	private Button mBtnLogout;
	
	private RadioGroup mRadioGp;
	private EditText mEdSoundRd;
	
	private VNowCore mCore;
	private int mValue;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		final View view = View.inflate(this, R.layout.sys_set_layout, null);
		setContentView(view);
		AlphaAnimation alphaAnim = new AlphaAnimation(0.0f, 1.0f);
		alphaAnim.setDuration(300);
		view.setAnimation(alphaAnim);
		mCore = VNowApplication.the().getCore();
	    loadSatrtUI();
	}
	 
	
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		String sound = mEdSoundRd.getText().toString();
		int soundValue = 240;
		if(sound.length()>0){
			int value = Integer.parseInt(sound);
			if(value<100||value>500){
				VNowApplication.the().setSetting(getString(R.string.setting_video_sound_red), mValue);
				soundValue = mValue;
			}else{
				VNowApplication.the().setSetting(getString(R.string.setting_video_sound_red), value);
				soundValue = value;
			}
		}
		mCore.doechoDelaySet(soundValue);
		super.onStop();
	}






	private void loadSatrtUI(){
		mBtnBack = (ImageButton) findViewById(R.id.btn_back);
		mBtnLogout = (Button) findViewById(R.id.btn_logout);
		mRadioGp = (RadioGroup) findViewById(R.id.rgroup_show_modes);
		mEdSoundRd = (EditText) findViewById(R.id.edit_sound_reduce);
		mBtnBack.setOnClickListener(this);
		mBtnLogout.setOnClickListener(this);
		
		String mode = VNowApplication.the().getSetting(getString(R.string.setting_video_show_params), "-30-320-240-300000-1");
		if(mode.equals("-30-320-240-300000-1")){
			mRadioGp.check(R.id.rbtn_mode1_show);
		}else if(mode.equals("-30-640-480-800000-1")){
			mRadioGp.check(R.id.rbtn_mode2_show);
		}else if(mode.equals("-30-1280-720-1500000-1")){
			mRadioGp.check(R.id.rbtn_mode3_show);
		}
		mValue = VNowApplication.the().getSetting(getString(R.string.setting_video_sound_red), 240);
		mEdSoundRd.setText(""+mValue);
		
		mEdSoundRd.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(s.length()>0){
					int value = Integer.parseInt(s.toString());
					if(value<100||value>500){
						mEdSoundRd.setError("请填写100-500之间的值!");
					}
				}
				
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		mRadioGp.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.rbtn_mode1_show:{
					VNowApplication.the().setSetting(getString(R.string.setting_video_show_params), "-30-320-240-300000-1");
				}
					break;
				case R.id.rbtn_mode2_show:{
					VNowApplication.the().setSetting(getString(R.string.setting_video_show_params), "-30-640-480-800000-1");
				}
					break;
				case R.id.rbtn_mode3_show:{
					VNowApplication.the().setSetting(getString(R.string.setting_video_show_params), "-30-1280-720-1500000-1");
				}
					break;
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.btn_back:{
			SystemSetActivity.this.finish();
		}
		break;
		case R.id.btn_logout: {
			mCore.exitCore();
			VNowApplication.the().setSetting(getString(R.string.setting_login_user_pwd), null);
			Intent intent = new Intent(this,
					VNowHostActivity.class);
			startActivity(intent);
			mCore.initCore();
			this.finish();
		}
			break;
		}
	}
}
