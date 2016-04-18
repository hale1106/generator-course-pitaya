package com.flyfinger.blank.controller;

import java.io.IOException;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.flyfinger.blank.model.Demo;
import com.flyfinger.blank.service.BlankService;
import com.flyfinger.common.controller.BaseController;
import com.flyfinger.wx.oauth.common.OauthConstant;
import com.flyfinger.wx.oauth.service.OauthService;

@Controller
@RequestMapping("/blank")
public class BlankController extends BaseController {

	@Resource(name = "blankService")
	private BlankService blankService;

	@Resource(name = "oauthService")
	private OauthService oauthService;

	private static final Logger logger = Logger
			.getLogger(BlankController.class);

	@RequestMapping(value = "/index")
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		logger.info("index begin");

		try {
			String uuid = super.getData(request, OauthConstant.WX_OAUTH_KEY);

			// 是否进行登录授权
			int isLogin = 1;

			// 授权后回来的地址
			String backurl = "blank/index";

			// 微信授权验证，如果验证不通过，则进行授权调转
			if (!oauthService.wxOauthValidate(response, uuid, isLogin, backurl)) {
				return null;
			}

			Demo demo = new Demo();
			demo.setName("name");
			demo.setCreatedOn(new Date());
			demo.setModifiedOn(new Date());
			int id = blankService.doSaveDemo(demo);

			System.out.println("id:" + id);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
			return null;
		}

		ModelAndView mv = new ModelAndView("page/blank/index");

		return mv;
	}
}
