package com.hideactive.activity;

import java.lang.reflect.Field;

import com.hideactive.R;
import com.hideactive.SessionApplication;
import com.hideactive.dialog.LoadingDialog;
import com.hideactive.util.ActivityCollector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

public class BaseActivity extends Activity {

	protected LoadingDialog loadingDialog;
	protected SessionApplication application;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		hideSoftInputView();
	    loadingDialog = new LoadingDialog(this);
	    application = SessionApplication.getInstance();
		application.addActivity(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		application.removeActivity(this);
	}
	
	/**
	 * 隐藏软键盘
	 */
	public void hideSoftInputView() {
		InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * 打开Activity，并附带动画
     * 可选则anim文件夹下任意动画，此处为其中一种案例
	 * @param intent
	 */
	protected void openActivity(Intent intent) {
		startActivity(intent);
		overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
	}

	/**
	 * 关闭Activity，并附带动画
     * 可选则anim文件夹下任意动画，此处为其中一种案例
	 */
	protected void closeActivity() {
		finish();
		overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
	}

	/**
	 * 打开Activity销毁当前activity，并附带动画
	 * 可选则anim文件夹下任意动画，此处为其中一种案例
	 * @param intent
	 */
	protected void openActivityAndClose(Intent intent) {
		startActivity(intent);
		overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
		finish();
	}

}
