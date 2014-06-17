package com.nyist.vnow.receiver;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.nyist.vnow.core.VNowApplication;
import com.nyist.vnow.struct.VNowRctContact;
import com.nyist.vnow.ui.ConfActivity;
import com.nyist.vnow.utils.DES;
import com.nyist.vnow.utils.ToastUtil;

public class EventReceiver extends BroadcastReceiver {
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Log.d("EventReceiver", "onReceive");
        mContext = context;
        String strEvent = intent.getAction();
        if (strEvent != null && "com.vnow.brdcast.callin".equals(strEvent)) {
            String strUserID = intent.getStringExtra("userid");
            System.out.println("com.vnow.brdcast.callin" + strUserID);
            if (strUserID != null) {
                Intent callIntent = new Intent(context, ConfActivity.class);
                callIntent.putExtra("isCallin", true);
                callIntent.putExtra("callNumber", (new DES()).encrypt(strUserID));
                callIntent.putExtra("callName", strUserID);
                callIntent.addFlags(callIntent.FLAG_ACTIVITY_NEW_TASK);
                VNowRctContact rctItem = new VNowRctContact();
                rctItem.setmStrUserId(VNowApplication.getInstance().getCore()
                        .getMySelf().uuid);
                rctItem.setmStrUserName(VNowApplication.getInstance().getCore()
                        .getMySelf().name);
                rctItem.setmStrConPhone((new DES()).encrypt(strUserID));
                rctItem.setmStrContactName(strUserID);
                rctItem.setmCallTime(System.currentTimeMillis());
                rctItem.setmIsCallIn(true);
                VNowApplication.getInstance().getCore().insertCallHistory(rctItem);
                context.startActivity(callIntent);
                ToastUtil.getInstance(mContext).showShort(strUserID + "呼入...");
            }
        }
    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.example.MyService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}