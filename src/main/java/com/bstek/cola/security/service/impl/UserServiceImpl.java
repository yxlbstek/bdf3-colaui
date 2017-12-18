package com.bstek.cola.security.service.impl;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bstek.bdf3.jpa.JpaUtil;
import com.bstek.bdf3.security.cache.SecurityCacheEvict;
import com.bstek.bdf3.security.orm.RoleGrantedAuthority;
import com.bstek.bdf3.security.orm.User;
import com.bstek.cola.security.service.UserService;

/** 
* 
* @author bob.yang
* @since 2017年12月15日
*
*/
@Service("cola.userService")
public class UserServiceImpl implements UserService {

	@Autowired
	protected PasswordEncoder passwordEncoder;

	@Override
	public Page<User> load(Pageable pageable, String searchKey) {		
		return JpaUtil.linq(User.class).paging(pageable);
	}

	@Override
	public void remove(String id) {

	}

	@Override
	@Transactional
	public void add(Map<String, Object> user) throws Exception {

	}

	@Override
	public void modify(Map<String, Object> user) throws Exception {

	}

	@Override
	public void changePassword(String username, String newPassword) {

	}

	@Override
	public boolean validatePassword(String username, String password) {

		return false;
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isExist(String username) {
		return true;
	}

	@Override
	@SecurityCacheEvict
	public String addRoleGrantedAuthority(RoleGrantedAuthority roleGrantedAuthority) {
		roleGrantedAuthority.setId(UUID.randomUUID().toString());
		JpaUtil.persist(roleGrantedAuthority);
		return roleGrantedAuthority.getId();
		
	}

	@Override
	@SecurityCacheEvict
	public void removeRoleGrantedAuthority(String id) {
		RoleGrantedAuthority roleGrantedAuthority = JpaUtil.getOne(RoleGrantedAuthority.class, id);
		JpaUtil.remove(roleGrantedAuthority);
	}

	@Override
	public void modifyNickname(String username, String nickname) {
		
	}
}
