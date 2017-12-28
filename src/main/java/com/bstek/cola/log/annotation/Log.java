package com.bstek.cola.log.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.commons.lang.StringUtils;

/** 
* 
* @author bob.yang
* @since 2017年12月28日
*
*/

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Log {

	String module() default StringUtils.EMPTY;
	
	String logger() default StringUtils.EMPTY;
	
	String title() default StringUtils.EMPTY;
	
	String desc() default StringUtils.EMPTY;
	
	String context() default StringUtils.EMPTY;
	
	String var() default StringUtils.EMPTY;
	
	String disabled() default StringUtils.EMPTY;
	
	String category() default StringUtils.EMPTY;
	
	String dataPath() default StringUtils.EMPTY;
	
	String source() default StringUtils.EMPTY;
		
	boolean recursive() default true;
	
	String operation() default StringUtils.EMPTY;
	
	String operationUser() default StringUtils.EMPTY;
	
	String operationUserNickname() default StringUtils.EMPTY;
	
}
