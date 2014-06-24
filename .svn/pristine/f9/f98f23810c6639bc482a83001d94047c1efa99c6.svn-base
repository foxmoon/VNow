package com.nyist.vnow.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nyist.vnow.R;
import com.nyist.vnow.core.VNowApplication;
import com.nyist.vnow.utils.ToastUtil;

public class VNowAddContactActivity extends Activity {
    private ImageView mBtnBack;
    private EditText mEditSearch;
    private Button mBtnSearch;
    private RelativeLayout mRLayoutPhoneContact;
    private RelativeLayout mRLayoutQQContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vnow_add_contact);
        initUI();
    }

    private void initUI() {
        mBtnBack = (ImageView) findViewById(R.id.btn_back);
        mEditSearch = (EditText) findViewById(R.id.edit_add_contacts_search);
        mBtnSearch = (Button) findViewById(R.id.btn_add_search);
        mRLayoutPhoneContact = (RelativeLayout) findViewById(R.id.rlayout_add_phone_contact);
        mRLayoutQQContact = (RelativeLayout) findViewById(R.id.rlayout_add_qq_contact);
        mBtnBack.setOnClickListener(mBtnOnClickListener);
        mBtnSearch.setOnClickListener(mBtnOnClickListener);
        mRLayoutPhoneContact.setOnClickListener(mBtnOnClickListener);
        mRLayoutQQContact.setOnClickListener(mBtnOnClickListener);
    }

    private View.OnClickListener mBtnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int id = v.getId();
            if (id == R.id.btn_back) {
                finish();
            }
            else if (id == R.id.btn_add_search) {
                ToastUtil.getInstance(VNowAddContactActivity.this).showShort("search");
            }
            else if (id == R.id.rlayout_add_phone_contact) {
                ToastUtil.getInstance(VNowAddContactActivity.this).showShort("phone_contact");
                Intent intent = new Intent(VNowAddContactActivity.this, One2oneActivity.class);
                startActivity(intent);
            }
            else if (id == R.id.rlayout_add_qq_contact) {
                ToastUtil.getInstance(VNowAddContactActivity.this).showShort("qq_contact");
            }
        }
    };
}
