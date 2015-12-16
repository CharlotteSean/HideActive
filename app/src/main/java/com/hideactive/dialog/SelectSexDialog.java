package com.hideactive.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.hideactive.R;

public class SelectSexDialog extends Dialog{

	private View itemMaleView;
	private View itemFemaleView;
	private ImageView maleImageView;
	private ImageView femaleImageView;

	private int sex;
	private OnDoneListener onDoneListener;

	public SelectSexDialog(Context context, int sex, OnDoneListener onDoneListener) {
		super(context, R.style.NormalDialog);
		this.sex = sex;
		this.onDoneListener = onDoneListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_select_sex);
		setCancelable(true);
		setCanceledOnTouchOutside(true);

		itemMaleView = findViewById(R.id.item_male);
		itemFemaleView = findViewById(R.id.item_female);
		maleImageView = (ImageView) findViewById(R.id.image_male);
		femaleImageView = (ImageView) findViewById(R.id.image_female);

		switch (sex) {
			case 0:
				maleImageView.setSelected(true);
				femaleImageView.setSelected(false);
				break;
			case 1:
				maleImageView.setSelected(false);
				femaleImageView.setSelected(true);
				break;
			default:
				maleImageView.setSelected(true);
				femaleImageView.setSelected(false);
				break;
		}

		itemMaleView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				maleImageView.setSelected(true);
				femaleImageView.setSelected(false);
				sex = 0;
				onDoneListener.onDone(sex);
				cancel();
			}
		});
		itemFemaleView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				maleImageView.setSelected(false);
				femaleImageView.setSelected(true);
				sex = 1;
				onDoneListener.onDone(sex);
				cancel();
			}
		});

	}

	/**
	 * 修改完，回调接口
	 */
	public interface OnDoneListener {
		void onDone(int sex);
	}

}
