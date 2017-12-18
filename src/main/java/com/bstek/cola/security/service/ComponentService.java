package com.bstek.cola.security.service;

import java.util.List;

import com.bstek.bdf3.security.orm.Component;

/** 
* 
* @author bob.yang
* @since 2017年12月18日
*
*/
public interface ComponentService {

	List<Component> load(String roleId, String urlId);

	void remove(String id);

	String add(Component component);

	void modify(Component component);

	List<Component> loadComponentsByPath(String path);

}