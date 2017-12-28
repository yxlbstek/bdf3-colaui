package com.bstek.cola.security.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bstek.cola.log.annotation.Log;
import com.bstek.cola.security.service.PermissionService;

/** 
* 
* @author bob.yang
* @since 2017年12月18日
*
*/
@RestController("cola.permissionController")
@RequestMapping("/api")
@Transactional(readOnly = true)
public class PermissionController {

	@Autowired
	private PermissionService permissionService;
	
	@SuppressWarnings("unchecked")
	@RequestMapping(path = "/permission/save", method = RequestMethod.POST)
	@Log(module = "菜单权限", category = "系统日志", operation = "分配菜单权限", source = "/permission")
	@Transactional
	public void save(@RequestBody Map<String, Object> params) {
		String roleId = (String) params.get("roleId");
        List<String> urlIds = (List<String>) params.get("urlIds");
        List<String> excludeUrlIds = (List<String>) params.get("excludeUrlIds");
		permissionService.save(roleId, urlIds, excludeUrlIds);
	}
}
