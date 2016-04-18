package com.flyfinger.test.util;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.DirectFieldAccessor;

/**
 * 一些测试帮助类
 * 
 */
public class TestHelper {

    /**
     * 将一个私有方法转化为一个可访问的方法
     * 
     * @param target
     *            测试对象
     * @param methodName
     *            方法名称
     * @param parameterTypes
     *            方法的传入参数类型
     * @return 可访问的m方法
     */
    public static Method getAccessablePrivateMethod(Object target,
            String methodName, Class<?>... parameterTypes) {
        Method method = null;
        try {
            method = target.getClass().getDeclaredMethod(methodName,
                    parameterTypes);
            method.setAccessible(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return method;
    }
    
    /**
     * 设置对象target的field属性值 = value<br>
     * 如果当前类没有此field，继续往上找<br>
     * @param <T>
     * @param type
     * @param target
     * @param field
     * @param value
     * @throws Exception 
     */
    public static <T> void setFieldValue(Object target,String field,Object value) throws Exception{
           
        Class<?> targetClazz = null;
        Object targetObejct = target;
        if(AopUtils.isJdkDynamicProxy(targetObejct)){
            //Proxy
            InvocationHandler invo = Proxy.getInvocationHandler(target);
            AdvisedSupport advised = (AdvisedSupport) new DirectFieldAccessor(invo).getPropertyValue("advised");
            targetClazz = advised.getTargetClass();
            targetObejct = advised.getTargetSource().getTarget();
        }else{
            targetClazz = AopUtils.getTargetClass(target);
        }
        Field f = null;
            try {
                f = getField(targetClazz,field);
                //f = targetClazz.getDeclaredField(field);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
            
            f.setAccessible(true);
            
            try {
                f.set(targetObejct, value);
            } catch (IllegalArgumentException e1) {
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            }       
    }

    private static Field getField(Class<?> targetClazz, String field) {
        Field f = null;
        try {
            f = targetClazz.getDeclaredField(field);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            // 获取父类
            targetClazz = getParent(targetClazz);
            if (null != targetClazz){
                f = getField(targetClazz,field);
            }
        }
        return f;
    }

    private static Class<?> getParent(Class<?> targetClazz) {
        return targetClazz.getSuperclass();
    }

    /**
     * 比较两个对象是否相等，之所以有这个方式是因为有些类没有实现equal方法，手工比较每个字段比较麻烦
     * 这里比较的过程是调用对象的所有get或者is方法看是否相等
     * 
     * @param expected
     *            期望值
     * @param actual
     *            实际值
     * @param clazz
     *            类型
     */
    public static void assertAppear(Object expected, Object actual) {
        assertEquals("class not equals", expected.getClass(), actual.getClass());

        Method[] methods = expected.getClass().getMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length == 0) {
                if (methodName.startsWith("get") || methodName.startsWith("is")) {
                    try {
                        Object ex = method.invoke(expected, new Object[] {});
                        Object ac = method.invoke(actual, new Object[] {});
                        assertEquals(methodName + "()'s return value is not equals", ex, ac);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 比较两个列表里面的内容是否相等，之所以有这个方式是因为有些列表里面的类没有实现equal方法，手工比较每个字段比较麻烦
     * 这里比较的过程是每次调用列表里面的对象的get和is方向进行比较
     * 
     * @param expected
     *            期待结果
     * @param actual
     *            实际结果
     */
    public static void assertListAppear(List<?> expected, List<?> actual) {
        Object[] expecteds = expected.toArray();
        Object[] actuals = actual.toArray();
        assertEquals("array size is not same", expecteds.length, actuals.length);

        for (int i = 0; i < expecteds.length; i++) {
            assertAppear(expecteds[i], actuals[i]);
        }
    }
        
    /** 
     * 利用递归找一个类的指定方法，如果找不到，去父亲里面找直到最上层Object对象为止。 
     *  
     * @param clazz 
     *            目标类 
     * @param methodName 
     *            方法名 
     * @param classes 
     *            方法参数类型数组 
     * @return 方法对象 
     * @throws Exception 
     */  
    public static Method getMethod(Class<?> clazz, String methodName,  
            final Class[] classes) throws Exception {  
        Method method = null;  
        try {  
            method = clazz.getDeclaredMethod(methodName, classes);  
        } catch (NoSuchMethodException e) {  
            try {  
                method = clazz.getMethod(methodName, classes);  
            } catch (NoSuchMethodException ex) {  
                if (clazz.getSuperclass() == null) {  
                    return method;  
                } else {  
                    method = getMethod(clazz.getSuperclass(), methodName,  
                            classes);  
                }  
            }  
        }  
        return method;  
    }  
  
    /** 
     *  调用某对象的私有方法<br>
     *  可以用来辅助测试<br>
     * @param obj 
     *            调整方法的对象 
     * @param methodName 
     *            方法名 
     * @param classes 
     *            参数类型数组 
     * @param objects 
     *            参数数组 
     * @return 方法的返回值 
     */  
    public static Object invoke(final Object obj, final String methodName,  
            final Class[] classes, final Object[] objects) {  
        try {  
            Method method = getMethod(obj.getClass(), methodName, classes);  
            method.setAccessible(true);// 调用private方法的关键一句话  
            return method.invoke(obj, objects);  
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);  
        }  
    }  
  
    public static Object invoke(final Object obj, final String methodName,  
            final Class[] classes) {  
        return invoke(obj, methodName, classes, new Object[] {});  
    }  
  
    public static Object invoke(final Object obj, final String methodName) {  
        return invoke(obj, methodName, new Class[] {}, new Object[] {});  
    }
    
    /**
     * 读取对象的某个属性值
     * @param <T>
     * @param target
     * @param field
     * @return
     * @throws Exception
     */
    public static <T> T readFieldValue(Object target,String field) throws Exception{
        
        Class<?> targetClazz = null;
        Object targetObejct = target;
        if(AopUtils.isJdkDynamicProxy(targetObejct)){
            //Proxy
            InvocationHandler invo = Proxy.getInvocationHandler(target);
            AdvisedSupport advised = (AdvisedSupport) new DirectFieldAccessor(invo).getPropertyValue("advised");
            targetClazz = advised.getTargetClass();
            targetObejct = advised.getTargetSource().getTarget();
        }else{
            targetClazz = AopUtils.getTargetClass(target);
        }
        
           Field f = null;
            try {
                f = targetClazz.getDeclaredField(field);
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            f.setAccessible(true);
            try {
                return (T) f.get(target);
                //f.set(target, value);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;       
    }    
}