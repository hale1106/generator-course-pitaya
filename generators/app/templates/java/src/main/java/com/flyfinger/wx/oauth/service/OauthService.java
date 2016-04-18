package com.flyfinger.wx.oauth.service;

import javax.servlet.http.HttpServletResponse;

import com.flyfinger.wx.oauth.model.WxUser;

public interface OauthService {

	int saveWxUser(WxUser wxUser);

	/**
	 * 更新微信用户信息
	 * 
	 * @param wxUser
	 */
	public void updateWxUser(WxUser wxUser);

	/**
	 * 根据OpenId获取微信用户信息
	 * 
	 * @param openid
	 * @return
	 */
	public WxUser getWxUserByOpenid(String openid);

	/**
	 * 根据MD5加密后的OpenId获取微信用户信息
	 * 
	 * @param md5Openid
	 * @return
	 */
	public WxUser getWxUserByMd5Openid(String md5Openid);

	/**
	 * 微信授权验证
	 * 
	 * @param md5Openid
	 * @return
	 * @throws Exception
	 */
	public boolean wxOauthValidate(HttpServletResponse response,
			String md5Openid, int isLogin, String backurl) throws Exception;

	/**
	 * 获取微信授权跳转URL
	 * 
	 * @param isLogin
	 * @param backurl
	 * @return
	 * @throws Exception
	 */
	public String getOauthRedirectUrl(int isLogin, String backurl)
			throws Exception;

}
