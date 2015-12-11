package com.hideactive.config;

public class Constant {
	
	public static final String UTF8 = "UTF-8";
	public static final String HOST = "http://chuangjia.playtogather.net/";
	public static final String BMOB_APP_ID = "d38eddcc1a7a481489d087173191f89d";



	/**
	 * 微信获取用户openid
	 */
	public static final String WECHAT_GET_USEROPENID = "https://api.weixin.qq.com/sns/oauth2/access_token";
	/**
	 * 微信获取用户信息
	 */
	public static final String WECHAT_GET_USERINFO = "https://api.weixin.qq.com/sns/userinfo";
	/**
	 * 微信登录
	 */
	public static final String WECHAT_LOGIN = HOST + "api/User/Logon";
	/**
	 * 用户普通登录
	 */
	public static final String USER_LOGIN = HOST + "api/User/Logon";
	/**
	 * 获取项目列表
	 */
	public static final String GET_PRODUCTS = HOST + "api/Product/GetUserProducts?uId=";
}
