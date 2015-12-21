package com.hideactive.model;

import android.content.Context;

import cn.bmob.v3.BmobInstallation;

/**
 * Created by Senierr on 2015/12/21.
 */
public class CustomInstallation extends BmobInstallation {

    private String uId;// 用户ID

    public CustomInstallation(Context context) {
        super(context);
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }
}
