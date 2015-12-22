package com.hideactive.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.hideactive.R;

public class CameraOrNativeDialog extends Dialog{

	public static final int CAMERA = 0;
	public static final int NATIVE = 1;

    private Context context;
	private OnSelectedListener listener;

	private Button cameraBtn;
	private Button nativeBtn;

	public CameraOrNativeDialog(Context context, OnSelectedListener listener) {
		super(context, R.style.NormalDialog);
        this.context = context;
		this.listener = listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_camera_or_native);
		setCancelable(true);
		setCanceledOnTouchOutside(true);

		cameraBtn = (Button) findViewById(R.id.btn_camera);
		nativeBtn = (Button) findViewById(R.id.btn_native);

		cameraBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onSelected(CAMERA);
				cancel();
			}
		});
		nativeBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onSelected(NATIVE);
				cancel();
			}
		});
	}

	/**
	 * 选择回调接口
	 */
	public interface OnSelectedListener {
		void onSelected(int type);
	}
	
}
