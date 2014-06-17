package com.nyist.vnow.fragment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nyist.vnow.R;
import com.nyist.vnow.adapter.ContentFrameAdapter;
import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.UnderlinePageIndicatorEx;

public class VNowFragmentContacts extends Fragment {
    ViewPager mViewPager;
    TabPageIndicator mTabPageIndicator;
    UnderlinePageIndicatorEx mUnderlinePageIndicator;
    private ContentFrameAdapter mContentAdapter;
    private List<String> mTitleList;
    private List<Fragment> mListFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.vnow_fragment_contacts, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        // mViewPager.setAdapter(mContentAdapter);
        mTabPageIndicator = (TabPageIndicator) view.findViewById(R.id.indicator);
        // mTabPageIndicator.setViewPager(mViewPager);
        mUnderlinePageIndicator = (UnderlinePageIndicatorEx) view.findViewById(R.id.underline_indicator);
        // mUnderlinePageIndicator.setViewPager(mViewPager);
        // mUnderlinePageIndicator.setFades(false);
        //
        // mTabPageIndicator.setOnPageChangeListener(mUnderlinePageIndicator);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        doInitAllData();
        mViewPager.setAdapter(mContentAdapter);
        mTabPageIndicator.setViewPager(mViewPager);
        mUnderlinePageIndicator.setViewPager(mViewPager);
        mUnderlinePageIndicator.setFades(false);
        mTabPageIndicator.setOnPageChangeListener(mUnderlinePageIndicator);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class
                    .getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        }
        catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    void doInitAllData() {
        mTitleList = new ArrayList<String>();
        String[] arrStrings = getResources().getStringArray(
                R.array.vnow_contacts);
        for (int i = 0; i < arrStrings.length; i++) {
            mTitleList.add(arrStrings[i]);
        }
        mListFragment = new ArrayList<Fragment>();
        mListFragment.add(new VNowFragmentPhoneContacts());
        mListFragment.add(new VNowFragmentColleague());
        mListFragment.add(new VNowFragmentGroup());
        mListFragment.add(new VnowFragmentContactOther());
        mContentAdapter = new ContentFrameAdapter(mListFragment,
                getChildFragmentManager());
        mContentAdapter.setTitleList(mTitleList);
    }
}
