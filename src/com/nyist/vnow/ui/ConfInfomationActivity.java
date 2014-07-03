package com.nyist.vnow.ui;

import java.util.ArrayList;

import com.nyist.vnow.R;
import com.nyist.vnow.adapter.ConfMemberGridAdapter;
import com.nyist.vnow.receiver.CheckNetWorkReceiver;
import com.nyist.vnow.struct.Colleage;
import com.nyist.vnow.utils.ActivityUtility;
import com.nyist.vnow.utils.Constants;
import com.nyist.vnow.utils.LogTag;
import com.nyist.vnow.utils.NetUtil;
import com.nyist.vnow.utils.ThreadHelper;
import com.nyist.vnow.view.CustomGridView;

import de.greenrobot.event.EventBus;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ConfInfomationActivity extends BaseActivity implements OnItemClickListener, OnClickListener {
    private CustomGridView mConfMember;
    private ArrayList<String> memberUrls;
    private ConfMemberGridAdapter confMemberGridAdapter;
    private String confName;
    private String confTheme;
    private String confTime;
    private TextView mTopBarText;
    private TextView mConfThemeLabel;
    private TextView mConfThemeDetailLabel;
    private TextView mConfTimeLabel;
    private TextView mConfTimeDetailLabel;
    private Button mCreatConfButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_conf_member);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    // will be called on the main thread
    public void onEventMainThread(ArrayList<String> selectedMembers) {
        for (int i = 0; i < selectedMembers.size(); i++) {
            LogTag.e("onEventMainThread", selectedMembers.get(i));
        }
        LogTag.d("in onEventMainThread! ", ThreadHelper.getThreadInfo());
        memberUrls.add("http://img1.6544.cc/mn123/201201/1109067906-lp.jpg");
        confMemberGridAdapter.notifyDataSetChanged();
    }

    @Override
    protected void initializeData() {
        memberUrls = new ArrayList<String>();
        Intent intent = getIntent();
        String enterActivity = intent.getStringExtra(Constants.ENTERACTIVITY);
        if (!TextUtils.isEmpty(enterActivity)) {
            if (enterActivity.equals(Constants.CREATECONFACTIVITY)) {
                confName = intent.getStringExtra(Constants.CONFNAME);
                confTheme = intent.getStringExtra(Constants.CONFTHEME);
                confTime = intent.getStringExtra(Constants.CONFTIME);
            }
            else {}
        }
    }

    @Override
    protected void initializeViews() {
        mTopBarText = (TextView) findViewById(R.id.mTopBarText);
        mTopBarText.setText(confName);
        // mConfThemeLabel = (TextView) findViewById(R.id.mConfThemeLabel);
        mConfThemeDetailLabel = (TextView) findViewById(R.id.mConfThemeDetailLabel);
        // mConfTimeLabel = (TextView) findViewById(R.id.mConfTimeLabel);
        mConfTimeDetailLabel = (TextView) findViewById(R.id.mConfTimeDetailLabel);
        if (!TextUtils.isEmpty(confName)) {
            mTopBarText.setText(confName);
        }
        if (!TextUtils.isEmpty(confTheme)) {
            mConfThemeDetailLabel.setText(confTheme);
        }
        if (!TextUtils.isEmpty(confTime)) {
            mConfTimeDetailLabel.setText(confTime);
        }
        mCreatConfButton = (Button) findViewById(R.id.mCreatConfButton);
        mCreatConfButton.setOnClickListener(this);
        mConfMember = (CustomGridView) findViewById(R.id.mGridConfMember);
        confMemberGridAdapter = new ConfMemberGridAdapter(this, memberUrls);
        mConfMember.setAdapter(confMemberGridAdapter);
        memberUrls.add("http://img1.6544.cc/mn123/201201/1109067906-lp.jpg");
        memberUrls.add("http://img1.6544.cc/mn123/201201/1109067906-lp.jpg");
        memberUrls.add("http://img1.6544.cc/mn123/201201/1109067906-lp.jpg");
        memberUrls.add("http://img1.6544.cc/mn123/201201/1109067906-lp.jpg");
        memberUrls.add("http://img1.6544.cc/mn123/201201/1109067906-lp.jpg");
        confMemberGridAdapter.notifyDataSetChanged();
        mConfMember.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
        if (position >= memberUrls.size()) {
            ActivityUtility.switchTo(ConfInfomationActivity.this, ChoseMemberActivity.class);
            overridePendingTransition(R.anim.push_up_in, R.anim.push_no_y);
            LogTag.e("ChoseMember", "ChoseMemberActivity");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mCreatConfButton:
                creatOrModifyConf();
                break;
            default:
                break;
        }
    }

    /**
     * 创建或修改会议
     */
    private void creatOrModifyConf() {}

    @Override
    public void finish() {
//        Intent intent = new Intent(ConfInfomationActivity.this,VNowMainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
        super.finish();
    }
    
    
}
