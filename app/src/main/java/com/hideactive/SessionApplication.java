package com.hideactive;

import com.hideactive.config.UserConfig;

import android.app.Application;
import android.content.Context;

import cn.bmob.v3.BmobUser;

public class SessionApplication extends Application{

	private static SessionApplication application;
	private UserConfig userConfig;
	private BmobUser currentUser;
	
	@Override
	public void onCreate() {
		super.onCreate();
		application = this;
	}
	
	public static SessionApplication getInstance() {
        return application;
    }

	/**
	 * 获取用户设置信息
	 * @return
	 */
	public UserConfig getUserConfig() {
        if (userConfig == null) {
			userConfig = new UserConfig(this);
        }
        return userConfig;
    }

	/**
	 * 获取缓存用户信息
	 * @return
	 */
	public BmobUser getCurrentUser(Context context) {
		if (currentUser == null) {
            currentUser = BmobUser.getCurrentUser(context);
		}
		return currentUser;
	}

}
