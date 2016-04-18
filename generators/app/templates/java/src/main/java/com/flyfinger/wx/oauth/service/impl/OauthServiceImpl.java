package com.flyfinger.wx.oauth.service.impl;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.flyfinger.wx.oauth.dao.WxUserMapper;
import com.flyfinger.wx.oauth.model.WxUser;
import com.flyfinger.wx.oauth.service.OauthService;

@Service("oauthService")
public class OauthServiceImpl implements OauthService {

	@Resource(name = "wxUserMapper")
	private WxUserMapper wxUserMapper;

	@Override
	public int saveWxUser(WxUser wxUser) {
		wxUserMapper.insert(wxUser);
		return wxUser.getId();
	}

	@Override
	public void updateWxUser(WxUser wxUser) {
		wxUserMapper.updateByPrimaryKeySelective(wxUser);
	}

	@Override
	public WxUser getWxUserByOpenid(String openid) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("openid", openid);

		WxUser wxUser = wxUserMapper.getWxUserByOpenid(params);

		return wxUser;
	}

	@Override
	public WxUser getWxUserByMd5Openid(String md5Openid) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("md5Openid", md5Openid);
		WxUser wxUser = wxUserMapper.getWxUserByMd5Openid(params);

		return wxUser;
	}

	@Override
	public boolean wxOauthValidate(HttpServletResponse response,
			String md5Openid, int isLogin, String backurl) throws Exception {

		// 如果没有opendid，则调用非login登录授权，以实现已经存在的用户，不用再次登录）
		if (StringUtils.isEmpty(md5Openid)) {
			response.sendRedirect(getOauthRedirectUrl(0, backurl));
			return false;
		}

		// 获取wxUser
		WxUser wxUser = getWxUserByMd5Openid(md5Openid);

		// 用户不存在
		if (wxUser == null) {
			response.sendRedirect(getOauthRedirectUrl(isLogin, backurl));
			return false;
		}

		// 需要登录的情况下，要求获取登录信息
		if (isLogin == 1 && StringUtils.isEmpty(wxUser.getNickname())) {
			response.sendRedirect(getOauthRedirectUrl(isLogin, backurl));
			return false;
		}

		return true;
	}

	@Override
	public String getOauthRedirectUrl(int isLogin, String backurl)
			throws Exception {

		// 对返回的地址，进行URLEncoder处理
		backurl = URLEncoder.encode(backurl, "UTF-8");

		String url = "../wx/oauth?backurl=" + backurl + "&isLogin=" + isLogin;

		return url;
	}

}
