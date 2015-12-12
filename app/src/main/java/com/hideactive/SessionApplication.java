package com.hideactive;

import com.hideactive.config.UserConfig;
import com.hideactive.model.User;

import android.app.Application;
import android.content.Context;

import cn.bmob.v3.BmobUser;

public class SessionApplication extends Application{

	private static Context context;
	private static SessionApplication application;
	private UserConfig userConfig;
	private User currentUser;
	
	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
		application = this;
	}
	
	public static SessionApplication getInstance() {
        return application;
    }

	/**
	 * 获取全局上下文
	 * @return
	 */
	public static Context getContext() {
		return context;
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
	public User getCurrentUser() {
		if (currentUser == null) {
            currentUser = BmobUser.getCurrentUser(context, User.class);
		}
		return currentUser;
	}

}
