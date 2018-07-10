package com.bstek.cola.excel.filter;

import java.util.Map;

import javax.persistence.EntityManagerFactory;

/** 
* 
* @author bob.yang
* @since 2018年7月3日
*
*/
public interface EntityManagerFactoryFilter {

	void filter(Map<String, EntityManagerFactory> entityManagerFactory);
	
}
