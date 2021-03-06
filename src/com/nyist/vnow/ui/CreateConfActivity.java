package com.nyist.vnow.ui;

import java.util.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.nyist.vnow.R;
import com.nyist.vnow.core.VNowApplication;
import com.nyist.vnow.core.VNowCore;
import com.nyist.vnow.struct.LoginResult;
import com.nyist.vnow.utils.ActivityUtility;
import com.nyist.vnow.utils.Constants;
import com.nyist.vnow.utils.LogTag;
import com.nyist.vnow.utils.ToastUtil;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import com.stay.AppException;
import com.stay.net.Request;
import com.stay.net.Request.RequestMethod;
import com.stay.net.callback.JsonCallback;
import com.stay.net.callback.StringCallback;

public class CreateConfActivity extends BaseFragmentActivity implements OnClickListener, OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {
    public static final String TAG = CreateConfActivity.class.getName();
    private ImageButton mBtnBack;
    private Button mBtnCreate;
    private EditText mEdConfName;
    private EditText mEdConfTheme;
    private TextView mConfTime;
    private VNowCore mCore;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    public static final String DATEPICKER_TAG = "datepicker";
    public static final String TIMEPICKER_TAG = "timepicker";
    private StringBuffer confTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setContentView() {
        final View view = View.inflate(this, R.layout.fragment_crtconf, null);
        // view.setBackgroundColor(Color.WHITE);
        setContentView(view);
        AlphaAnimation alphaAnim = new AlphaAnimation(0.0f, 1.0f);
        alphaAnim.setDuration(300);
        view.setAnimation(alphaAnim);
    }

    @Override
    protected void initializeData() {
        mCore = VNowApplication.newInstance().getCore();
        final Calendar calendar = Calendar.getInstance();
        datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), true);
        timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), false, false);
    }

    @Override
    protected void initializeViews() {
        mBtnBack = (ImageButton) findViewById(R.id.btn_back);
        mBtnCreate = (Button) findViewById(R.id.conf_create_btn);
        mEdConfName = (EditText) findViewById(R.id.conf_name);
        mEdConfTheme = (EditText) findViewById(R.id.conf_theme);
        mConfTime = (TextView) findViewById(R.id.conf_time);
        mConfTime.setOnClickListener(this);
        mBtnBack.setOnClickListener(this);
        mBtnCreate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
        // case R.id.btn_back: {
        // CreateConfActivity.this.finish();
        // }
        // break;
            case R.id.conf_time:
                SelectTime();
                break;
            case R.id.conf_create_btn: {
                final String confName = mEdConfName.getText().toString().trim();
                final String confTheme = mEdConfTheme.getText().toString().trim();
                // String confTime = mConfTime.getText().toString().trim();
                final String confTime = "0";
                if (checkItemInput(confName, confTheme, confTime)) {
                    Intent intent = new Intent(CreateConfActivity.this,
                            ConfInfomationActivity.class);
                    intent.putExtra(Constants.CONFNAME, confName);
                    intent.putExtra(Constants.CONFTHEME, confTheme);
                    intent.putExtra(Constants.CONFTIME, confTime);
                    intent.putExtra(Constants.ENTERACTIVITY, Constants.CREATECONFACTIVITY);
                    startActivity(intent);
                    finish();
                    // String createConfUrl = mCore.getCreateConfUrl(confName,
                    // confTheme, confTime);
                    String modifyConfUrl = mCore.getModifyConfUrl(confTheme, confTime);
                    String deteleConfUrl = mCore.getDeteleConfUrl();
                    // LogTag.e("createConfUrl", createConfUrl);
                    LogTag.e("modifyConfUrl", modifyConfUrl);
                    LogTag.e("deteleConfUrl", deteleConfUrl);
                    Request request = new Request(modifyConfUrl, RequestMethod.GET);
                    request.setCallback(new JsonCallback<LoginResult>() {
                        @Override
                        public void onPreExecute() {
                        }

                        @Override
                        public void onSuccess(LoginResult result) {
                            if (result.getResult() == 1 || result.getResult() == 2) {
                                Intent intent = new Intent(CreateConfActivity.this,
                                        ConfInfomationActivity.class);
                                intent.putExtra(Constants.CONFNAME, confName);
                                intent.putExtra(Constants.CONFTHEME, confTheme);
                                intent.putExtra(Constants.CONFTIME, confTime);
                                intent.putExtra(Constants.ENTERACTIVITY, Constants.CREATECONFACTIVITY);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(AppException result) {}
                    }.setReturnClass(LoginResult.class));
                    // request.execute();
                }
            }
                break;
            case R.id.btn_back: {
                String confName = mEdConfName.getText().toString().trim();
                String confTheme = mEdConfTheme.getText().toString().trim();
                // String confTime = mConfTime.getText().toString().trim();
                String confTime = "0";
                if (checkItemInput(confName, confTheme, confTime)) {
                    // String createConfUrl = mCore.getCreateConfUrl(confName,
                    // confTheme, confTime);
                    String modifyConfUrl = mCore.getModifyConfUrl(confTheme, confTime);
                    String deteleConfUrl = mCore.getDeteleConfUrl();
                    // String modifyConfUrl = mCore.getModifyConfUrl( confTheme,
                    // confTime);
                    // VNConfItem item = new VNConfItem();
                    // item.setmConfName(confName);
                    // item.setmConfTheme(confTheme);
                    // item.setmConfTime(confTime);
                    // mCore.addConfItem(item);
                    // LogTag.e("createConfUrl", createConfUrl);
                    LogTag.e("modifyConfUrl", modifyConfUrl);
                    LogTag.e("deteleConfUrl", deteleConfUrl);
                    Request request = new Request(deteleConfUrl, RequestMethod.GET);
                    request.setCallback(new StringCallback() {
                        @Override
                        public void onSuccess(String result) {
                            LogTag.e("onSuccess", result);
                        }

                        @Override
                        public void onFailure(AppException result) {
                            LogTag.e("onFailure", result);
                        }

                        @Override
                        public void onPreExecute() {
                            
                        }
                    });
                    // request.execute();
                }
            }
                break;
        }
    }

    /**
     * 选择与会时间
     */
    private void SelectTime() {
        datePickerDialog.setVibrate(true);
        datePickerDialog.setYearRange(2014, 2015);
        datePickerDialog.setCloseOnSingleTapDay(false);
        datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
    }

    /**
     * 判断是否满足创建会议的条件
     * 
     * @return
     * 
     */
    private boolean checkItemInput(String confName, String confTheme, String confTime) {
        if (TextUtils.isEmpty(confName)) {
            ToastUtil.getInstance(CreateConfActivity.this).showShort("请输入会议名称");
        }
        else if (TextUtils.isEmpty(confTheme)) {
            ToastUtil.getInstance(CreateConfActivity.this).showShort("请输入会议主题");
        }
        else if (TextUtils.isEmpty(confTime)) {
            ToastUtil.getInstance(CreateConfActivity.this).showShort("请选择会议时间");
        }
        else {
            return true;
        }
        return false;
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        confTime.append(" " + hourOfDay + ":" + minute);
        mConfTime.setText(confTime);
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        confTime = new StringBuffer().append(year + "-" + month + "-" + day);
        timePickerDialog.setVibrate(true);
        timePickerDialog.setCloseOnSingleTapMinute(false);
        timePickerDialog.show(getSupportFragmentManager(), TIMEPICKER_TAG);
    }
}
