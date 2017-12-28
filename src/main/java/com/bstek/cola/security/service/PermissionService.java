package com.bstek.cola.security.service;

import java.util.List;


/** 
* 
* @author bob.yang
* @since 2017年12月18日
*
*/
public interface PermissionService {

	void save(String roleId, List<String> urlIds, List<String> excludeUrlIds);

}