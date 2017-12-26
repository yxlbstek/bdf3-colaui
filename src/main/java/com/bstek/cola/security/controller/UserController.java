package com.bstek.cola.security.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bstek.bdf3.security.orm.RoleGrantedAuthority;
import com.bstek.bdf3.security.orm.User;
import com.bstek.cola.security.service.UserService;

/** 
* 
* @author bob.yang
* @since 2017年12月15日
*
*/

@RestController("cola.userController")
@Transactional(readOnly = true)
@RequestMapping("/api")
public class UserController {

	@Autowired
	private UserService userService;
	
	@RequestMapping(path = "/user/load", method = RequestMethod.GET)
	public Page<User> load(Pageable pageable, @RequestParam(name = "searchKey", required = false) String searchKey) {
		return userService.load(pageable, searchKey);
	}
	
	@RequestMapping(path = "/user/remove", method = RequestMethod.POST)
	@Transactional
	public void remove(@RequestParam String username) {
		userService.remove(username);
	}
	
	@RequestMapping(path = "/user/add", method = RequestMethod.POST)
	@Transactional
	public void add(@RequestBody User user) throws Exception {
		userService.add(user);
	}

	@RequestMapping(path = "/user/modify", method = RequestMethod.PUT)
	@Transactional
	public void modify(@RequestBody User user) throws Exception {
		userService.modify(user);
	}
	
	@RequestMapping(path = "/user/exist", method = RequestMethod.GET)
	public boolean validate(@RequestParam String username) {
		return !userService.isExist(username);
	}
	
	@RequestMapping(path = "/user/validatePassword", method = RequestMethod.GET)
	public boolean validatePassword(@RequestParam String oldPassword) {
		User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return userService.validatePassword(user.getUsername(), oldPassword);
	}
	
	@RequestMapping(path = "/user/changePassword", method = RequestMethod.GET)
	public void changePassword(@RequestParam String newPassword) {
		User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		userService.changePassword(user.getUsername(), newPassword);
	}
	
	@RequestMapping(path = "/user/resetPassword", method = RequestMethod.PUT)
	@Transactional
	public void resetPassword(@RequestBody User user) {
		userService.resetPassword(user);
	}
	
	@RequestMapping(path = "/authority/add", method = RequestMethod.POST)
	@Transactional
	public String addRoleGrantedAuthority(@RequestBody RoleGrantedAuthority roleGrantedAuthority) {
		return userService.addRoleGrantedAuthority(roleGrantedAuthority);
		
	}
	
	@RequestMapping(path = "/authority/remove/{id}", method = RequestMethod.DELETE)
	@Transactional
	public void removeRoleGrantedAuthority(@PathVariable String id) {
		userService.removeRoleGrantedAuthority(id);
	}
}
