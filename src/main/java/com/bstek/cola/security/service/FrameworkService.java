package com.bstek.cola.security.service;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;


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

	String getUserPage(Model model);

	String getUrlPage(Model model);

	String getRolePage(Model model);

	String getComponentPage(Model model);

	UserDetails getLoginUserInfo();

	String getRoleAllotPage(Model model);

	String getPermissionPage(Model model);

	String getComponentAllotPage(Model model);

	String getLogInfoPage(Model model);

	String getDictionaryPage(Model model);

	String getMessagePage();

	String getTaskPage();

	String getWorkbenchPage();

	String getFileinfoPage(Model model);

	String getExistsPage();

	String getExcelPage(Model model);


	//Long getMessageTotal(String username);

	//List<Notify> getMessages(String username);

}