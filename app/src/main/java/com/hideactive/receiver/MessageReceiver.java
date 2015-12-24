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
import com.hideactive.config.UserConfig;
import com.hideactive.dialog.OffsiteNotifyDialog;
import com.hideactive.model.PushMessage;
import com.hideactive.util.NotifyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.bmob.push.PushConstants;
import cn.bmob.v3.BmobInstallation;

/**
 * Created by Senierr on 2015/12/21.
 */
public class MessageReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(PushConstants.ACTION_MESSAGE)){
            Log.d("bmob", "客户端收到推送内容：" + intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING));
            PushMessage pushMessage = PushMessage.pase2Message(intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING));

            UserConfig userConfig = SessionApplication.getInstance().getUserConfig();
            // 若当前没用户登录，则不提示
            if (userConfig == null) {
                return;
            }
            switch (pushMessage.getType()) {
                case PushMessage.TYPE_TEXT:
                    boolean isNotify = userConfig.isAllowNotify();
                    if (!isNotify) {
                        return;
                    }
                    boolean isNotifyDetail = userConfig.isAllowNotifyDetail();
                    String content = null;
                    if (isNotifyDetail) {
                        content = pushMessage.getUsername()
                                + ": "
                                + pushMessage.getContent();
                    } else {
                        content = context.getString(R.string.new_comment);
                    }
                    Intent targetIntent = null;
                    if (isRunning(context)) {
                        targetIntent = null;
                    } else {
                        targetIntent = new Intent(context, SplashActivity.class);
                    }
                    boolean isAllowVirbate = userConfig.isAllowVibrate();
                    boolean isAllowVoice = userConfig.isAllowVoice();
                    NotifyUtil.getInstance(context).showNotifyWithExtras(
                            isAllowVoice, isAllowVirbate,
                            R.mipmap.tab_home_normal, context.getString(R.string.new_comment),
                            context.getString(R.string.app_name), content,
                            targetIntent
                    );
                    break;
                case PushMessage.TYPE_OFFSITE:
                    // 若是通知的是当前登录的设备，则不提醒
                    if (BmobInstallation.getInstallationId(context).equalsIgnoreCase(pushMessage.getUsername())) {
                        return;
                    }
                    // 是否显示异地通知
                    if (!userConfig.isOffsiteNotify()) {
                        return;
                    }
                    // 注销退出,并提示
                    SessionApplication.getInstance().logout(true);
                    break;
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
