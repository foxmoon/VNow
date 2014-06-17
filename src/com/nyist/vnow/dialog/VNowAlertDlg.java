package com.nyist.vnow.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nyist.vnow.R;

public class VNowAlertDlg extends Dialog {
    private TextView mTxtTitle;
    private TextView mTxtContent;
    private Button mBtnOk;
    private Button mBtnCancel;
    private OnClickListener mListenerOK;
    private OnClickListener mListenerCancel;

    public VNowAlertDlg(Context context, boolean cancelable,
            OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        // TODO Auto-generated constructor stub
    }

    public VNowAlertDlg(Context context, int theme) {
        super(context, theme);
        // TODO Auto-generated constructor stub
        getWindow().setWindowAnimations(R.style.dialog_anim_style_v14);
    }

    public VNowAlertDlg(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vnow_dlg_alert);
        initUI();
    }

    private void initUI() {
        mTxtTitle = (TextView) findViewById(R.id.txt_dlg_titile);
        mTxtContent = (TextView) findViewById(R.id.txt_dlg_content);
        mBtnOk = (Button) findViewById(R.id.btn_dlg_ok);
        mBtnCancel = (Button) findViewById(R.id.btn_dlg_cancel);
    }

    public void setTitle(int res) {
        mTxtTitle.setText(res);
    }

    public void setContent(String text) {
        mTxtContent.setText(text);
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
            if (id == R.id.btn_dlg_ok) {
                if (null != mListenerOK)
                    mListenerOK.onClick(VNowAlertDlg.this, 0);
                VNowAlertDlg.this.dismiss();
            }
            else if (id == R.id.btn_dlg_cancel) {
                if (null != mListenerCancel)
                    mListenerCancel.onClick(VNowAlertDlg.this, 1);
                VNowAlertDlg.this.dismiss();
            }
        }
    };

    public interface OnClickListener {
        public void onClick(DialogInterface dialog, int i);
    }
}
