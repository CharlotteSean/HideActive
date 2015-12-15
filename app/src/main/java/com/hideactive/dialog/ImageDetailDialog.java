package com.hideactive.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.hideactive.R;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import com.hideactive.config.ImageLoaderOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import com.bm.library.PhotoView;

public class ImageDetailDialog extends Dialog{

    private Context context;
	private PhotoView photoView;
	private String imageSrc;

	public ImageDetailDialog(Context context, String imageSrc) {
		super(context, R.style.NormalDialog);
        this.context = context;
		this.imageSrc = imageSrc;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_image_detail);
		setCancelable(true);
		setCanceledOnTouchOutside(true);

        Window window = getWindow();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        LayoutParams params = new LayoutParams();
        params.width = width;
        params.height = height;
		params.alpha = 0.9f;
        window.setAttributes(params);
        window.setBackgroundDrawableResource(R.color.black);
		window.setWindowAnimations(R.style.dialogWindowAnim);

        photoView = (PhotoView) findViewById(R.id.photoview);
		// 启用缩放功能
		photoView.enable();
		ImageLoader.getInstance().displayImage(imageSrc, photoView, ImageLoaderOptions.getOptions());
	}
	
}
