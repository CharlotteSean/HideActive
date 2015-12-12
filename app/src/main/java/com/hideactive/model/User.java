package com.hideactive.model;

import cn.bmob.v3.BmobUser;

/**
 * 用户
 */
public class User extends BmobUser {

	private String logo;// 用户头像
	private int sex;// 性别
	private int age;// 年龄

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
