package com.nyist.vnow.receiver;

import com.nyist.vnow.utils.Constants;
import com.nyist.vnow.utils.LogTag;
import com.nyist.vnow.utils.ThreadHelper;
import de.greenrobot.event.EventBus;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author harry
 * @version Creat on 2014-6-24上午9:09:41
 */
public class CheckNetWorkReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Constants.NET_CONNECTIVITY_CHANGE)) {
            LogTag.d("Sending event! ", ThreadHelper.getThreadInfo());
            EventBus.getDefault().post(new NetworkConnectionChanged());
        }
    }

    public static class NetworkConnectionChanged {
        // we could send the connection state in this object,
        // but the receiver will check the connection instead,
        // so the information he gets is as fresh as possible
    }
}
