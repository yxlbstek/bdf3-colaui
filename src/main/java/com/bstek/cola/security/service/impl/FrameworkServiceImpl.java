package com.bstek.cola.security.service.impl;

import java.util.List;

import org.malagu.linq.JpaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.bstek.bdf3.security.decision.manager.SecurityDecisionManager;
import com.bstek.bdf3.security.orm.Url;
import com.bstek.bdf3.security.orm.User;
import com.bstek.bdf3.security.service.UserService;
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
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private SecurityDecisionManager securityDecisionManager;
	
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
		return "bdf3/user";
	}

	@Override
	public String getUrlPage() {
		return "bdf3/url";
	}

	@Override
	public String getRolePage() {
		boolean auth = decide("./role");
		if (auth) {
			return "bdf3/role";
		}
		return "页面权限不足！";
	}
	
	@Override
	public String getRoleAllotPage() {
		return "bdf3/roleallot";
	}
	

	@Override
	public String getPermissionPage() {
		return "bdf3/permission";
	}


	@Override
	public String getComponentPage() {
		return "bdf3/component";
	}
	
	@Override
	public String getComponentAllotPage() {
		return "bdf3/componentallot";
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
	
	private boolean decide(String path) {
		User user = (User)getLoginUserInfo();
		List<Url> urls = JpaUtil.linq(Url.class).equal("path", path).list();
		boolean administrator = userService.isAdministrator(user.getUsername());
		if (administrator || securityDecisionManager.decide(user.getUsername(), urls.get(0))) {
			return true;
		}
		return false;
	}
	
	
}
