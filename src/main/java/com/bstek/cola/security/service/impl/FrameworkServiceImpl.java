package com.bstek.cola.security.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.bstek.cola.security.service.FrameworkService;

/** 
* 
* @author bob.yang
* @since 2017年12月15日
*
*/
@Service("cola.frameworkService")
public class FrameworkServiceImpl implements FrameworkService {

	//@Autowired
	//private NotifyService notifyService;
	
	@Value("${bdf3.security.loginSuccessPath:main}")
	private String loginSuccessPath;
	
	@Value("${bdf3.security.loginPath:frame/login}")
	protected String loginPath;
	

	@Override
	public String getHomePage() {
		return "redirect:" + loginSuccessPath;
	}

	@Override
	public String getLoginPage() {
		return loginPath;
	}

	@Override
	public String getMainPage() {
		return "frame/main";
	}

	@Override
	public String getUserPage() {
		return "bdf3/user_2";
	}

	@Override
	public String getUrlPage() {
		return "bdf3/url_2";
	}

	@Override
	public String getRolePage() {
		return "bdf3/role_2";
	}
	
	@Override
	public String getRoleAllotPage() {
		return "bdf3/roleallot_2";
	}
	

	@Override
	public String getPermissionPage() {
		return "bdf3/permission_2";
	}


	@Override
	public String getComponentPage() {
		return "bdf3/component_2";
	}

	@Override
	public String getMePage() {
		return "bdf3/me";
	}

	@Override
	public UserDetails getLoginUserInfo() {
		return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
	}

	
//	@Override
//	public Long getMessageTotal(String username) {
//		notifyService.pullAnnounce(username);
//		notifyService.pullRemind(username);
//		return notifyService.getUserNotifyCount(username);
//	}
//
//	@Override
//	public List<Notify> getMessages(String username) {
//		notifyService.pullAnnounce(username);
//		notifyService.pullRemind(username);
//		return notifyService.getUserNotify(username);
//	}
}
