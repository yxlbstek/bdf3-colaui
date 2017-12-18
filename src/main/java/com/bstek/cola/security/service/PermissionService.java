package com.bstek.cola.security.service;

import com.bstek.bdf3.security.orm.Permission;

/** 
* 
* @author bob.yang
* @since 2017年12月18日
*
*/
public interface PermissionService {

	void remove(String id);

	void remove(String roleId, String resourceId);

	String add(Permission permission);

	void modify(Permission permission);

}