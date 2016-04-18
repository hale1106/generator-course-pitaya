package com.flyfinger.wx.oauth.dao;

import java.util.Map;

import com.flyfinger.dao.Mapper;
import com.flyfinger.wx.oauth.model.WxUser;

public interface WxUserMapper extends Mapper<WxUser> {

	public WxUser getWxUserByOpenid(Map<String, Object> params);
	
	public WxUser getWxUserByMd5Openid(Map<String, Object> params);
}