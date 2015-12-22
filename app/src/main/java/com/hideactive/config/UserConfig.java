package com.hideactive.config;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

@SuppressLint("CommitPrefEdits")
public class UserConfig {
	
	private SharedPreferences mSharedPreferences;
	private static SharedPreferences.Editor editor;
	/**
	 * SharePreference名
	 */
	public static final String PRE_NAME = "user_config";
	/**
	 * key值
	 */
	private String SHARED_KEY_NOTIFY = "shared_key_notify";
	private String SHARED_KEY_NOTIFY_DETAIL = "shared_key_notify_detail";
	private String SHARED_KEY_VOICE = "shared_key_sound";
	private String SHARED_KEY_VIBRATE = "shared_key_vibrate";

	public UserConfig(Context context, String uId) {
		mSharedPreferences = context.getSharedPreferences(
				PRE_NAME + "_" + uId,
				Context.MODE_PRIVATE);
		editor = mSharedPreferences.edit();
	}
	
	// 清除缓存
	public void clean() {
		editor.clear();
		editor.commit();
	}
	
	// 是否允许推送
	public boolean isAllowNotify() {
		return mSharedPreferences.getBoolean(SHARED_KEY_NOTIFY, true);
	}

	public void setPushNotifyEnable(boolean isChecked) {
		editor.putBoolean(SHARED_KEY_NOTIFY, isChecked);
		editor.commit();
	}

	// 是否允许通知显示详情
	public boolean isAllowNotifyDetail() {
		return mSharedPreferences.getBoolean(SHARED_KEY_NOTIFY_DETAIL, true);
	}

	public void setNotifyDetailEnable(boolean isChecked) {
		editor.putBoolean(SHARED_KEY_NOTIFY_DETAIL, isChecked);
		editor.commit();
	}

	// 是否允许声音
	public boolean isAllowVoice() {
		return mSharedPreferences.getBoolean(SHARED_KEY_VOICE, true);
	}

	public void setAllowVoiceEnable(boolean isChecked) {
		editor.putBoolean(SHARED_KEY_VOICE, isChecked);
		editor.commit();
	}

	// 是否允许震动
	public boolean isAllowVibrate() {
		return mSharedPreferences.getBoolean(SHARED_KEY_VIBRATE, true);
	}

	public void setAllowVibrateEnable(boolean isChecked) {
		editor.putBoolean(SHARED_KEY_VIBRATE, isChecked);
		editor.commit();
	}
}
