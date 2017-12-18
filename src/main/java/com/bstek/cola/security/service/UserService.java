package com.bstek.cola.security.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bstek.bdf3.security.orm.RoleGrantedAuthority;
import com.bstek.bdf3.security.orm.User;

/** 
* 
* @author bob.yang
* @since 2017年12月15日
*
*/
public interface UserService {

	Page<User> load(Pageable pageable, String searchKey);

	void remove(String id);

	void add(User user) throws Exception;

	void modify(User user) throws Exception;

	void changePassword(String username, String newPassword);

	boolean validatePassword(String username, String password);

	boolean isExist(String username);

	String addRoleGrantedAuthority(RoleGrantedAuthority roleGrantedAuthority);

	void removeRoleGrantedAuthority(String id);

	void modifyNickname(String username, String nickname);

	void resetPassword(User user);

}