package com.nyist.vnow.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ContentFrameAdapter extends FragmentPagerAdapter {

	private List<String> mTitleList;
	
	private List<Fragment> mlistFragment;
	
	public ContentFrameAdapter(List<Fragment> list, FragmentManager fm) {
		super(fm);
		mlistFragment = list;
	}
	
	public void setTitleList(List<String> list){
		mTitleList = list;
	}

	@Override
	public Fragment getItem(int pos) {
		// TODO Auto-generated method stub
		if (mlistFragment == null ) {
			return null;
		}
		return mlistFragment.get(pos);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (null != mlistFragment) {
			return mlistFragment.size();
		} else {
			return 0;
		}		
	}


	@Override
	public CharSequence getPageTitle(int position) {
		if (mTitleList != null){
			return mTitleList.get(position).toString();
		}
		return super.getPageTitle(position);
	}

}
