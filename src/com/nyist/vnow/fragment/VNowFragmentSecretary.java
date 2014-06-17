package com.nyist.vnow.fragment;

import com.nyist.vnow.R;
import com.nyist.vnow.core.VNowApplication;
import com.nyist.vnow.ui.VNowVideoSigninActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class VNowFragmentSecretary extends Fragment {
	private ImageView mBtnVdoSignIn;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.vnow_fragment_secretary,
				container, false);
		mBtnVdoSignIn = (ImageView) view.findViewById(R.id.btn_video_sign_in);
		mBtnVdoSignIn.setOnClickListener(mBtnOnClickListener);
		return view;
	}
	
	private View.OnClickListener mBtnOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();
			if (id == R.id.btn_video_sign_in) {
				if(null != VNowApplication.the().getBDLocation()){
					Intent intent = new Intent(getActivity(), VNowVideoSigninActivity.class);
					startActivity(intent);
				}else{
					VNowApplication.the().showToast("正在为您定位...请10秒后重试");
				}
			}
		}
	};
}
