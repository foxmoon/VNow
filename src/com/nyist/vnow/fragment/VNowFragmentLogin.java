package com.nyist.vnow.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nyist.vnow.R;
import com.nyist.vnow.core.VNowApplication;
import com.nyist.vnow.core.VNowCore;
import com.nyist.vnow.struct.VNowRctContact;
import com.nyist.vnow.ui.ConfActivity;
import com.nyist.vnow.ui.VNowHostActivity;
import com.nyist.vnow.utils.ActionEvent;
import com.nyist.vnow.utils.DES;

public class VNowFragmentLogin extends Fragment implements OnClickListener {
    private ImageView mImgHead;
    private EditText mEdUserName;
    private EditText mEdPwd;
    private Button mBtnLogin;
    private TextView mBtnLoginRegist;
    private TextView mBtnforget;
    private TextView mBtnSetSvr;
    private VNowCore mCore;
    private LinearLayout mInfoLayout;
    private DES mDes;

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.vnow_login, container, false);
        mCore = VNowApplication.the().getCore();
        mDes = new DES();
        initUI(view);
        return view;
    }

    private void initUI(View view) {
        mImgHead = (ImageView) view.findViewById(R.id.login_user_head);
        mInfoLayout = (LinearLayout) view.findViewById(R.id.login_info_input);
        mEdUserName = (EditText) view.findViewById(R.id.login_user_name);
        mEdPwd = (EditText) view.findViewById(R.id.login_user_pwd);
        mBtnLogin = (Button) view.findViewById(R.id.login_btn);
        mBtnLoginRegist = (TextView) view.findViewById(R.id.login_regist);
        mBtnforget = (TextView) view.findViewById(R.id.login_forget_pwd);
        mBtnSetSvr = (TextView) view.findViewById(R.id.login_set_server);
        mBtnLogin.setOnClickListener(this);
        mBtnLoginRegist.setOnClickListener(this);
        mBtnforget.setOnClickListener(this);
        mBtnSetSvr.setOnClickListener(this);
        autoLogin();
    }

    public void autoLogin() {
        String strPhone = VNowApplication.the().getSetting(getString(R.string.setting_login_user_phone), null);
        String strpws = VNowApplication.the().getSetting(getString(R.string.setting_login_user_pwd), null);
        if (null != strPhone && null != strpws) {
            mEdUserName.setText(mDes.decrypt(strPhone));
            mEdPwd.setText(strpws);
            AlphaAnimation anim = new AlphaAnimation(1, 0);
            anim.setDuration(1000);
            mInfoLayout.setAnimation(anim);
            mInfoLayout.setVisibility(View.GONE);
            // mCore.doLogin(strPhone, strpws, true);
            String status = mCore.getApiStatus();
            if (null == status)
                return;
            if (status.equals("0") || status.equals("2")) {
                ((VNowHostActivity) getActivity()).actionLogin();
            }
            else if (Integer.valueOf(status) > 4) {
                Intent intent = new Intent(getActivity(),
                        ConfActivity.class);
                VNowRctContact rctItem = mCore.getRctContact().get(0);
                intent.putExtra("callNumber", rctItem.getmStrConPhone());
                intent.putExtra("callName", rctItem.getmStrContactName());
                intent.putExtra("isCallin", false);
                startActivity(intent);
            }
        }
        else if (null != strPhone) {
            mEdUserName.setText(mDes.decrypt(strPhone));
        }
    }

    private void UILogin() {
        DES des = new DES();
        String userName = mEdUserName.getText().toString().trim();
        String password = mEdPwd.getText().toString().trim();
        if (userName.length() == 0) {
            mEdUserName.setError(getString(R.string.login_name_error));
            return;
        }
        else if (password.length() == 0) {
            mEdPwd.setError(getString(R.string.login_password_error));
            return;
        }
        mCore.doLogin(userName, password, false);
    }

    public void loginError() {
        mInfoLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int id = v.getId();
        switch (id) {
            case R.id.login_btn: {
                UILogin();
            }
                break;
            case R.id.login_regist: {
                ((VNowHostActivity) getActivity()).actionToFragment(ActionEvent.ACTION_REGIST);
            }
                break;
            case R.id.login_forget_pwd: {
            }
                break;
            case R.id.login_set_server: {
                ((VNowHostActivity) getActivity()).actionToFragment(ActionEvent.ACTION_SETSVR);
            }
                break;
        }
    }
}
