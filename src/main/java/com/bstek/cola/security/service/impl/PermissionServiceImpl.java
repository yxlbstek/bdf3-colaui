package com.bstek.cola.security.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.bstek.bdf3.jpa.JpaUtil;
import com.bstek.bdf3.security.cache.SecurityCacheEvict;
import com.bstek.bdf3.security.orm.Permission;
import com.bstek.cola.security.service.PermissionService;

/** 
* 
* @author bob.yang
* @since 2017年12月18日
*
*/
@Service("cola.permissionService")
public class PermissionServiceImpl implements PermissionService {

	@Override
	@SecurityCacheEvict
	public void remove(String id) {
		Permission permission = JpaUtil.getOne(Permission.class, id);
		JpaUtil.remove(permission);
	}

	@Override
	@SecurityCacheEvict
	public void remove(String roleId, String resourceId) {
		JpaUtil.lind(Permission.class)
			.equal("roleId", roleId)
			.equal("resourceId", resourceId)
			.delete();
	}

	@Override
	@SecurityCacheEvict
	public String add(Permission permission) {
		permission.setId(UUID.randomUUID().toString());
		JpaUtil.persist(permission);
		return permission.getId();
	}

	@Override
	@SecurityCacheEvict
	public void modify(Permission permission) {
		JpaUtil.merge(permission);
	}
	
	@Override
	public void save(String roleId, List<String> urlIds, List<String> excludeUrlIds) {
		Permission permission = null;
		List<Permission> pers = null;
		for (String urlId : urlIds) { // 选中的节点(新选中或末改变)
			pers = JpaUtil
				.linq(Permission.class)
				.equal("roleId", roleId)
				.equal("resourceId", urlId)
				.list();
			if (pers.size() > 0) continue;
			permission = new Permission();
			permission.setId(UUID.randomUUID().toString());
			permission.setRoleId(roleId);
			permission.setResourceId(urlId);
			permission.setResourceType("URL");
			JpaUtil.persist(permission);
		}

		for (String excludeUrlId : excludeUrlIds) { // 末选中的节点(原选中后取消,或从未选中)
			pers = JpaUtil
				.linq(Permission.class)
				.equal("roleId", roleId)
				.equal("resourceId", excludeUrlId)
				.list();
			if (pers.size() == 0) continue;
			JpaUtil
				.lind(Permission.class)
				.equal("roleId", roleId)
				.equal("resourceId", excludeUrlId)
				.delete();
		}
	}
}
