package com.flyfinger.test.dbunit;

import java.lang.reflect.Method;

import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

/**
 * 注册到Spring-test的listener，这样就能在junit执行周期中插入dbunit的准备和清除数据操作
 * 
 */
public class DBUnitExecutionListener extends AbstractTestExecutionListener {

	@Override
	public void beforeTestMethod(TestContext testContext) throws Exception {
		Object testInstance = testContext.getTestInstance();
		Method testMethod = testContext.getTestMethod();
		if (testInstance instanceof AbstractDBUnitSpringContextTests) {
			DBUnitFile annotation = testMethod.getAnnotation(DBUnitFile.class);
			if (annotation != null) {
				String value = annotation.value();
				((AbstractDBUnitSpringContextTests) testInstance)
						.setDbunitFile(value);
				((AbstractDBUnitSpringContextTests) testInstance)
						.beforeForDBUnit();
			}
		}
	}

	@Override
	public void afterTestMethod(TestContext testContext) throws Exception {
		Object testInstance = testContext.getTestInstance();
		Method testMethod = testContext.getTestMethod();
		if (testInstance instanceof AbstractDBUnitSpringContextTests) {
			DBUnitFile annotation = testMethod.getAnnotation(DBUnitFile.class);
			if (annotation != null) {
				((AbstractDBUnitSpringContextTests) testInstance)
						.afterForDBUnit();
			}
		}
	}
}
