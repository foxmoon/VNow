package com.nyist.vnow.ui;

import java.io.File;

import com.nyist.vnow.R;
import com.nyist.vnow.core.VNowApplication;
import com.nyist.vnow.utils.ImageUtil;
import com.nyist.vnow.utils.Session;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

/**
 * 开机启动页面
 * 
 * @author harry
 * 
 */
public class SplashActivity extends Activity {
    // 原图
    private Bitmap mSourceBitmap;
    // 处理后的图
    private Bitmap mTargetBitmap;
    private Session mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        final View view = View.inflate(this, R.layout.vnow_activity_startup, null);
        setContentView(view);
        AlphaAnimation alphaAnim = new AlphaAnimation(0.0f, 1.0f);
        alphaAnim.setDuration(2000);
        view.setAnimation(alphaAnim);
        VNowApplication.setAppState(0);
        mSession = Session.newInstance(getApplicationContext());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!mSession.isLogin()) {
                    startLoginActivity();
                }
                else {
                    startMainActivity();
                }
            }
        }, 2500);
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, VNowMainActivity.class);
        startActivity(intent);
        this.finish();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, VNowHostActivity.class);
        startActivity(intent);
        this.finish();
    }

    /*
     * 初始化Splash背景图
     */
    private void initSplashBg() {
        ImageView v = ((ImageView) findViewById(R.id.iv_splash));
        File splashFile = new File(getApplicationContext().getCacheDir(),
                "splash.png");
        if (splashFile.exists()) {
            mSourceBitmap = BitmapFactory.decodeFile(splashFile
                    .getAbsolutePath());
            if (ImageUtil.isAvailableBitmap(mSourceBitmap)) {
                v.setImageBitmap(mSourceBitmap);
                return;
            }
        }
        /*
         * // 没有新的Splash页，使用默认图 mSourceBitmap =
         * BitmapFactory.decodeResource(getResources(), R.drawable.splash_bg);
         * setSplashBitmap(mSourceBitmap);
         */
        v.setImageResource(R.drawable.ic_launcher);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // if (ImageUtil.isAvailableBitmap(mTargetBitmap)) {
        // mTargetBitmap.recycle();
        // }
        // if (ImageUtil.isAvailableBitmap(mSourceBitmap)) {
        // mSourceBitmap.recycle();
        // }
    }
}
