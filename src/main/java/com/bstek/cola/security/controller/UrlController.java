package com.bstek.cola.security.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bstek.bdf3.security.orm.Url;
import com.bstek.cola.log.annotation.Log;
import com.bstek.cola.security.service.UrlService;


/** 
* 
* @author bob.yang
* @since 2017年12月15日
*
*/




@RestController("cola.urlController")
@RequestMapping("/api")
@Transactional(readOnly = true)
public class UrlController {

	@Autowired
	private UrlService urlService;
	
	@RequestMapping(path = "/url/load-tree", method = RequestMethod.GET)
	public List<Url> loadTree() {
		return urlService.loadTree();
	}
	
	@RequestMapping(path = "/url/loadAll", method = RequestMethod.GET)
	public List<Url> loadAll() {
		return urlService.loadAll();
	}
	
	@RequestMapping(path = "/url/loadTreeByRoleId/{roleId}", method = RequestMethod.GET)
	public List<Url> loadTreeByRoleId(@PathVariable("roleId") String roleId) {
		return urlService.loadTreeByRoleId(roleId);
	}
	
	@RequestMapping(path = "/url/loadTop", method = RequestMethod.GET)
	public List<Url> loadTop() {
		return urlService.loadTop();
	}
	
	@RequestMapping(path = "/url/loadSub", method = RequestMethod.GET)
	public List<Url> loadSub(@RequestParam(required=false) String parentId) {
		return urlService.loadSub(parentId);
	}
	
	@RequestMapping(path = "/url/load", method = RequestMethod.GET)
	public List<Url> load() {
		return urlService.load();
	}
	
	@RequestMapping(path = "/url/remove", method = RequestMethod.POST)
	@Log(module = "菜单管理", category = "系统日志", operation = "删除菜单", source = "/url")
	@Transactional
	public void remove(@RequestParam String id) {
		urlService.remove(id);
	}
	
	@RequestMapping(path = "/url/add", method = RequestMethod.POST)
	@Log(module = "菜单管理", category = "系统日志", operation = "新增菜单", source = "/url")
	@Transactional
	public String add(@RequestBody Url url) {
		return urlService.add(url);
	}

	@RequestMapping(path = "/url/modify", method = RequestMethod.PUT)
	@Log(module = "菜单管理", category = "系统日志", operation = "修改菜单", source = "/url")
	@Transactional
	public void modify(@RequestBody Url url) {
		urlService.modify(url);
	}
	
	@RequestMapping(path = "/url/exist", method = RequestMethod.GET)
	public boolean validate(@RequestParam String name) {
		return !urlService.isExist(name);
	}
	
}
