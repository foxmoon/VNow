package com.nyist.vnow.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.nyist.vnow.R;
import com.nyist.vnow.core.VNowApplication;
import com.nyist.vnow.core.VNowCore;
import com.nyist.vnow.ui.VNowHostActivity;
import com.nyist.vnow.utils.ActionEvent;
import com.nyist.vnow.utils.CommonUtil;

public class VNowFragmentSetSvr extends Fragment implements OnClickListener {
    private final int RESET_VNAPI_CORE = 0x01;
    private ImageButton mImgBtnBack;
    private VNowCore mCore;
    private Button mBtnSave;
    private EditText mEdServer;
    private String _lastServer;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if (msg.what == RESET_VNAPI_CORE) {
                mCore.initCore();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.vnow_set_server, container, false);
        initUI(view);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mCore = VNowApplication.the().getCore();
    }

    private void initUI(View view) {
        mBtnSave = (Button) view.findViewById(R.id.btn_save);
        mEdServer = (EditText) view.findViewById(R.id.edit_server);
        mImgBtnBack = (ImageButton) view.findViewById(R.id.btn_back);
        _lastServer = VNowApplication.the().getSetting(getString(R.string.setting_media_server_ip),
                getString(R.string.setting_defult_server_ip));
        mEdServer.setText(_lastServer);
        mBtnSave.setOnClickListener(this);
        mImgBtnBack.setOnClickListener(this);
    }

    private void saveServer() {
        String server = mEdServer.getText().toString().trim();
        if ("".equals(server) || !CommonUtil.isIpv4(server)) {
            VNowApplication.the().showToast(getString(R.string.str_set_error_server));
            return;
        }
        VNowApplication.the().setSetting(getString(R.string.setting_media_server_ip), server);
        CommonUtil.set_httpUrl(server);
        VNowApplication.the().showToast(getString(R.string.str_set_save_server));
        if (!_lastServer.equals(server)) {
            mCore.logoutCore();
            mHandler.sendMessageDelayed(mHandler.obtainMessage(RESET_VNAPI_CORE), 500);
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != getActivity().getCurrentFocus())
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        int id = v.getId();
        switch (id) {
            case R.id.btn_back: {
                ((VNowHostActivity) getActivity()).actionToFragment(ActionEvent.ACTION_LOGIN);
            }
                break;
            case R.id.btn_save: {
                saveServer();
            }
                break;
        }
    }
}
