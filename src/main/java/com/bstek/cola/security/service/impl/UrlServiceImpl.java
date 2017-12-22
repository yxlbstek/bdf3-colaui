package com.bstek.cola.security.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.bstek.bdf3.jpa.JpaUtil;
import com.bstek.bdf3.security.cache.SecurityCacheEvict;
import com.bstek.bdf3.security.orm.Permission;
import com.bstek.bdf3.security.orm.Url;
import com.bstek.cola.security.service.UrlService;

/**
 * 
 * @author bob.yang
 * @since 2017年12月15日
 *
 */
@Service("cola.urlService")
public class UrlServiceImpl implements UrlService {

	@Override
	public List<Url> loadTree() {
		List<Url> result = new ArrayList<Url>();
		Map<String, List<Url>> childrenMap = new HashMap<String, List<Url>>();
		List<Url> urls = JpaUtil.linq(Url.class).asc("order").list();
		for (Url url : urls) {

			if (childrenMap.containsKey(url.getId())) {
				url.setChildren(childrenMap.get(url.getId()));
			} else {
				url.setChildren(new ArrayList<Url>());
				childrenMap.put(url.getId(), url.getChildren());
			}

			if (url.getParentId() == null) {
				result.add(url);
			} else {
				List<Url> children;
				if (childrenMap.containsKey(url.getParentId())) {
					children = childrenMap.get(url.getParentId());
				} else {
					children = new ArrayList<Url>();
					childrenMap.put(url.getParentId(), children);
				}
				children.add(url);
			}
		}
		return result;
	}

	@Override
	public List<Url> loadTreeByRoleId(String roleId) {
		List<Url> urls = loadTree();
		List<Permission> permissions = JpaUtil
				.linq(Permission.class)
				.equal("resourceType", "URL")
				.equal("roleId", roleId)
				.list();
		Set<String> roleUrlIds = JpaUtil.collect(permissions, "resourceId");
		checked(urls, roleUrlIds);
		return urls;
	}

	private void checked(List<Url> urls, Set<String> roleUrlIds) {
		if (urls.size() > 0) {
			for (Url url : urls) {
				if (roleUrlIds.size() > 0) {
					if (roleUrlIds.contains(url.getId())) {
						url.setNavigable(true);
					} else {
						url.setNavigable(false);
					}
					checked(url.getChildren(), roleUrlIds);
				} else {
					url.setNavigable(false);
					checked(url.getChildren(), roleUrlIds);
				}
			}
		}
		
	}


	@Override
	public List<Url> loadTop() {
		return JpaUtil.linq(Url.class).isNull("parentId").asc("order").list();
	}

	@Override
	public List<Url> loadSub(String parentId) {
		return JpaUtil.linq(Url.class).equal("parentId", parentId).asc("order").list();
	}

	@Override
	public List<Url> load() {
		return JpaUtil.linq(Url.class).asc("order").list();
	}

	@Override
	@SecurityCacheEvict
	public void remove(String id) {
		Url url = JpaUtil.getOne(Url.class, id);
		JpaUtil.remove(url);
	}

	@Override
	@SecurityCacheEvict
	public String add(Url url) {
		url.setId(UUID.randomUUID().toString());
		JpaUtil.persist(url);
		return url.getId();
	}

	@Override
	@SecurityCacheEvict
	public void modify(Url url) {
		JpaUtil.merge(url);
	}

	@Override
	public boolean isExist(String name) {
		boolean result = JpaUtil.linq(Url.class).equal("name", name).exists();
		return result;
	}

	@Override
	public List<Url> loadAll() {
		return JpaUtil
			.linq(Url.class)
			.asc("order")
			.list();
	}

}
