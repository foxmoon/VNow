package com.nyist.vnow.dialog;

import com.nyist.vnow.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class VNowEditFriendDlg extends Dialog {
	
	private TextView mTxtTitle;
	private EditText mEditFriendName;
	private EditText mEditFriendPhone;
	
	private Button mBtnOk;
	private Button mBtnCancel;
	
	private OnClickListener mListenerOK;
	private OnClickListener mListenerCancel;

	public VNowEditFriendDlg(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public VNowEditFriendDlg(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}

	public VNowEditFriendDlg(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vnow_dlg_edit_friend);
		initUI();
	}
	private void initUI() {
		mTxtTitle = (TextView) findViewById(R.id.txt_dlg_titile);
		mEditFriendName = (EditText) findViewById(R.id.edit_dlg_friend_name);
		mEditFriendPhone = (EditText) findViewById(R.id.edit_dlg_friend_phone);
		mBtnOk = (Button) findViewById(R.id.btn_dlg_ok);
		mBtnCancel = (Button) findViewById(R.id.btn_dlg_cancel);
	}
	
	public void setTitle(String title) {
		mTxtTitle.setText(title);
	}
	
	public void setEditFriendName(String text) {
		mEditFriendName.setText(text);
	}
	
	public void setEditFriendPhone(String text) {
		mEditFriendPhone.setText(text);
	}
	
	public String getFriendName() {
		return mEditFriendName.getText().toString();
	}
	
	public String getFriendPhone() {
		return mEditFriendPhone.getText().toString();
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
					mListenerOK.onClick(VNowEditFriendDlg.this, 0);
				if (!"".equals(mEditFriendName.getText().toString())
						&& !"".equals(mEditFriendPhone.getText().toString())) {
					VNowEditFriendDlg.this.dismiss();
				}
			} else if (id == R.id.btn_dlg_cancel) {
				if (null != mListenerCancel)
					mListenerCancel.onClick(VNowEditFriendDlg.this, 1);
				VNowEditFriendDlg.this.dismiss();
			}		
		}
	};

	public interface OnClickListener {
		public void onClick(DialogInterface dialog, int i);
	}

}
