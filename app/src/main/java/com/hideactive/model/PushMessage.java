package com.hideactive.model;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.push.PushConstants;

/**
 * Created by v-zhchu on 12/21/2015.
 */
public class PushMessage {

    public static final String TYPE = "type";
    public static final String USERNAME = "username";
    public static final String CONTENT = "content";

    public static final int TYPE_TEXT = 0;// 普通文本推送信息
    public static final int TYPE_OFFSITE = 1;// 异地登录提醒

    private int type;// 推送消息类型
    private String username;// 发送者昵称
    private String content;// 内容

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 将对象转成Json对象
     * @param message
     * @return
     */
    public static JSONObject pase2Json(PushMessage message) {
        if (message == null) {
            return null;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(TYPE, message.getType());
            jsonObject.put(USERNAME, message.getUsername());
            jsonObject.put(CONTENT, message.getContent());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 将推送来的消息转成对象
     * @param receiverStr
     * @return
     */
    public static PushMessage pase2Message(String receiverStr) {
        PushMessage pushMessage = new PushMessage();
        try {
            String message = new JSONObject(receiverStr).optString("alert");
            JSONObject jsonObject = new JSONObject(message);
            pushMessage.setType(jsonObject.optInt(TYPE));
            pushMessage.setUsername(jsonObject.optString(USERNAME));
            pushMessage.setContent(jsonObject.optString(CONTENT));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return pushMessage;
    }

}
