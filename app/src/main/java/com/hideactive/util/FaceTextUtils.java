package com.hideactive.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;

public class FaceTextUtils {

	public static class FaceText {
		public String text;

		public FaceText(String text) {
			this.text = text;
		}
	}

	public static List<FaceText> faceTexts = new ArrayList<FaceText>();
	static {
		for (int i = 1; i < 93; i++) {
			if (i < 10) {
				faceTexts.add(new FaceText("\\ue00" + i));
			} else {
				faceTexts.add(new FaceText("\\ue0" + i));
			}
		}
	}

	public static String parse(String s) {
		for (FaceText faceText : faceTexts) {
			s = s.replace("\\" + faceText.text, faceText.text);
			s = s.replace(faceText.text, "\\" + faceText.text);
		}
		return s;
	}

	/** 
	  * toSpannableString
	  * @return SpannableString
	  * @throws
	  */
	public static SpannableString toSpannableString(Context context, String text) {
		try {
			if (!TextUtils.isEmpty(text)) {
				SpannableString spannableString = new SpannableString(text);
				int start = 0;
				Pattern pattern = Pattern.compile("\\\\ue[a-z0-9]{3}", Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(text);
				while (matcher.find()) {
					String faceText = matcher.group();
					String key = faceText.substring(1);
					BitmapFactory.Options options = new BitmapFactory.Options();
					Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
							context.getResources().getIdentifier(key, "drawable", context.getPackageName()), options);
					ImageSpan imageSpan = new ImageSpan(context, bitmap);
					int startIndex = text.indexOf(faceText, start);
					int endIndex = startIndex + faceText.length();
					if (startIndex >= 0)
						spannableString.setSpan(imageSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					start = (endIndex - 1);
				}
				return spannableString;
			} else {
				return new SpannableString("");
			}
		} catch (Exception e) {
			return new SpannableString("");
		}
	}

}
