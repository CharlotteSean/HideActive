package com.hideactive.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.hideactive.R;
import com.hideactive.util.ViewUtil;

public class TabButtonView extends View {

	private Drawable drawable;
	private Drawable choose_drawable;
	private String text;
	private float textSize;
	private int textColor;
	private int textChooseColor;
	private Paint pt;
	private Context context;

	public TabButtonView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.TabButtonView);
		drawable = a.getDrawable(R.styleable.TabButtonView_icon);
		choose_drawable = a.getDrawable(R.styleable.TabButtonView_icon_selected);
		textColor = a.getColor(R.styleable.TabButtonView_text_color, 0x000000);
		textChooseColor = a.getColor(
				R.styleable.TabButtonView_text_color_selected, 0xffffff);
		text = a.getString(R.styleable.TabButtonView_text);
		textSize = a.getDimension(R.styleable.TabButtonView_text_size, 18);
		a.recycle();
		pt = new Paint();
	}

	private Bitmap drawable2Bitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		} else if (drawable instanceof NinePatchDrawable) {
			Bitmap bitmap = Bitmap
					.createBitmap(
							drawable.getIntrinsicWidth(),
							drawable.getIntrinsicHeight(),
							drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
									: Bitmap.Config.RGB_565);
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());
			drawable.draw(canvas);
			return bitmap;
		} else {
			return null;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Bitmap image = null;
		pt.setAntiAlias(true);
		
		if (isSelected()) {
			image = drawable2Bitmap(choose_drawable);
			pt.setColor(textChooseColor);
			canvas.drawRect(0, 0, getWidth(), 3, pt);
		} else {
			image = drawable2Bitmap(drawable);
			pt.setColor(textColor);
		}
		
		if (!TextUtils.isEmpty(text)) {
			pt.setTextSize(textSize);
			pt.setTextAlign(Align.CENTER);
			FontMetricsInt fontMetrics = pt.getFontMetricsInt();
			float baseline = (getHeight() - fontMetrics.bottom + fontMetrics.top)/2 - fontMetrics.top;
			canvas.drawText(text, getWidth()/2, baseline, pt);
		}
		
		if (image != null) {
			float imageX = this.getWidth()/2 - image.getWidth() - ViewUtil.dip2px(context, 5);
			float imageY = this.getHeight()/2 - image.getHeight()/2;
			canvas.drawBitmap(image, imageX, imageY, pt);
		}
		
	}
}
