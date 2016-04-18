package com.flyfinger.wx.oauth.controller;

import java.net.URLEncoder;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.flyfinger.common.controller.BaseController;
import com.flyfinger.utils.FlyfingerConfig;
import com.flyfinger.wx.oauth.common.OauthConstant;
import com.flyfinger.wx.oauth.model.WxUser;
import com.flyfinger.wx.oauth.service.OauthService;
import com.flyfinger.wx.utils.WeixinUtils;

@Controller
@RequestMapping("/wx")
public class WxOauthController extends BaseController {

	private static final String APP_ID;

	private static final String APP_SECRET;

	private static final String WX_OAUTH_RESPONSE_URL;

	private static final String WX_OAUTH_LOGIN_RESPONSE_URL;

	@Resource(name = "oauthService")
	private OauthService oauthService;

	static {
		APP_ID = FlyfingerConfig.getValue("app_id");
		APP_SECRET = FlyfingerConfig.getValue("app_secret");

		WX_OAUTH_RESPONSE_URL = FlyfingerConfig
				.getValue("wx_oauth_response_url");

		WX_OAUTH_LOGIN_RESPONSE_URL = FlyfingerConfig
				.getValue("wx_oauth_login_response_url");
	}

	private static final Logger logger = Logger
			.getLogger(WxOauthController.class);

	/**
	 * 微信授权转发
	 */
	@RequestMapping(value = "/oauth")
	public ModelAndView oauth(
			@RequestParam(required = true, defaultValue = "") String backurl,
			@RequestParam(required = true, defaultValue = "") int isLogin,
			HttpServletRequest request, HttpServletResponse response) {

		try {

			backurl = URLEncoder.encode(backurl, "UTF-8");

			String responseUrl = "";

			// 获取授权回调地址
			if (isLogin == 1) {
				responseUrl = WX_OAUTH_LOGIN_RESPONSE_URL + "?backurl="
						+ backurl;
			} else {
				responseUrl = WX_OAUTH_RESPONSE_URL + "?backurl=" + backurl;
			}

			String oauthRedirectUrl = WeixinUtils.getWxOauthUrl(APP_ID,
					responseUrl, isLogin);

			response.sendRedirect(oauthRedirectUrl);

			return null;

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}

	/**
	 * 微信授权转发（无登陆）
	 */
	@RequestMapping(value = "/oauthResponse")
	public ModelAndView oauthResponse(
			@RequestParam(required = true, defaultValue = "") String backurl,
			@RequestParam(required = true, defaultValue = "") String code,
			HttpServletRequest request, HttpServletResponse response) {

		try {

			logger.info("[oauthResponse] backurl:" + backurl);

			JSONObject json = WeixinUtils.getAccessToken(APP_ID, APP_SECRET,
					code);

			String openid = json.getString("openid");

			String md5Openid = DigestUtils.md5Hex(openid);

			// 对一个openid进行同步处理
			synchronized (md5Openid.intern()) {
				WxUser wxUser = oauthService.getWxUserByOpenid(openid);

				if (wxUser == null) {

					wxUser = new WxUser();
					wxUser.setOpenid(openid);
					wxUser.setMd5Openid(md5Openid);
					wxUser.setCreatedOn(new Date());
					wxUser.setModifiedOn(new Date());

					// 保存用户信息
					oauthService.saveWxUser(wxUser);
				}
			}

			setData(request, response, OauthConstant.WX_OAUTH_KEY, md5Openid);

			// 页面跳转分发
			response.sendRedirect("../" + backurl);
			return null;

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}

	/**
	 * 微信授权转发（登陆）
	 */
	@RequestMapping(value = "/oauthLoginResponse")
	public ModelAndView oauthLoginResponse(
			@RequestParam(required = true, defaultValue = "") String backurl,
			@RequestParam(required = true, defaultValue = "") String code,
			HttpServletRequest request, HttpServletResponse response) {

		try {

			logger.info("[oauthLoginResponse] backurl:" + backurl);

			JSONObject json = WeixinUtils.getAccessToken(APP_ID, APP_SECRET,
					code);

			String accessToken = json.getString("access_token");
			String openid = json.getString("openid");

			String md5Openid = DigestUtils.md5Hex(openid);

			// 对一个openid进行同步处理
			synchronized (md5Openid.intern()) {
				WxUser wxUser = oauthService.getWxUserByOpenid(openid);

				if (wxUser == null) {

					wxUser = new WxUser();
					wxUser.setOpenid(openid);
					wxUser.setMd5Openid(md5Openid);

					// 获取微信用户信息
					JSONObject userInfoJson = WeixinUtils.getWxUserInfo(
							accessToken, openid);

					wxUser.setNickname(userInfoJson.getString("nickname"));
					// 值为1时是男性，值为2时是女性，值为0时是未知
					wxUser.setSex(userInfoJson.getString("sex"));
					wxUser.setProvince(userInfoJson.getString("province"));
					wxUser.setCity(userInfoJson.getString("city"));
					wxUser.setCountry(userInfoJson.getString("country"));
					wxUser.setHeadimgurl(userInfoJson.getString("headimgurl"));
					wxUser.setCreatedOn(new Date());
					wxUser.setModifiedOn(new Date());

					// 保存用户信息
					oauthService.saveWxUser(wxUser);
				} else {

					// 如果已经存在用户信息，但是nickname为空
					if (StringUtils.isEmpty(wxUser.getNickname())) {
						// 获取微信用户信息
						JSONObject userInfoJson = WeixinUtils.getWxUserInfo(
								accessToken, openid);

						wxUser.setNickname(userInfoJson.getString("nickname"));
						// 值为1时是男性，值为2时是女性，值为0时是未知
						wxUser.setSex(userInfoJson.getString("sex"));
						wxUser.setProvince(userInfoJson.getString("province"));
						wxUser.setCity(userInfoJson.getString("city"));
						wxUser.setCountry(userInfoJson.getString("country"));
						wxUser.setHeadimgurl(userInfoJson
								.getString("headimgurl"));
						wxUser.setModifiedOn(new Date());

						oauthService.updateWxUser(wxUser);
					}
				}
			}

			setData(request, response, OauthConstant.WX_OAUTH_KEY, md5Openid);

			// 页面跳转分发
			response.sendRedirect("../" + backurl);
			return null;

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}

	/**
	 * 设置Cookie
	 */
	@RequestMapping(value = "/reset")
	public ModelAndView reset(
			@RequestParam(required = true, defaultValue = "") int times,
			HttpServletRequest request, HttpServletResponse response) {

		try {
			String key = OauthConstant.WX_OAUTH_KEY;
			String data = "db1dfbc4f1244cd7c3a87f53ab994fd2";

			setData(request, response, key, data, times);

			return null;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
