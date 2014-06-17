package com.nyist.vnow.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.nyist.vnow.R;
import com.nyist.vnow.core.VNowApplication;
import com.nyist.vnow.core.VNowCore;
import com.nyist.vnow.struct.VNConfItem;

public class CreateConfActivity extends Activity implements OnClickListener {
    private ImageButton mBtnBack;
    private Button mBtnCreate;
    private EditText mEdConfName;
    private EditText mEdConfTheme;
    private EditText mEdConfTime;
    private VNowCore mCore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        final View view = View.inflate(this, R.layout.fragment_crtconf, null);
        // view.setBackgroundColor(Color.WHITE);
        setContentView(view);
        AlphaAnimation alphaAnim = new AlphaAnimation(0.0f, 1.0f);
        alphaAnim.setDuration(300);
        view.setAnimation(alphaAnim);
        mCore = VNowApplication.the().getCore();
        loadSatrtUI();
    }

    private void loadSatrtUI() {
        mBtnBack = (ImageButton) findViewById(R.id.btn_back);
        mBtnCreate = (Button) findViewById(R.id.conf_create_btn);
        mEdConfName = (EditText) findViewById(R.id.conf_name);
        mEdConfTheme = (EditText) findViewById(R.id.conf_theme);
        mEdConfTime = (EditText) findViewById(R.id.conf_time);
        mBtnBack.setOnClickListener(this);
        mBtnCreate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int id = v.getId();
        switch (id) {
            case R.id.btn_back: {
                CreateConfActivity.this.finish();
            }
                break;
            case R.id.conf_create_btn: {
                String confName = mEdConfName.getText().toString().trim();
                String confTheme = mEdConfTheme.getText().toString().trim();
                String confTime = mEdConfTime.getText().toString().trim();
                if (confName.length() == 0) {
                    VNowApplication.the().showToast("请输入会议名称");
                }
                else {
                    VNConfItem item = new VNConfItem();
                    item.setmConfName(confName);
                    item.setmConfTheme(confTheme);
                    item.setmConfTime(confTime);
                    mCore.addConfItem(item);
                    VNowApplication.the().showToast("会议创建成功！");
                    CreateConfActivity.this.finish();
                }
            }
                break;
        }
    }
}
