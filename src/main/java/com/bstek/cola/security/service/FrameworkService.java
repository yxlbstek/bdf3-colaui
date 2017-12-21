package com.bstek.cola.security.service;


import org.springframework.security.core.userdetails.UserDetails;


/** 
* 
* @author bob.yang
* @since 2017年12月15日
*
*/
public interface FrameworkService {

	String getHomePage();

	String getLoginPage();

	String getMainPage();

	String getUserPage();

	String getUrlPage();

	String getRolePage();

	String getComponentPage();

	String getMePage();

	UserDetails getLoginUserInfo();

	String getRoleAllotPage();

	String getPermissionPage();

	//Long getMessageTotal(String username);

	//List<Notify> getMessages(String username);

}