package com.bstek.cola.security.service.impl;

import java.util.List;
import java.util.Set;

import org.malagu.linq.JpaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.bstek.bdf3.security.ContextUtils;
import com.bstek.bdf3.security.orm.Permission;
import com.bstek.bdf3.security.orm.Role;
import com.bstek.bdf3.security.orm.RoleGrantedAuthority;
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
	public String getUserPage(Model model) {
		boolean result = decide("./user");
		if (result) {
			return "bdf3/user";
		}
		model.addAttribute("status", 403);
		return "frame/error";
	}

	@Override
	public String getUrlPage(Model model) {
		boolean result = decide("./url");
		if (result) {
			return "bdf3/url";
		}
		model.addAttribute("status", 403);
		return "frame/error";	
	}

	@Override
	public String getRolePage(Model model) {
		boolean result = decide("./role");
		if (result) {
			return "bdf3/role";
		}
		model.addAttribute("status", 403);
		return "frame/error";
	}
	
	@Override
	public String getRoleAllotPage(Model model) {
		boolean result = decide("./roleallot");
		if (result) {
			return "bdf3/roleallot";
		}
		model.addAttribute("status", 403);
		return "frame/error";
	}

	@Override
	public String getPermissionPage(Model model) {
		boolean result = decide("./permission");
		if (result) {
			return "bdf3/permission";
		}
		model.addAttribute("status", 403);
		return "frame/error";
	}

	@Override
	public String getLogInfoPage(Model model) {
		boolean result = decide("./loginfo");
		if (result) {
			return "bdf3/loginfo";
		}
		model.addAttribute("status", 403);
		return "frame/error";
	}
	

	@Override
	public String getDictionaryPage(Model model) {
		boolean result = decide("./dictionary");
		if (result) {
			return "bdf3/dictionary";
		}
		model.addAttribute("status", 403);
		return "frame/error";
	}

	@Override
	public String getComponentPage(Model model) {
		boolean result = decide("./component");
		if (result) {
			return "bdf3/component";
		}
		model.addAttribute("status", 403);
		return "frame/error";
	}
	
	@Override
	public String getComponentAllotPage(Model model) {
		boolean result = decide("./componentallot");
		if (result) {
			return "bdf3/componentallot";
		}
		model.addAttribute("status", 403);
		return "frame/error";
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
	
	public boolean decide(String path) {
		User user = ContextUtils.getLoginUser();
		List<Url> urls = JpaUtil.linq(Url.class).equal("path", path).list();
		if (userService.isAdministrator(user.getUsername())) {
			return true;
		}
		List<Role> roles = JpaUtil
			.linq(Role.class)
			.in(Permission.class)
				.select("roleId")
				.equal("resourceId", urls.get(0).getId())
				.equal("resourceType", Url.RESOURCE_TYPE)
			.end()
			.list();
		if (roles.size() > 0) {
			Set<String> roleIds = JpaUtil.collectId(roles);
			List<RoleGrantedAuthority> authorities = JpaUtil
				.linq(RoleGrantedAuthority.class)
				.equal("actorId", user.getUsername())
				.in("roleId", roleIds)
				.list();
			if (authorities.size() > 0) {
				return true;
			}
			return false;
		}
		return false;
	}


	
}
