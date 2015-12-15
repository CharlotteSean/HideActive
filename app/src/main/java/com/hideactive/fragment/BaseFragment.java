package com.hideactive.fragment;

import java.lang.reflect.Field;

import com.hideactive.SessionApplication;
import com.hideactive.dialog.LoadingDialog;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

public class BaseFragment extends Fragment{

	private String title;
	private int iconId;

	protected LoadingDialog loadingDialog;
	protected SessionApplication application;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadingDialog = new LoadingDialog(getActivity());
	    application = SessionApplication.getInstance();
	    hideSoftInputView();
	    setOverflowShowingAlways();
	}

	public View findViewById(int paramInt) {
		return getView().findViewById(paramInt);
	}
	
	/**
	 * 隐藏软键盘
	 */
	public void hideSoftInputView() {
		InputMethodManager manager = ((InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE));
		if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getActivity().getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	
	/**
	 * 始终显示ActionBar中的overflow
	 */
	private void setOverflowShowingAlways() {  
        try {  
            ViewConfiguration config = ViewConfiguration.get(getActivity());  
            Field menuKeyField = ViewConfiguration.class  
                    .getDeclaredField("sHasPermanentMenuKey");  
            menuKeyField.setAccessible(true);  
            menuKeyField.setBoolean(config, false);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }
}
