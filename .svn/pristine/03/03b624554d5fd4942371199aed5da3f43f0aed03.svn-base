package com.nyist.vnow.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.nyist.vnow.R;
import com.vnow.sdk.openapi.EventListener;

public abstract class BaseFragment extends Fragment {
    private boolean isReadyToFetchObjectData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
    }

    public abstract void fetchObjectData();

    public boolean isReadyToFetchObjectData() {
        return isReadyToFetchObjectData;
    }

    @Override
    public void onResume() {
        super.onResume();
        isReadyToFetchObjectData = true;
    }

}
