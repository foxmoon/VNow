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
import android.widget.TextView;

import com.nyist.vnow.R;
import com.nyist.vnow.core.VNowApplication;
import com.nyist.vnow.core.VNowCore;
import com.nyist.vnow.utils.DeviceUtil;

public class VNMoreActivity extends Activity implements OnClickListener {
    private ImageButton mBtnBack;
    private Button mBtnCheck;
    private TextView mTvVersion;
    private VNowCore mCore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        final View view = View.inflate(this, R.layout.more_version_layout, null);
        setContentView(view);
        AlphaAnimation alphaAnim = new AlphaAnimation(0.0f, 1.0f);
        alphaAnim.setDuration(300);
        view.setAnimation(alphaAnim);
        mCore = VNowApplication.newInstance().getCore();
        loadSatrtUI();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    private void loadSatrtUI() {
        mBtnBack = (ImageButton) findViewById(R.id.btn_back);
        mBtnCheck = (Button) findViewById(R.id.btn_check);
        mTvVersion = (TextView) findViewById(R.id.version_info);
        mBtnBack.setOnClickListener(this);
        mBtnCheck.setOnClickListener(this);
        mTvVersion.setText(DeviceUtil.getVersion(this));
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int id = v.getId();
        switch (id) {
            case R.id.btn_back: {
                VNMoreActivity.this.finish();
            }
                break;
            case R.id.btn_check: {
                mCore.checkUpVersion(VNMoreActivity.this, false);
            }
                break;
        }
    }
}
