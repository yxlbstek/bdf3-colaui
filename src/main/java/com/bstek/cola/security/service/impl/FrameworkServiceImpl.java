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
		int status = decide("./user");
		if (status == 200) {
			return "bdf3/user";
		}
		model.addAttribute("status", status);
		return "frame/error";
	}

	@Override
	public String getUrlPage(Model model) {
		int status = decide("./url");
		if (status == 200) {
			return "bdf3/url";
		}
		model.addAttribute("status", status);
		return "frame/error";	
	}

	@Override
	public String getRolePage(Model model) {
		int status = decide("./role");
		if (status == 200) {
			return "bdf3/role";
		}
		model.addAttribute("status", status);
		return "frame/error";
	}
	
	@Override
	public String getRoleAllotPage(Model model) {
		int status = decide("./roleallot");
		if (status == 200) {
			return "bdf3/roleallot";
		}
		model.addAttribute("status", status);
		return "frame/error";
	}

	@Override
	public String getPermissionPage(Model model) {
		int status = decide("./permission");
		if (status == 200) {
			return "bdf3/permission";
		}
		model.addAttribute("status", status);
		return "frame/error";
	}

	@Override
	public String getLogInfoPage(Model model) {
		int status = decide("./loginfo");
		if (status == 200) {
			return "bdf3/loginfo";
		}
		model.addAttribute("status", status);
		return "frame/error";
	}

	@Override
	public String getComponentPage(Model model) {
		int status = decide("./component");
		if (status == 200) {
			return "bdf3/component";
		}
		model.addAttribute("status", status);
		return "frame/error";
	}
	
	@Override
	public String getComponentAllotPage(Model model) {
		int status = decide("./componentallot");
		if (status == 200) {
			return "bdf3/componentallot";
		}
		model.addAttribute("status", status);
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
	
	public int decide(String path) {
		User user = (User)getLoginUserInfo();
		List<Url> urls = JpaUtil.linq(Url.class).equal("path", path).list();
		if (urls.size() > 0) {
			if (userService.isAdministrator(user.getUsername())) {
				return 200;
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
					return 200;
				}
				return 403;
			}
			return 403;
		}
		return 404;
	}

	
}
