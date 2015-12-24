package com.hideactive.util;

import android.content.Context;
import android.util.Log;

import com.hideactive.R;
import com.hideactive.model.CustomInstallation;
import com.hideactive.model.PushMessage;

import java.util.List;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Senierr on 2015/12/21.
 */
public class PushUtil {

    /**
     * 更新登录设备
     * @param context
     * @param uId
     */
    public static void updateInstallation(final Context context, final String uId) {
        BmobQuery<CustomInstallation> query = new BmobQuery<CustomInstallation>();
        query.addWhereEqualTo("installationId", BmobInstallation.getInstallationId(context));
        query.findObjects(context, new FindListener<CustomInstallation>() {
            @Override
            public void onSuccess(List<CustomInstallation> object) {
                if(object.size() > 0){
                    CustomInstallation customInstallation = object.get(0);
                    customInstallation.setuId(uId);
                    customInstallation.update(context,new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            Log.i("bmob", "设备信息更新成功");
                        }

                        @Override
                        public void onFailure(int code, String msg) {
                            Log.i("bmob","设备信息更新失败:"+msg);
                        }
                    });
                }else{
                }
            }

            @Override
            public void onError(int code, String msg) {
            }
        });
    }

    /**
     * 注销登录设备
     * @param context
     */
    public static void logoutInstallation(final Context context) {
        BmobQuery<CustomInstallation> query = new BmobQuery<CustomInstallation>();
        query.addWhereEqualTo("installationId", BmobInstallation.getInstallationId(context));
        query.findObjects(context, new FindListener<CustomInstallation>() {
            @Override
            public void onSuccess(List<CustomInstallation> object) {
                if(object.size() > 0){
                    CustomInstallation customInstallation = object.get(0);
                    customInstallation.setuId("");
                    customInstallation.update(context,new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            Log.i("bmob", "设备信息注销成功");
                        }

                        @Override
                        public void onFailure(int code, String msg) {
                            Log.i("bmob","设备信息注销失败:"+msg);
                        }
                    });
                }else{
                }
            }

            @Override
            public void onError(int code, String msg) {
            }
        });
    }

    /**
     * 给对应user发推送
     * @param context
     * @param uId
     * @param message
     */
    public static void push2User(final Context context, final String uId, final String message) {
        BmobQuery<CustomInstallation> query = new BmobQuery<CustomInstallation>();
        query.addWhereEqualTo("uId", uId);
        query.findObjects(context, new FindListener<CustomInstallation>() {
            @Override
            public void onSuccess(List<CustomInstallation> object) {
                if(object.size() > 0){
                    CustomInstallation customInstallation = object.get(0);
                    String installationId = customInstallation.getInstallationId();
                    BmobPushManager bmobPush = new BmobPushManager(context);
                    BmobQuery<BmobInstallation> query = BmobInstallation.getQuery();
                    query.addWhereEqualTo("installationId", installationId);
                    bmobPush.setQuery(query);
                    bmobPush.pushMessage(message);
                }
            }

            @Override
            public void onError(int code, String msg) {
            }
        });
    }

    /**
     * 提醒异地登录的当前用户设备
     * @param context
     * @param uId
     */
    public static void notifyOffsite(final Context context, final String uId) {
        BmobQuery<CustomInstallation> query = new BmobQuery<CustomInstallation>();
        query.addWhereEqualTo("uId", uId);
        query.findObjects(context, new FindListener<CustomInstallation>() {
            @Override
            public void onSuccess(List<CustomInstallation> object) {
                if(object.size() > 0){
                    // 给查到的每个设备发送
                    for (int i = 0; i < object.size(); i++) {
                        CustomInstallation customInstallation = object.get(i);
                        // 发送推送
                        PushMessage pushMessage = new PushMessage();
                        pushMessage.setType(PushMessage.TYPE_OFFSITE);
                        pushMessage.setUsername(BmobInstallation.getInstallationId(context));
                        pushMessage.setContent(context.getString(R.string.offsite_notify_message));

                        BmobPushManager bmobPush = new BmobPushManager(context);
                        BmobQuery<BmobInstallation> query = BmobInstallation.getQuery();
                        query.addWhereEqualTo("installationId", customInstallation.getInstallationId());
                        bmobPush.setQuery(query);
                        bmobPush.pushMessage(PushMessage.pase2Json(pushMessage).toString());
                    }
                }
                // 更新当前设备登录用户
                updateInstallation(context, uId);
            }

            @Override
            public void onError(int code, String msg) {
            }
        });
    }

}
