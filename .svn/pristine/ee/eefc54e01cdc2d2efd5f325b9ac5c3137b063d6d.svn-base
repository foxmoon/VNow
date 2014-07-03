package com.nyist.vnow.db;

import android.content.Context;
import android.os.AsyncTask;

import com.nyist.vnow.struct.User;
import com.nyist.vnow.utils.ToastUtil;

/**
 * @author harry
 * @version Creat on 2014-7-3下午12:42:58
 */
public class UserDbTask extends AsyncTask<User, Void, Boolean> {
    private Context mContext;

    public UserDbTask(Context context) {
        mContext = context;
    }

    @Override
    protected Boolean doInBackground(User... params) {
        User user = params[0];
        return UserDTOController.addOrUpdate(user);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            ToastUtil.getInstance(mContext).showShort("用户保存成功!");
        }
        else {
            ToastUtil.getInstance(mContext).showShort("用户保存失败!");
        }
    }
}
