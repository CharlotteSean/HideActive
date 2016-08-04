package com.hideactive;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.hideactive.activity.LoginActivity;
import com.hideactive.config.Constant;
import com.hideactive.config.UserConfig;
import com.hideactive.db.LikesDB;
import com.hideactive.model.User;
import com.hideactive.util.PushUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobUser;

public class SessionApplication extends Application{

	private static Context context;
	private static SessionApplication application;
	private static UserConfig userConfig;
	private static List<Activity> activities = new ArrayList<Activity>();
	private static LikesDB likesDB;

	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
		application = this;

		// Fresco初始化
		ImagePipelineConfig config = ImagePipelineConfig.newBuilder(application)
				.setDownsampleEnabled(true) // 支持多种图片格式
				.build();
		Fresco.initialize(application, config);

		// 初始化Bmob
		Bmob.initialize(this, Constant.BMOB_APP_ID);
		// 使用推送服务时的初始化操作
		BmobInstallation.getCurrentInstallation(this).save();
		// 启动推送服务
		BmobPush.startWork(this, Constant.BMOB_APP_ID);
	}
	
	public static SessionApplication getInstance() {
        return application;
    }



	/**
	 * 获取全局上下文
	 * @return
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * 获取用户设置信息
	 * @return
	 */
	public UserConfig getUserConfig() {
        if (userConfig == null && getCurrentUser() != null) {
			userConfig = new UserConfig(this, getCurrentUser().getObjectId());
        }
        return userConfig;
    }

	/**
	 * 获取缓存用户信息
	 * @return
	 */
	public User getCurrentUser() {
		return BmobUser.getCurrentUser(context, User.class);
	}

	/**
	 * 获取用户设置信息
	 * @return
	 */
	public synchronized LikesDB getLikesDB() {
		if (likesDB == null) {
			likesDB = new LikesDB(context);
		}
		return likesDB;
	}

	/***** Activity管理 start ****/
	public void addActivity(Activity activity) {
		activities.add(activity);
	}

	public void removeActivity(Activity activity) {
		activities.remove(activity);
	}

	public void finishAll() {
		for (Activity activity : activities) {
			if (!activity.isFinishing()) {
				activity.finish();
			}
		}
	}
	/***** Activity管理 end ****/

	private void closedDB() {
		likesDB.closedDB();
		likesDB = null;
	}

	/**
	 * 注销
	 * @param isOffsite 是否显示异地登录
	 */
	public void logout(boolean isOffsite) {
		// 关闭数据库
		closedDB();
		// 注销登录设备
		PushUtil.logoutInstallation(context);
		// 用户缓存注销
		BmobUser.logOut(context);
		// 清空缓存用户配置
		userConfig = null;
		// 清空所有界面
		finishAll();
		// 跳转至登录页
		Intent intent = new Intent(context, LoginActivity.class);
		intent.putExtra("isOffsite", isOffsite);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

}
