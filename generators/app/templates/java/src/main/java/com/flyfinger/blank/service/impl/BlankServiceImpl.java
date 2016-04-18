package com.flyfinger.blank.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.flyfinger.blank.dao.DemoMapper;
import com.flyfinger.blank.model.Demo;
import com.flyfinger.blank.service.BlankService;

@Service("blankService")
public class BlankServiceImpl implements BlankService {

	@Resource(name = "demoMapper")
	private DemoMapper demoMapper;

	@Override
	public int doSaveDemo(Demo demo) {
		demoMapper.insertSelective(demo);
		return demo.getId();
	}

}
