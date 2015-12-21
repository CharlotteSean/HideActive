package com.hideactive.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by v-zhchu on 12/21/2015.
 */
public class Message extends BmobObject{

    private String content;// 内容
    private User fromUser;// 发送者
    private User toUser;// 接收者
    private Post post;// 对应的帖子

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getFromUser() {
        return fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public User getToUser() {
        return toUser;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
