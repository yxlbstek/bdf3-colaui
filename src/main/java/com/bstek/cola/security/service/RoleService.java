package com.bstek.cola.security.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bstek.bdf3.security.orm.Role;
import com.bstek.bdf3.security.orm.Url;
import com.bstek.bdf3.security.orm.User;

/** 
* 
* @author bob.yang
* @since 2017年12月18日
*
*/
public interface RoleService {

	Page<Role> load(Pageable pageable, String searchKey);

	List<Role> load(String username);

	List<Url> loadUrls(String roleId);

	void remove(String id);

	String add(Role role);

	void modify(Role role);

	boolean isExist(String name);

	Page<User> loadNotAllotUser(Pageable pageable, String searchKey, String roleId);

	Page<User> loadIsAllotUser(Pageable pageable, String searchKey, String roleId);

	void addRoleUser(String roleId, String actorId);

	void removeRoleUser(String roleId, String actorId);

}