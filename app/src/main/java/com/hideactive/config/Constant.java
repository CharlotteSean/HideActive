package com.hideactive.config;

import android.os.Environment;

import java.io.File;

public class Constant {
	
	public static final String UTF8 = "UTF-8";
	public static final String BMOB_APP_ID = "d38eddcc1a7a481489d087173191f89d";
	public static final String IMAGE_CACHE_PATH = Environment.getExternalStorageDirectory()
			+ File.separator + "HideActive"+ File.separator + "Cache";

}
