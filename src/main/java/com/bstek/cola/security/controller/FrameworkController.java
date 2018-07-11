package com.bstek.cola.security.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bstek.bdf3.security.ContextUtils;
import com.bstek.bdf3.security.orm.Url;
import com.bstek.bdf3.security.service.UrlService;
import com.bstek.cola.log.annotation.Log;
import com.bstek.cola.security.service.FrameworkService;


/** 
* 
* @author bob.yang
* @since 2017年12月15日
*
*/
@Controller("cola.frameworkController")
public class FrameworkController {

	@Autowired
	private UrlService urlService;
	
	@Value("${bdf3.application.title:BSTEK BDF3 FRAME}")
	private String applicationTitle;
	
	@Value("${bdf3.application.name:BSTEK BDF3 FRAME}")
	private String applicationName;
	
	@Value("${bdf3.main.topBarBackground:#09C}")
	private String topBarBackground;
	
	@Value("${bdf3.main.topBarHoverBackground:rgba(255, 255, 255, 0.08)}")
	private String topBarHoverBackground;
	
	@Value("${bdf3.main.topLeftCornerBackground:#0087b4}")
	private String topLeftCornerBackground;
	
	@Value("${bdf3.main.topLeftCornerHoverBackground:#0087b4}")
	private String topLeftCornerHoverBackground;
	
	@Value("${bdf3.main.leftBarBackground:#22282e}")
	private String leftBarBackground;
	
	@Value("${bdf3.main.leftBarHoverBackground:#314253}")
	private String userCenterBackground;
	
	@Value("${bdf3.main.topBarColor:rgba(255, 255, 255, 0.9)}")
	private String topBarColor;
	
	@Value("${bdf3.main.topBarHoverColor:rgba(255, 255, 255, 0.08)}")
	private String topBarHoverColor;
	
	@Value("${bdf3.main.topBarColor:rgba(255, 255, 255, 0.7)}")
	private String topLeftCornerColor;
	
	@Value("${bdf3.main.topBarHoverColor:rgba(255, 255, 255, 0.08)}")
	private String topLeftCornerHoverColor;
	
	@Value("${bdf3.main.menuSearchColor:green}")
	private String menuSearchColor;
	
	@Value("${bdf3.main.menuSearchHoverColor:green}")
	private String menuSearchHoverColor;
	
	@Value("${bdf3.security.logoutPath:/logout}")
	private String logoutPath;
	
	@Value("${bdf3.message.disabled:true}")
	private boolean messageDisabled;
	
	@Value("${bdf3.task.disabled:true}")
	private boolean taskDisabled;
	
	@Value("${bdf3.message.longPollingTimeout:0}")
    private int longPollingTimeout;
	
	@Value("${bdf3.message.liveMessage:true}")
    private boolean liveMessage;
    
    @Value("${bdf3.message.longPollingInterval:15000}")
    private int longPollingInterval;
    
    @Value("${bdf3.message.pullPath:./api/message/pull}")
    private String messagePullPath;
    
    @Value("${bdf3.message.totallPullPath:./api/message/total/pull}")
    private String messageTotalPullPath;
    
    @Autowired
    private FrameworkService frameworkService;
	
	@RequestMapping("/")
	@Transactional
	@Log(module = "系统登录", category = "系统日志", operation = "登录系统", source = "/login")
	public String home() {
		return frameworkService.getHomePage();
	}
	
	@RequestMapping("/main") 
	public String main(Model model) {
		model.addAttribute("applicationTitle", applicationTitle);
		model.addAttribute("applicationName", applicationName);
		
		model.addAttribute("topBarBackground", topBarBackground);
		model.addAttribute("topBarHoverBackground", topBarHoverBackground);
		model.addAttribute("topLeftCornerBackground", topLeftCornerBackground);
		model.addAttribute("topLeftCornerHoverBackground", topLeftCornerHoverBackground);
		model.addAttribute("leftBarBackground", leftBarBackground);
		model.addAttribute("userCenterBackground", userCenterBackground);
		model.addAttribute("topBarColor", topBarColor);
		model.addAttribute("topBarHoverColor", topBarHoverColor);
		model.addAttribute("topLeftCornerColor", topLeftCornerColor);
		model.addAttribute("topLeftCornerHoverColor", topLeftCornerHoverColor);
		model.addAttribute("menuSearchColor", menuSearchColor);
		model.addAttribute("menuSearchHoverColor", menuSearchHoverColor);
		
		model.addAttribute("logoutPath", logoutPath.substring(1));
		model.addAttribute("messageDisabled", messageDisabled);
		model.addAttribute("taskDisabled", taskDisabled);
		model.addAttribute("longPollingTimeout", longPollingTimeout);
		model.addAttribute("liveMessage", liveMessage);
		model.addAttribute("longPollingInterval", longPollingInterval);
		model.addAttribute("messagePullPath", messagePullPath);
		model.addAttribute("messageTotalPullPath", messageTotalPullPath);
		model.addAttribute("loginUsername", ContextUtils.getLoginUsername());
		return frameworkService.getMainPage();
	}
	
