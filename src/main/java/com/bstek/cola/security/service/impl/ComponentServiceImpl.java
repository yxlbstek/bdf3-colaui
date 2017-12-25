package com.bstek.cola.security.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.malagu.linq.JpaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bstek.bdf3.security.access.provider.ComponentProvider;
import com.bstek.bdf3.security.cache.SecurityCacheEvict;
import com.bstek.bdf3.security.decision.manager.SecurityDecisionManager;
import com.bstek.bdf3.security.orm.Component;
import com.bstek.bdf3.security.orm.ComponentType;
import com.bstek.bdf3.security.orm.Permission;
import com.bstek.bdf3.security.orm.Url;
import com.bstek.cola.security.service.ComponentService;

/** 
* 
* @author bob.yang
* @since 2017年12月18日
*
*/
@Service("cola.componentService")
public class ComponentServiceImpl implements ComponentService {

	@Autowired
	private ComponentProvider componentProvider;
	
	@Autowired
	private SecurityDecisionManager securityDecisionManager;

	@Override
	public List<Component> loadByRoleId(String roleId, String urlId) {
		List<Component> components = JpaUtil.linq(Component.class).equal("urlId", urlId).list();
		if (!components.isEmpty()) {
			Set<String> ids = JpaUtil.collectId(components);
			
			List<Permission> permissions = JpaUtil.linq(Permission.class)
				.equal("roleId", roleId)
				.equal("resourceType", Component.RESOURCE_TYPE)
				.in("resourceId", ids)
				.list();
			
			Map<String, Permission> permissionMap = JpaUtil.index(permissions, "resourceId");

			for (Component component : components) {
				component.setComponentType(ComponentType.ReadWrite);
				Permission permission = permissionMap.get(component.getId());
				if (permission != null) {
					component.setComponentType(ComponentType.Read); 
					component.setAuthorized(true);
					component.setConfigAttributeId(permission.getId());
				}
			}
		}
		return components;
	}
	

	@Override
	public Page<Component> load(Pageable pageable, String searchKey) {
		return JpaUtil
			.linq(Component.class)
			.addIf(searchKey)
			.or()
				.like("name", "%" + searchKey + "%")
				.like("componentId", "%" + searchKey + "%")
				.like("path", "%" + searchKey + "%")
			.endIf()
			.asc("path")
			.paging(pageable);
	}

	@Override
	@SecurityCacheEvict
	public void remove(String id) {
		Component component = JpaUtil.getOne(Component.class, id);
		JpaUtil.remove(component);
		JpaUtil.lind(Permission.class)
			.equal("resourceType", Component.RESOURCE_TYPE)
			.equal("resourceId", id)
			.delete();
	}

	@Override
	public String add(Component component) {
		component.setId(UUID.randomUUID().toString());
		JpaUtil.persist(component);
		return component.getId();
	}

	@Override
	@SecurityCacheEvict
	public void modify(Component component) {
		JpaUtil.merge(component);
	}

	@Override
	public List<Component> loadComponentsByPath(String path) {
		List<Component> result = new ArrayList<Component>();
		Map<String, Collection<Component>> componentMap = componentProvider.provide();
		Collection<Component> components = componentMap.get(path);
		if (components != null) {
			for (Component c : components) {
			Component component = new Component();
			component.setComponentId(c.getComponentId());
			component.setPath(c.getPath());
			component.setAttributes(c.getAttributes());
			component.setComponentType(ComponentType.ReadWrite);
			if (securityDecisionManager.decide(component)) {
				component.setAuthorized(true);
			} else {
				component.setComponentType(ComponentType.Read);
				if (securityDecisionManager.decide(component)) {
					component.setAuthorized(true);
				}
			}
			component.setAttributes(null);
			result.add(component);
		}
		}
		
		return result;
	}


	@Override
	public String getUrlName(String urlId) {
		List<Url> urls = JpaUtil.linq(Url.class).equal("id", urlId).list();
		if (urls.size() > 0) {
			return urls.get(0).getName();
		}
		return null;
	}


	@Override
	public void save(String roleId, List<String> componentIds) {
		for (String componentId : componentIds) {
			List<Permission> permissions = JpaUtil
				.linq(Permission.class)
				.equal("resourceType", Component.RESOURCE_TYPE)
				.equal("roleId", roleId)
				.equal("resourceId", componentId)
				.list();
			if (permissions.size() > 0) {
				JpaUtil
					.lind(Permission.class)
					.equal("roleId", roleId)
					.equal("resourceId", componentId)
					.delete();
			} else {
				Permission permission = new Permission();
				permission.setId(UUID.randomUUID().toString());
				permission.setRoleId(roleId);
				permission.setResourceId(componentId);
				permission.setResourceType(Component.RESOURCE_TYPE);
				permission.setAttribute("ROLE_" + roleId);
				JpaUtil.persist(permission);
			}
		}
		
	}

}
