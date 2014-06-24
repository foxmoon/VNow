package com.nyist.vnow.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * 通讯录fragment适配器
 * @author harry
 * @version Creat on 2014-6-18下午4:13:55
 */
public class ContactFragmentAdapter extends FragmentPagerAdapter {
    private List<String> mTitleList;
    private List<Fragment> mlistFragment;

    public ContactFragmentAdapter(List<Fragment> list, FragmentManager fm) {
        super(fm);
        mlistFragment = list;
    }

    public void setTitleList(List<String> list) {
        mTitleList = list;
    }

    @Override
    public Fragment getItem(int pos) {
        if (mlistFragment == null) {
            return null;
        }
        return mlistFragment.get(pos);
    }

    @Override
    public int getCount() {
        if (null != mlistFragment) {
            return mlistFragment.size();
        }
        else {
            return 0;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mTitleList != null) {
            return mTitleList.get(position).toString();
        }
        return super.getPageTitle(position);
    }
}
