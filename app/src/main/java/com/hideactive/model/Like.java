package com.hideactive.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by Senierr on 2015/12/17.
 */
public class Like extends BmobObject {

    private String uId;
    private String postId;

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
