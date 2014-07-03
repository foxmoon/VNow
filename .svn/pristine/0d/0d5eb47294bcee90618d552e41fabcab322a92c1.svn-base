package com.nyist.vnow.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.nyist.vnow.R;
import com.nyist.vnow.fragment.BaseFragment;
import com.nyist.vnow.fragment.ChoseColleagueFragment;
import com.nyist.vnow.fragment.ChosePhoneContactFragment;
import com.nyist.vnow.struct.Colleage;
import com.nyist.vnow.struct.ModuleEntity;
import com.nyist.vnow.utils.ActivityUtility;
import com.nyist.vnow.utils.LogTag;
import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.UnderlinePageIndicatorEx;

import de.greenrobot.event.EventBus;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author harry
 * @version Creat on 2014-7-1上午11:09:51
 *          注意要在Manifest.xml文件中配置android:theme="@style/StyledIndicators"
 *          否则，TabPageIndicator中的文字不会居中，也不会改变颜色
 */
public class ChoseMemberActivity extends BaseFragmentActivity implements OnClickListener {
    private ViewPager mViewPager;
    private TabPageIndicator mTabPageIndicator;
    private UnderlinePageIndicatorEx mUnderlinePageIndicator;
    private ArrayList<ModuleEntity> mEntities = new ArrayList<ModuleEntity>();
    private HashMap<Integer, BaseFragment> mModuleFragments = new HashMap<Integer, BaseFragment>();
    private HashMap<Integer, Boolean> mModuleFragmentsState = new HashMap<Integer, Boolean>();
    private ModuleAdapter mModuleAdapter;
    private ArrayList<String> mTitleList = new ArrayList<String>();
    private HorizontalScrollView mScrollView;
    private LinearLayout mSelectedMemberLayout;
    private Button mConfirm;
    private ArrayList<String> mSelectedMembers = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    public void onEventMainThread(String path) {
        if (!mSelectedMembers.contains(path)) {
            mSelectedMembers.add(path);
        }
        else {
            removeOneData(mSelectedMembers, path);
        }
        if (mSelectedMembers.size() == 0)
        {
            mConfirm.setText("确定");
            mConfirm.setEnabled(false);
        }
        else
        {
            mConfirm.setText("确定" + "(" + mSelectedMembers.size() + ")");
            mConfirm.setEnabled(true);
        }
    }

    private void removeOneData(ArrayList<String> arrayList,
            String path)
    {
        for (int i = 0; i < arrayList.size(); i++)
        {
            if (arrayList.get(i).equals(path))
            {
                arrayList.remove(i);
                return;
            }
        }
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_chose_member);
    }

    @Override
    protected void initializeData() {
        initViewpagerTitleData();
    }

    /**
     * 初始化viewPager的title
     */
    private void initViewpagerTitleData() {
        String[] arrStrings = getResources().getStringArray(
                R.array.chose_members);
        for (int i = 0; i < arrStrings.length; i++) {
            mTitleList.add(arrStrings[i]);
        }
    }

    @Override
    protected void initializeViews() {
        initTopBar();
        initViewPager();
        initSelectedMemberBar();
    }

    /**
     * 初始化title
     */
    private void initTopBar() {
        TextView mTopBarText = (TextView) findViewById(R.id.mTopBarText);
        mTopBarText.setText("选择与会人员");
        ImageView mTopBarBack = (ImageView) findViewById(R.id.mTopBarBack);
        mTopBarBack.setOnClickListener(this);
    }

    /**
     * 
     */
    private void initSelectedMemberBar() {
        mScrollView = (HorizontalScrollView) findViewById(R.id.sv_member_selected);
        mSelectedMemberLayout = (LinearLayout) findViewById(R.id.mSelectedMember);
        mConfirm = (Button) findViewById(R.id.btn_sure);
        mConfirm.setOnClickListener(this);
    }

    /**
     * 初始化fragmentviewpager
     */
    private void initViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.pager);
        for (int i = 0; i < 2; i++) {
            ModuleEntity moduleEntity = new ModuleEntity();
            moduleEntity.setGroupId(i);
            mEntities.add(moduleEntity);
        }
        mModuleAdapter = new ModuleAdapter(getSupportFragmentManager(),
                mEntities);
        mModuleAdapter.setTitleList(mTitleList);
        mViewPager.setAdapter(mModuleAdapter);
        mTabPageIndicator = (TabPageIndicator) findViewById(R.id.m_indicator);
        mUnderlinePageIndicator = (UnderlinePageIndicatorEx) findViewById(R.id.m_underline_indicator);
        mTabPageIndicator.setViewPager(mViewPager);
        mUnderlinePageIndicator.setViewPager(mViewPager);
        mUnderlinePageIndicator.setFades(false);
        mTabPageIndicator.setOnPageChangeListener(mUnderlinePageIndicator);
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
            if (i == 0) {
                fragment = ChoseColleagueFragment.newInstance(moduleEntity);
            }
            else {
                fragment = ChosePhoneContactFragment.newInstance(moduleEntity);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mTopBarBack:
                finish();
                break;
            case R.id.btn_sure:
                EventBus.getDefault().post(mSelectedMembers);
//                ActivityUtility.switchTo(ChoseMemberActivity.this, ConfInfomationActivity.class);
//                overridePendingTransition(R.anim.push_no_y, R.anim.push_down_out);
                finish();
                break;
            default:
                break;
        }
    }

//    @Override
//    public void onBackPressed() {
//        ActivityUtility.switchTo(ChoseMemberActivity.this, ConfInfomationActivity.class);
//        overridePendingTransition(R.anim.push_no_y, R.anim.push_down_out);
//    }

    public HorizontalScrollView getmScrollView() {
        return mScrollView;
    }

    public void setmScrollView(HorizontalScrollView mScrollView) {
        this.mScrollView = mScrollView;
    }

    public LinearLayout getmSelectedMemberLayout() {
        return mSelectedMemberLayout;
    }

    public void setmSelectedMemberLayout(LinearLayout mSelectedMemberLayout) {
        this.mSelectedMemberLayout = mSelectedMemberLayout;
    }
}
