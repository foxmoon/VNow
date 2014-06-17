package com.nyist.vnow.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.nyist.vnow.R;
import com.nyist.vnow.adapter.VNowRctContactAdapter;
import com.nyist.vnow.core.VNowApplication;
import com.nyist.vnow.core.VNowCore;
import com.nyist.vnow.struct.Friend;
import com.nyist.vnow.struct.VNowRctContact;
import com.nyist.vnow.utils.DES;
import com.nyist.vnow.view.Dialpad;
import com.nyist.vnow.view.Dialpad.OnDialKeyListener;
import com.vnow.sdk.openapi.EventListener;

public class One2oneActivity extends FragmentActivity {
    private final int LOAD_RCT_CONTACT_FINISH = 0X301;
    private ImageButton mBtnBack;
    private ListView mListviewRctContact;
    private Dialpad mDialpad;
    private EditText mEditTextNum;
    private ImageView mBtnDel;
    private ImageView mBtnCall;
    private VNowRctContactAdapter mAdapterRctContact;
    private VNowCore mCore;
    private MyEventListener mCallBackListener;
    private List<VNowRctContact> mListRctContacts;
    private Handler mMainHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == LOAD_RCT_CONTACT_FINISH) {
                mListRctContacts = mCore.getmListRctContacts();
                if (null != mListRctContacts) {
                    mAdapterRctContact = new VNowRctContactAdapter(
                            One2oneActivity.this, mListRctContacts);
                    mListviewRctContact.setAdapter(mAdapterRctContact);
                }
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        final View view = View.inflate(this, R.layout.fragment_one2one, null);
        setContentView(view);
        mCore = VNowApplication.getInstance().getCore();
        mCallBackListener = new MyEventListener();
        loadSatrtUI();
        AlphaAnimation alphaAnim = new AlphaAnimation(0.0f, 1.0f);
        alphaAnim.setDuration(300);
        view.setAnimation(alphaAnim);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        initRctContactData();
        mCore.doSetEventListener(mCallBackListener);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mCore.doRemoveEventListener(mCallBackListener);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        // mTitleList = null;
        super.onDestroy();
    }

    private void loadSatrtUI() {
        mDialpad = (Dialpad) findViewById(R.id.dialpad);
        mEditTextNum = (EditText) findViewById(R.id.digit_view);
        mBtnDel = (ImageView) findViewById(R.id.btn_del_num);
        mBtnBack = (ImageButton) findViewById(R.id.btn_back);
        mListviewRctContact = (ListView) findViewById(R.id.listview_rct_data);
        mBtnCall = (ImageView) findViewById(R.id.btn_num_call);
        mDialpad.setOnDialKeyListener(mDialKeyListener);
        mBtnCall.setOnClickListener(mBtnOnClickListener);
        mBtnBack.setOnClickListener(mBtnOnClickListener);
        mBtnDel.setOnClickListener(mBtnOnClickListener);
        mListviewRctContact.setOnItemClickListener(mListItemClickListener);
        mListviewRctContact.setOnItemLongClickListener(mItemLongClickListener);
        mEditTextNum.addTextChangedListener(mTextWatcher);
    }

    private void keyPressed(int keyCode) {
        KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
        mEditTextNum.onKeyDown(keyCode, event);
    }

    /**
     * the method to init the contacts from phone
     */
    private void initRctContactData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                mCore.getRctContact();
                mMainHandler.sendEmptyMessage(LOAD_RCT_CONTACT_FINISH);
            }
        }).start();
    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     * 
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<VNowRctContact> filterDateList = new ArrayList<VNowRctContact>();
        if (TextUtils.isEmpty(filterStr)) {
            if (null == mListRctContacts)
                return;
            filterDateList.addAll(mListRctContacts);
        }
        else {
            filterDateList.clear();
            for (VNowRctContact contact : mListRctContacts) {
                String name = new DES().decrypt(contact.getmStrConPhone());
                if (name.indexOf(filterStr.toString()) != -1
                        || name.startsWith(filterStr.toString())) {
                    filterDateList.add(contact);
                }
            }
        }
        if (null != mAdapterRctContact) {
            mAdapterRctContact.updateListView(filterDateList, filterStr);
        }
    }

    /**
     * the method to show recent chat message dialog when you long clik recent
     * chat listview item
     * 
     * @param longClickPos
     */
    private void showItemLongClickDialog(final int longClickPos) {
        final VNowRctContact grp = mCore.getmListRctContacts()
                .get(longClickPos);
        String[] choices = new String[1];
        choices[0] = getString(R.string.str_delete_contact);
        new AlertDialog.Builder(this)
                .setTitle(grp.getmStrContactName())
                .setItems(choices, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: {
                                mCore.deleteCallHistory(grp.getmStrContactName());
                                mCore.getmListRctContacts().remove(longClickPos);
                                mAdapterRctContact.notifyDataSetChanged();
                            }
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
    }

    private AdapterView.OnItemClickListener mListItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapter, View view,
                int position, long id) {
            Intent intent = new Intent(One2oneActivity.this, ConfActivity.class);
            intent.putExtra("callNumber", mCore.getmListRctContacts().get(position).getmStrConPhone());
            intent.putExtra("callName", mCore.getmListRctContacts().get(position).getmStrContactName());
            intent.putExtra("isCallin", false);
            VNowRctContact rctItem = new VNowRctContact();
            rctItem.setmStrUserId(mCore.getMySelf().uuid);
            rctItem.setmStrUserName(mCore.getMySelf().name);
            rctItem.setmStrConPhone(mCore.getmListRctContacts()
                    .get(position).getmStrConPhone());
            rctItem.setmStrContactName(mCore.getmListRctContacts()
                    .get(position).getmStrContactName());
            rctItem.setmCallTime(System.currentTimeMillis());
            rctItem.setmIsCallIn(false);
            mCore.insertCallHistory(rctItem);
            startActivity(intent);
        }
    };
    private AdapterView.OnItemLongClickListener mItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapter, View view,
                int position, long id) {
            // TODO Auto-generated method stub
            showItemLongClickDialog(position);
            return false;
        }
    };
    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub
            filterData(s.toString());
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {
            // TODO Auto-generated method stub
        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
        }
    };
    private OnDialKeyListener mDialKeyListener = new OnDialKeyListener() {
        @Override
        public void onTrigger(int keyCode, int dialTone) {
            // TODO Auto-generated method stub
            keyPressed(keyCode);
        }
    };
    private View.OnClickListener mBtnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int id = v.getId();
            if (id == R.id.btn_back) {
                One2oneActivity.this.finish();
            }
            else if (id == R.id.btn_del_num) {
                keyPressed(KeyEvent.KEYCODE_DEL);
            }
            else if (id == R.id.btn_num_call) {
                String name = mEditTextNum.getText().toString().trim();
                String callPhone = new DES().encrypt(name);
                if (!"".equals(name)) {
                    Intent intent = new Intent(One2oneActivity.this,
                            ConfActivity.class);
                    intent.putExtra("callNumber", callPhone);
                    intent.putExtra("callName", mEditTextNum.getText()
                            .toString().trim());
                    intent.putExtra("isCallin", false);
                    VNowRctContact rctItem = new VNowRctContact();
                    rctItem.setmStrUserId(mCore.getMySelf().uuid);
                    rctItem.setmStrUserName(mCore.getMySelf().name);
                    rctItem.setmStrConPhone(callPhone);
                    rctItem.setmStrContactName(name);
                    rctItem.setmCallTime(System.currentTimeMillis());
                    rctItem.setmIsCallIn(false);
                    mCore.insertCallHistory(rctItem);
                    startActivity(intent);
                    mEditTextNum.setText("");
                }
            }
        }
    };

    // web 接口回调
    private class MyEventListener extends EventListener {
        @Override
        public void onResponseApiStatus(String status) {
            // TODO Auto-generated method stub
            super.onResponseApiStatus(status);
        }
    }
}
