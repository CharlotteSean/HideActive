package com.hideactive.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;

/**
 * Created by Senierr on 2015/12/21.
 */
public class NotifyUtil {

    public static NotificationManager mNotificationManager;

    //通知ID
    public static final int NOTIFY_ID = 0x000;

    private Context globalContext;
    // 创建private static类实例
    private volatile static NotifyUtil INSTANCE;
    //同步锁
    private static Object INSTANCE_LOCK = new Object();

    /**
     * 使用单例模式创建--双重锁定
     */
    public static NotifyUtil getInstance(Context context) {
        if (INSTANCE == null)
            synchronized (INSTANCE_LOCK) {
                if (INSTANCE == null) {
                    INSTANCE = new NotifyUtil();
                }
                INSTANCE.init(context);
            }
        return INSTANCE;
    }

    /**
     * 只初始化创建一次上下文对象 init
     */
    public void init(Context context) {
        this.globalContext = context;
        mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**创建显示通知栏
     * @param  icon:通知栏的图标
     * @param  tickerText：状态栏提示语
     * @param  contentTitle：通知标题
     * @param  contentText：通知内容
     * @param  targetIntent ：点击之后进入的Class
     * @return
     * @throws
     */
    public void showNotifyWithExtras(boolean isAllowVoice,boolean isAllowVirbate,int icon,String tickerText,String contentTitle,String contentText,Intent targetIntent) {
        PendingIntent pendingIntent = PendingIntent.getActivity(globalContext, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = null;
        notification = new Notification.Builder(globalContext)
                .setSmallIcon(icon)
                .setTicker(tickerText)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setContentIntent(pendingIntent)
                .build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        if(isAllowVoice){
            // 设置默认声音
            notification.defaults |= Notification.DEFAULT_SOUND;
        }
        if(isAllowVirbate){
            // 设定震动(需加VIBRATE权限)
            notification.defaults |= Notification.DEFAULT_VIBRATE;
        }
        mNotificationManager.notify(0, notification);
    }

    /** 取消指定通知栏
     * @param
     * @return
     */
    public void cancelNotify(){
        if(mNotificationManager!=null){
            mNotificationManager.cancel(NOTIFY_ID);
        }
    }

    /** 取消所有通知栏
     * @param
     * @return
     */
    public void cancelAll(){
        if(mNotificationManager!=null){
            mNotificationManager.cancelAll();
        }
    }

}
