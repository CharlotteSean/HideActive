package com.hideactive.receiver;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hideactive.R;
import com.hideactive.SessionApplication;
import com.hideactive.activity.LoginActivity;
import com.hideactive.activity.SplashActivity;
import com.hideactive.util.NotifyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.bmob.push.PushConstants;

/**
 * Created by Senierr on 2015/12/21.
 */
public class MessageReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(PushConstants.ACTION_MESSAGE)){
            Log.d("bmob", "客户端收到推送内容：" + intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING));
            try {
                String message = new JSONObject(intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING)).optString("alert");
                JSONObject jsonObject = new JSONObject(message);
                Intent targetIntent = null;
                if (isRunning(context)) {
                    targetIntent = null;
                } else {
                    targetIntent = new Intent(context, SplashActivity.class);
                }
                NotifyUtil.getInstance(context).showNotifyWithExtras(
                        true, true,
                        R.mipmap.tab_home_selected, "您有新的评论", jsonObject.optString("username"), jsonObject.optString("content"),
                        targetIntent
                );
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断APP是否在运行
     * @param context
     * @return
     */
    private boolean isRunning(Context context) {
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals("com.hideactive")
                    && info.baseActivity.getPackageName().equals("com.hideactive")) {
                return true;
            }
        }
        return false;
    }
}
