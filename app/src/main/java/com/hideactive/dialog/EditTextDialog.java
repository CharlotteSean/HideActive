package com.hideactive.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hideactive.R;
import com.hideactive.util.ToastUtil;

public class EditTextDialog extends Dialog{

	private TextView titleView;
	private TextView desView;
	private ImageButton doneButton;
	private EditText editTextView;

	private String titleStr;
	private String desStr;
	private int inputType;
	private String contentStr;
	private OnDoneListener onDoneListener;

	public EditTextDialog(Context context, @NonNull String titleStr,
						  String desStr, int inputType,
						  String contentStr, OnDoneListener onDoneListener) {
		super(context, R.style.NormalDialog);
		this.titleStr = titleStr;
		this.desStr = desStr;
		this.inputType = inputType;
		this.contentStr = contentStr;
		this.onDoneListener = onDoneListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_edit_text);
		setCancelable(true);
		setCanceledOnTouchOutside(true);

		titleView = (TextView) findViewById(R.id.title);
		desView = (TextView) findViewById(R.id.description);
		doneButton = (ImageButton) findViewById(R.id.done);
		editTextView = (EditText) findViewById(R.id.edit_text);

		titleView.setText(titleStr);
		desView.setText(desStr);
		editTextView.setText(contentStr);
		editTextView.setSelection(editTextView.getText().length());
		editTextView.setInputType(inputType);
		doneButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String content = editTextView.getText().toString();
				if (TextUtils.isEmpty(content)) {
					ToastUtil.showShort("内容不能为空！");
					return;
				}
				onDoneListener.onDone(content);
				cancel();
			}
		});
	}

	/**
	 * 修改完，回调接口
	 */
	public interface OnDoneListener {
		void onDone(String contentStr);
	}

}
