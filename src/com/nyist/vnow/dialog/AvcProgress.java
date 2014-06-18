package com.nyist.vnow.dialog;

import com.nyist.vnow.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

public class AvcProgress extends Dialog {
    private TextView mTxtContent;

    public AvcProgress(Context context) {
        super(context);
    }

    public AvcProgress(Context context, int theme) {
        super(context, theme);
    }

    public AvcProgress(Context context, boolean cancelable,
            OnCancelListener cancelListener) {
        super(context, true, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avc_dlg_progress);
        initUI();
    }

    private void initUI() {
        mTxtContent = (TextView) findViewById(R.id.txt_progress_msg);
    }

    public void startProgress(String msg) {
        mTxtContent.setText(msg);
    }

    public void stopProgress() {
        if (isShowing()) {
            this.dismiss();
        }
    }
}
