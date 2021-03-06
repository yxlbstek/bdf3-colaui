package com.bstek.cola.utils;

import java.lang.reflect.Field;


/** 
* 
* @author bob.yang
* @since 2018年7月4日
*
*/
public final class BeanUtils {

	public static <T> T getFieldValue(Object bean, String name) {
		Field field = FieldUtils.getField(bean.getClass(), name);
		return getFieldValue(bean, field);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getFieldValue(Object bean, Field field) {
		T result = null;
		try {
			field.setAccessible(true);
			result = (T) field.get(bean);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return result;
	}
	
	public static void setFieldValue(Object bean, String fieldName, Object value) {
		try {
			Field field = FieldUtils.getField(bean.getClass(), fieldName);
			field.setAccessible(true);
			field.set(bean, value);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public static Object newInstance(Class<?> cls) {
		Object obj = null;
		try {
			obj = cls.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return obj;
	}
}
