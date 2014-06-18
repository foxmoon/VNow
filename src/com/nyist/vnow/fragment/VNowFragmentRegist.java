package com.nyist.vnow.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.nyist.vnow.R;
import com.nyist.vnow.core.VNowApplication;
import com.nyist.vnow.core.VNowCore;
import com.nyist.vnow.ui.VNowHostActivity;
import com.nyist.vnow.utils.ActionEvent;
import com.nyist.vnow.utils.SIMCardInfo;
import com.nyist.vnow.utils.ToastUtil;

public class VNowFragmentRegist extends Fragment implements OnClickListener {
    private ImageButton mImgBtnBack;
    private Button mBtnRegist;
    private EditText mEdUserName;
    private EditText mEdPassword;
    private EditText mEdRePassword;
    private EditText mEdPhone;
    private EditText mEdCmpEmail;
    private EditText mEdCmpCode;
    private VNowCore mCore;
    private SIMCardInfo mSimCardInfo;

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.vnow_regist, container, false);
        mCore = VNowApplication.newInstance().getCore();
        mSimCardInfo = new SIMCardInfo(getActivity());
        initUI(view);
        return view;
    }

    private void initUI(View view) {
        // TODO Auto-generated method stub
        mImgBtnBack = (ImageButton) view.findViewById(R.id.btn_back);
        mBtnRegist = (Button) view.findViewById(R.id.regist_button);
        mEdUserName = (EditText) view.findViewById(R.id.regist_username_ed);
        mEdPassword = (EditText) view.findViewById(R.id.regist_password_ed);
        mEdRePassword = (EditText) view.findViewById(R.id.regist_repassword_ed);
        mEdPhone = (EditText) view.findViewById(R.id.regist_phone_ed);
        mEdCmpEmail = (EditText) view.findViewById(R.id.regist_email_ed);
        mEdCmpCode = (EditText) view.findViewById(R.id.regist_code_ed);
        mEdPhone.setText(mSimCardInfo.getNativePhoneNumber());
        mImgBtnBack.setOnClickListener(this);
        mBtnRegist.setOnClickListener(this);
    }

    private void UIRegist() {
        String userName = mEdUserName.getText().toString().trim();
        String password = mEdPassword.getText().toString().trim();
        String rePassword = mEdRePassword.getText().toString().trim();
        String phone = mEdPhone.getText().toString().trim();
        String emial = mEdCmpEmail.getText().toString().trim();
        String code = mEdCmpCode.getText().toString().trim();
        if (userName.length() == 0 || password.length() == 0 || rePassword.length() == 0 || phone.length() == 0
                || emial.length() == 0) {
            ToastUtil.getInstance(getActivity()).showShort(getString(R.string.str_regist_error_info));
            return;
        }
        if (password.length() < 6 || !password.equals(rePassword)) {
            mEdPassword.setError(getString(R.string.str_regist_error_password));
            return;
        }
        if (code.length() == 0) {
            code = "000001";
        }
        mCore.doRegidt(userName, password, phone, emial, code);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int id = v.getId();
        switch (id) {
            case R.id.btn_back: {
                ((VNowHostActivity) getActivity()).actionToFragment(ActionEvent.ACTION_LOGIN);
            }
                break;
            case R.id.regist_button: {
                UIRegist();
            }
                break;
        }
    }
}
