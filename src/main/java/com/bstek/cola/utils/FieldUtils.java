package com.bstek.cola.utils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** 
* 
* @author bob.yang
* @since 2018年7月3日
*
*/
public class FieldUtils {

	public static List<Field> getFields(Class<?> clazz){
		if (clazz == null) {
	        throw new IllegalArgumentException("The class must not be null");
	    }
		List<Field> fieldList=new ArrayList<Field>();
		while(clazz!=null){
			Field[] fields=clazz.getDeclaredFields();
			for(Field field:fields){
				fieldList.add(field);
			}
			clazz=clazz.getSuperclass();
		}
		return fieldList;
	}
	
	public static Field getField(Class<?> clazz,String name){
		String[] ps = name.split("\\.");
		Class<?> cls = clazz;
		Field field = null;
		for (int i = 0; i < ps.length; i++) {
			field = doGetField(cls, ps[i]);
			cls = field.getType();
		}
		
		return field;
	}
	
	private static Field doGetField(Class<?> clazz,String name){
		while(clazz!=null){
			Field[] fields=clazz.getDeclaredFields();
			for(Field field:fields){
				if(field.getName().equals(name)){
					return field;
				}
			}
			clazz=clazz.getSuperclass();
		}
		return null;
	}
	
	public static Object convertType(Class<?> type, String cellValue) {
		Object result = null;
		String strType = type.getSimpleName();
		if (strType.equals("long")) {
			result = Long.parseLong(cellValue);
			
		} else if (strType.equals("Long")) {
			result = Long.valueOf(cellValue);
			
		} else if (strType.equals("int")) {
			result = Integer.parseInt(cellValue);
			
		} else if (strType.equals("Integer")) {
			result = Integer.valueOf(cellValue);
			
		} else if (strType.equals("float")) {
			result = Float.parseFloat(cellValue);
			
		} else if (strType.equals("Float")) {
			result = Float.valueOf(cellValue);
			
		} else if (strType.equals("double")) {
			result = Double.parseDouble(cellValue);
			
		} else if (strType.equals("Double")) {
			result = Double.valueOf(cellValue);
			
		} else if (strType.equals("BigDecimal")) {
			result = new BigDecimal(cellValue);
			
		} else if (strType.equals("boolean") || strType.equals("Boolean")) {
			result = cellValue.equals("0") || cellValue.equals("true") ? true : false;
			
		} else if (strType.equals("Date")) {
			result = DateUtils.stringToDate(cellValue, "yyyy-MM-dd HH:mm:ss");
			
		} else {
			result = cellValue;
			
		}
		return result;
	}
	
}
