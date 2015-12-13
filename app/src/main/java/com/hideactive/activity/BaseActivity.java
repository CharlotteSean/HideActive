package com.hideactive.activity;

import java.lang.reflect.Field;

import com.hideactive.SessionApplication;
import com.hideactive.dialog.LoadingDialog;
import com.hideactive.util.ActivityCollector;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

public class BaseActivity extends Activity {

	protected LoadingDialog loadingDialog;
	protected SessionApplication application;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityCollector.addActivity(this);
	    hideSoftInputView();
	    loadingDialog = new LoadingDialog(this);
	    application = SessionApplication.getInstance();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivityCollector.removeActivity(this);
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
	
}
