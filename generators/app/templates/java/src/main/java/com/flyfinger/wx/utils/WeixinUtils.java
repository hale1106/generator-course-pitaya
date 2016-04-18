package com.flyfinger.wx.utils;

import java.io.IOException;

import com.alibaba.fastjson.JSONObject;

public class WeixinUtils {

	public static void main(String[] args) throws IOException {

		String accessToken = "OezXcEiiBSKSxW0eoylIeAf0KL44brvBv2BY0ci2Z6dKUEXr9LZCGw5gnJkox7hN0YRPSNg4Lj4DFckbJ9MfFastFsv_J-O7p2kqIoa9KWETjxW0pxccALx-7YP3Y60DHADZe4qYIgeIxur6qu3osg";
		String openid = "oIPmZjtugU-xlxF3xir5yFsHVsGU";
		WeixinUtils.getWxUserInfo(accessToken, openid);

	}

	public static JSONObject getAccessToken(String appid, String secret,
			String code) throws IOException {

		// https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";

		url = url.replace("APPID", appid);
		url = url.replace("SECRET", secret);
		url = url.replace("CODE", code);

		JSONObject resp = WxHttpClientUtils.getForJsonResult(url);

		return resp;
	}

	public static JSONObject getWxUserInfo(String accessToken, String openid)
			throws IOException {

		// https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN
		String url = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
		url = url.replace("ACCESS_TOKEN", accessToken);
		url = url.replace("OPENID", openid);

		JSONObject resp = WxHttpClientUtils.getForJsonResult(url);

		return resp;
	}

	/**
	 * 得到获取微信授权的URL
	 * 
	 * @param appid
	 * @param responseUrl
	 * @param isLogin
	 *            是否进行登录授权 1: 登录 0: 非登录
	 * @return
	 */
	public static String getWxOauthUrl(String appid, String responseUrl,
			int isLogin) {

		String wxOauthUrl = "";
		if (isLogin == 1) {
			wxOauthUrl = getWxUserInfoOauthUrl(appid, responseUrl);

		} else {
			wxOauthUrl = getWxBaseOauthUrl(appid, responseUrl);
		}

		return wxOauthUrl;
	}

	/**
	 * 得到获取微信Base授权的URL
	 * 
	 * @param appid
	 * @param responseUrl
	 * @return
	 */
	public static String getWxBaseOauthUrl(String appid, String responseUrl) {
		String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="
				+ appid
				+ "&redirect_uri="
				+ responseUrl
				+ "&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";

		return url;
	}

	/**
	 * 得到获取微信UserInfo授权的URL
	 * 
	 * @param appid
	 * @param responseUrl
	 * @return
	 */
	public static String getWxUserInfoOauthUrl(String appid, String responseUrl) {

		String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="
				+ appid
				+ "&redirect_uri="
				+ responseUrl
				+ "&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";

		return url;
	}
}
