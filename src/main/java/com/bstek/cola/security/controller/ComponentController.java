package com.bstek.cola.security.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bstek.bdf3.security.orm.Component;
import com.bstek.cola.log.annotation.Log;
import com.bstek.cola.security.service.ComponentService;

/** 
* 
* @author bob.yang
* @since 2017年12月18日
*
*/
@RestController("cola.componentController")
@RequestMapping("/api")
@Transactional(readOnly = true)
public class ComponentController {

	@Autowired
	private ComponentService componentService;
		
	@RequestMapping(path = "/component/loadByRoleId/{roleId}/{urlId}", method = RequestMethod.GET)
	public List<Component> loadByRoleId(@PathVariable String roleId, @PathVariable String urlId) {
		return componentService.loadByRoleId(roleId, urlId);
	}
	
	@RequestMapping(path = "/component/load", method = RequestMethod.GET)
	public Page<Component> load(Pageable pageable, @RequestParam(name = "searchKey", required = false) String searchKey) {
		return componentService.load(pageable, searchKey);
	}
	
	@RequestMapping(path = "/component/remove", method = RequestMethod.POST)
	@Log(module = "组件管理", category = "系统日志", operation = "删除组件", source = "/component")
	@Transactional
	public void remove(@RequestParam String id) {
		componentService.remove(id);
	}
	
	@RequestMapping(path = "/component/add", method = RequestMethod.POST)
	@Log(module = "组件管理", category = "系统日志", operation = "新增组件", source = "/component")
	@Transactional
	public String add(@RequestBody Component component) {
		return componentService.add(component);
	}

	@RequestMapping(path = "/component/modify", method = RequestMethod.PUT)
	@Log(module = "组件管理", category = "系统日志", operation = "修改组件", source = "/component")
	@Transactional
	public void modify(@RequestBody Component component) {
		componentService.modify(component);
	}
	
	@RequestMapping(path = "/component/getUrlName", method = RequestMethod.GET)
	public String getUrlName(@RequestParam(required = false) String urlId) {
		return componentService.getUrlName(urlId);
	}
	
	@RequestMapping(path = "/component/loadByPath", method = RequestMethod.GET)
	@Transactional
	public List<Component> loadByPath(@RequestParam(required = false) String path) {
		return componentService.loadComponentsByPath(path);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(path = "/component/save", method = RequestMethod.POST)
	@Log(module = "组件权限", category = "系统日志", operation = "组件权限分配", source = "/componentallot")
	@Transactional
	public void save(@RequestBody Map<String, Object> params) {
		String roleId = (String) params.get("roleId");
        List<String> componentIds = (List<String>) params.get("componentIds");
        componentService.save(roleId, componentIds);
	}
	
}
