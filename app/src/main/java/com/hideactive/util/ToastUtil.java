package com.hideactive.util;

import android.content.Context;
import android.widget.Toast;

import com.hideactive.SessionApplication;

/**
 * Created by Senierr on 2015/12/11.
 */
public class ToastUtil {
    public static void showShort(String msg) {
        Toast.makeText(SessionApplication.getInstance().getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
