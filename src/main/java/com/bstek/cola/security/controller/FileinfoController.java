package com.bstek.cola.security.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bstek.cola.log.annotation.Log;
import com.bstek.cola.orm.FileInfo;
import com.bstek.cola.security.service.FileinfoService;

/** 
* 
* @author bob.yang
* @since 2017年12月18日
*
*/
@RestController("cola.fileinfoController")
@RequestMapping("/api")
@Transactional(readOnly = true)
public class FileinfoController {

	@Autowired
	private FileinfoService fileinfoService;
		
	@RequestMapping(path = "/fileinfo/load", method = RequestMethod.GET)
	public Page<FileInfo> load(Pageable pageable, @RequestParam(name = "searchKey", required = false) String searchKey) {
		return fileinfoService.load(pageable, searchKey);
	}
	
	@RequestMapping(path = "/fileinfo/remove", method = RequestMethod.POST)
	@Log(module = "文件管理", category = "系统日志", operation = "删除文件", source = "/fileinfo")
	@Transactional
	public void remove(@RequestParam String id) {
		fileinfoService.remove(id);
	}

	
}
