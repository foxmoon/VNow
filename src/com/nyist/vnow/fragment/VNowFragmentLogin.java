package com.nyist.vnow.fragment;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Program.TextureType;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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
import com.nyist.vnow.struct.LoginResult;
import com.nyist.vnow.struct.VNowRctContact;
import com.nyist.vnow.ui.ConfActivity;
import com.nyist.vnow.ui.VNowHostActivity;
import com.nyist.vnow.ui.VNowMainActivity;
import com.nyist.vnow.utils.ActionEvent;
import com.nyist.vnow.utils.CommonUtil;
import com.nyist.vnow.utils.DES;
import com.nyist.vnow.utils.LogTag;
import com.nyist.vnow.utils.Session;
import com.nyist.vnow.utils.ToastUtil;
import com.stay.AppException;
import com.stay.net.Request;
import com.stay.net.Request.RequestMethod;
import com.stay.net.callback.JsonCallback;
import com.stay.net.callback.StringCallback;

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
        mCore = VNowApplication.newInstance().getCore();
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
    }

    private void UILogin() {
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
        // mCore.doLogin(userName, password, false);
        ((VNowHostActivity) getActivity()).requestLogin(userName, password,false);
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
            case R.id.login_forget_pwd: {}
                break;
            case R.id.login_set_server: {
                ((VNowHostActivity) getActivity()).actionToFragment(ActionEvent.ACTION_SETSVR);
            }
                break;
        }
    }
}
