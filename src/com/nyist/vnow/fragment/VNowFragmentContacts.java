package com.nyist.vnow.fragment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nyist.vnow.R;
import com.nyist.vnow.adapter.ContactFragmentAdapter;
import com.nyist.vnow.struct.ModuleEntity;
import com.nyist.vnow.ui.ChoseMemberActivity.ModuleAdapter;
import com.nyist.vnow.utils.LogTag;
import com.stay.utilities.TextUtil;
import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.UnderlinePageIndicatorEx;

public class VNowFragmentContacts extends Fragment {
    ViewPager mViewPager;
    TabPageIndicator mTabPageIndicator;
    UnderlinePageIndicatorEx mUnderlinePageIndicator;
    private ContactFragmentAdapter mContentAdapter;
    private List<String> mTitleList;
    private List<Fragment> mListFragment;
    private View mFragContacts;
    private ArrayList<ModuleEntity> mEntities;
    private HashMap<Integer, BaseFragment> mModuleFragments = new HashMap<Integer, BaseFragment>();
    private HashMap<Integer, Boolean> mModuleFragmentsState = new HashMap<Integer, Boolean>();
    private ModuleAdapter mModuleAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mFragContacts = inflater.inflate(R.layout.vnow_fragment_contacts, container, false);
        mViewPager = (ViewPager) mFragContacts.findViewById(R.id.pager);
        mTabPageIndicator = (TabPageIndicator) mFragContacts.findViewById(R.id.indicator);
        mUnderlinePageIndicator = (UnderlinePageIndicatorEx) mFragContacts.findViewById(R.id.underline_indicator);
        return mFragContacts;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initChildFragments();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        /*
         * Fragment在detached之后都会被reset掉，但是它并没有对ChildFragmentManager做reset，
         * 所以会造成ChildFragmentManager的状态错误。
         * 解决方案在Fragment被detached的时候要去重置ChildFragmentManager
         */
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

    public void setUIVisibility(int visiable) {
        mFragContacts.setVisibility(visiable);
    }

    void initChildFragments() {
        if (!TextUtil.isValidate(mEntities)) {
            initViewpagerTitleData();
            mEntities = new ArrayList<ModuleEntity>();
            for (int i = 0; i < 4; i++) {
                ModuleEntity moduleEntity = new ModuleEntity();
                moduleEntity.setGroupId(i);
                mEntities.add(moduleEntity);
            }
        }
        mModuleAdapter = new ModuleAdapter(getChildFragmentManager(),
                mEntities);
        mModuleAdapter.setTitleList(mTitleList);
        mViewPager.setAdapter(mModuleAdapter);
        mTabPageIndicator.setViewPager(mViewPager);
        mUnderlinePageIndicator.setViewPager(mViewPager);
        mUnderlinePageIndicator.setFades(false);
        mTabPageIndicator.setOnPageChangeListener(mUnderlinePageIndicator);
    }

    /**
     * 
     */
    private void initViewpagerTitleData() {
        mTitleList = new ArrayList<String>();
        if (mTitleList == null) {}
        String[] arrStrings = getResources().getStringArray(
                R.array.vnow_contacts);
        for (int i = 0; i < arrStrings.length; i++) {
            mTitleList.add(arrStrings[i]);
        }
    }

    public class ModuleAdapter extends FragmentStatePagerAdapter {
        private ArrayList<ModuleEntity> moduleEntities;
        private List<String> mTitleList;

        public ModuleAdapter(FragmentManager fm, ArrayList<ModuleEntity> moduleEntities) {
            super(fm);
            this.moduleEntities = moduleEntities;
        }

        @Override
        public Fragment getItem(int i) {
            BaseFragment fragment = null;
            ModuleEntity moduleEntity = moduleEntities.get(i);
            moduleEntity.setGroupId(i);
            switch (i) {
                case 0:
                    fragment = VNowFragmentPhoneContacts.newInstance(moduleEntity);
                    break;
                case 1:
                    fragment = VNowFragmentColleague.newInstance(moduleEntity);
                    break;
                case 2:
                    fragment = VNowFragmentGroup.newInstance(moduleEntity);
                    break;
                case 3:
                    fragment = VnowFragmentContactOther.newInstance(moduleEntity);
                    break;
                default:
                    break;
            }
            mModuleFragments.put(i, fragment);
            mModuleFragmentsState.put(i, false);
            return fragment;
        }

        @Override
        public int getCount() {
            return moduleEntities != null ? moduleEntities.size() : 0;
        }

        public void setTitleList(List<String> list) {
            mTitleList = list;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (mTitleList != null) {
                return mTitleList.get(position).toString();
            }
            return super.getPageTitle(position);
        }

        /**
         * 解决FragmentStatePagerAdapter with ChildFragmentManager -
         * FragmentManagerImpl.getFragment results in NullPointerException
         */
        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
            // super.restoreState(arg0, arg1);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            mModuleFragments.remove(position);
            mModuleFragmentsState.remove(position);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position,
                Object object) {
            super.setPrimaryItem(container, position, object);
            if (mModuleFragmentsState.containsKey(position)
                    && !mModuleFragmentsState.get(position)) {
                BaseFragment fragment = mModuleFragments.get(position);
                if (fragment.isReadyToFetchObjectData()) {
                    mModuleFragmentsState.put(position, true);
                    fragment.fetchObjectData();
                }
            }
        }
    }
}
