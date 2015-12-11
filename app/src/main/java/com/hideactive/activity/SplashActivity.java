package com.hideactive.activity;

import com.hideactive.R;
import com.hideactive.config.Constant;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;

public class SplashActivity extends BaseActivity {
	
	private static final int GO_HOME = 100;
	private static final int GO_LOGIN = 200;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_HOME:
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                    break;
                case GO_LOGIN:
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                    break;
            }
        }
    };
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getActionBar().hide();
		// 初始化Bmob
		Bmob.initialize(this, Constant.BMOB_APP_ID);
		// 自动登录
		BmobUser user = application.getCurrentUser(this);
		if (user != null) {
			mHandler.sendEmptyMessageDelayed(GO_HOME, 1000);
		} else {
			mHandler.sendEmptyMessageDelayed(GO_LOGIN, 1000);
		}
    }
}
