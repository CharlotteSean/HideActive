package com.hideactive.dialog;

import com.hideactive.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

public class LoadingDialog extends Dialog{

	public LoadingDialog(Context context) {
		super(context, R.style.NormalDialog);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_loading);
		setCancelable(false);
		setCanceledOnTouchOutside(false);
	}
	
}