	@RequestMapping("/login") 
	public String login(HttpSession session, Model model) {
		Exception e = (Exception) session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		model.addAttribute("applicationTitle", applicationTitle);
		model.addAttribute("applicationName", applicationName);
		if (e != null) {
			model.addAttribute("error", e.getLocalizedMessage());
		} else {
			model.addAttribute("error", "");
		}
		
		return frameworkService.getLoginPage();
	}
	
	@RequestMapping("/message") 
	public String message() {
		return frameworkService.getMessagePage();
	}
	
	@RequestMapping("/task") 
	public String task() {
		return frameworkService.getTaskPage();
	}
	
	@RequestMapping("/workbench") 
	public String workbench() {
		return frameworkService.getWorkbenchPage();
	}

	@RequestMapping("/user") 
	public String user(Model model) {
		return frameworkService.getUserPage(model);
	}
	
	@RequestMapping("/role") 
	public String role(Model model) {
		return frameworkService.getRolePage(model);
	}
	
	@RequestMapping("/fileinfo") 
	public String fileinfo(Model model) {
		return frameworkService.getFileinfoPage(model);
	}
	
	@RequestMapping("/roleallot") 
	public String roleAllot(Model model) {
		return frameworkService.getRoleAllotPage(model);
	}
	
	@RequestMapping("/permission") 
	public String permission(Model model) {
		return frameworkService.getPermissionPage(model);
	}
	
	@RequestMapping("/url") 
	public String url(Model model) {
		return frameworkService.getUrlPage(model);
	}
	
	@RequestMapping("/component") 
	public String component(Model model) {
		return frameworkService.getComponentPage(model);
	}
	
	@RequestMapping("/componentallot") 
	public String componentAllot(Model model) {
		return frameworkService.getComponentAllotPage(model);
	}
	
	@RequestMapping("/loginfo") 
	public String logInfo(Model model) {
		return frameworkService.getLogInfoPage(model);
	}
	
	@RequestMapping("/dictionary") 
	public String dictionary(Model model) {
		return frameworkService.getDictionaryPage(model);
	}
	
	@RequestMapping("/excel") 
	public String excel(Model model) {
		return frameworkService.getExcelPage(model);
	}
	
	@RequestMapping("/exists") 
	public String exists() {
		return frameworkService.getExistsPage();
	}
	
	@RequestMapping("/exportError") 
	public String exportError() {
		return frameworkService.getExportErrorPage();
	}

	@RequestMapping("/api/menus")
	@ResponseBody
	@Transactional(readOnly = true)
	public List<Url> loadUrlsByCurrentUser(Authentication authentication) {
		UserDetails user = (UserDetails) authentication.getPrincipal();
		return urlService.findTreeByUsername(user.getUsername());
	}
	
	@RequestMapping("/api/user/detail")
	@ResponseBody
	@Transactional(readOnly = true)
	public UserDetails getLoginUser(){
		return frameworkService.getLoginUserInfo();
	}
	
//	@RequestMapping(path = "/api/message/total/pull", method = RequestMethod.GET)
//	@ResponseBody
//	@Transactional
//	public Long getMessageTotal(Authentication authentication) {
//		UserDetails user = (UserDetails) authentication.getPrincipal();
//		return frameworkService.getMessageTotal(user.getUsername());
//	}
	
//	@RequestMapping(path = "/api/message/pull", method = RequestMethod.GET)
//	@ResponseBody
//	@Transactional
//	public List<Notify> getMessages(Authentication authentication) {
//		UserDetails user = (UserDetails) authentication.getPrincipal();
//		return frameworkService.getMessages(user.getUsername());
//	}
}
