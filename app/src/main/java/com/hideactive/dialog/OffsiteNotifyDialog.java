package com.hideactive.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.hideactive.R;

public class OffsiteNotifyDialog extends Dialog{

	public OffsiteNotifyDialog(Context context) {
		super(context, R.style.NormalDialog);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_offsite_notify);
		setCancelable(true);
		setCanceledOnTouchOutside(true);
	}
	
}
