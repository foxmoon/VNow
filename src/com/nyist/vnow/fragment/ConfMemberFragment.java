package com.nyist.vnow.fragment;

import com.nyist.vnow.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

/**
 * @author harry
 * @version Creat on 2014-6-23上午9:28:58
 */
public class ConfMemberFragment extends BaseFragment {
    private EditText mSearchEdit;
    private ListView mConfMemList;
    private View mConfMemView;

    public ConfMemberFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mConfMemView = inflater.inflate(R.layout.fragment_confmember, null);
        mSearchEdit = (EditText) mConfMemView.findViewById(R.id.edit_text_contacts_search);
        mConfMemList = (ListView) mConfMemView.findViewById(R.id.mConfMemList);
        return mConfMemView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void fetchObjectData() {
    }
    
    public void setUIVisibility(int visiable) {
        mConfMemView.setVisibility(visiable);
    }
}
