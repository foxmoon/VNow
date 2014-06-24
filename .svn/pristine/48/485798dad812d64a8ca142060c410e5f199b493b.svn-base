package com.nyist.vnow.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.nyist.vnow.R;

public class VNowGroupDlg extends Dialog {
    private EditText mEditGrpName;
    private EditText mEditGrpDesc;
    private Button mBtnOk;
    private Button mBtnCancel;
    private OnClickListener mListenerOK;
    private OnClickListener mListenerCancel;

    public VNowGroupDlg(Context context, boolean cancelable,
            OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        // TODO Auto-generated constructor stub
    }

    public VNowGroupDlg(Context context, int theme) {
        super(context, theme);
        // TODO Auto-generated constructor stub
    }

    public VNowGroupDlg(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vnow_dlg_group);
        initUI();
    }

    private void initUI() {
        mEditGrpName = (EditText) findViewById(R.id.edit_dlg_group_name);
        mEditGrpDesc = (EditText) findViewById(R.id.edit_dlg_group_desc);
        mBtnOk = (Button) findViewById(R.id.btn_dlg_group_ok);
        mBtnCancel = (Button) findViewById(R.id.btn_dlg_group_cancel);
    }

    public void setEditGrpName(String text) {
        mEditGrpName.setText(text);
    }

    public void setEditGrpDesc(String text) {
        mEditGrpDesc.setText(text);
    }

    public String getGrpName() {
        return mEditGrpName.getText().toString();
    }

    public String getGrpDesc() {
        return mEditGrpDesc.getText().toString();
    }

    public void setOKButton(String text, OnClickListener listener) {
        mBtnOk.setVisibility(View.VISIBLE);
        mBtnOk.setText(text);
        mListenerOK = listener;
        mBtnOk.setOnClickListener(mOnClickListener);
    }

    public void setCancelButton(String text, OnClickListener listener) {
        mBtnCancel.setVisibility(View.VISIBLE);
        mBtnCancel.setText(text);
        mListenerCancel = listener;
        mBtnCancel.setOnClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int id = v.getId();
            if (id == R.id.btn_dlg_group_ok) {
                if (null != mListenerOK)
                    mListenerOK.onClick(VNowGroupDlg.this, 0);
                if (!"".equals(mEditGrpName.getText().toString())) {
                    VNowGroupDlg.this.dismiss();
                }
            }
            else if (id == R.id.btn_dlg_group_cancel) {
                if (null != mListenerCancel)
                    mListenerCancel.onClick(VNowGroupDlg.this, 1);
                VNowGroupDlg.this.dismiss();
            }
        }
    };

    public interface OnClickListener {
        public void onClick(DialogInterface dialog, int i);
    }
}
