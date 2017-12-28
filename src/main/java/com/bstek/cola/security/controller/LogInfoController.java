package com.bstek.cola.security.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bstek.cola.orm.LogInfo;
import com.bstek.cola.security.service.LogInfoService;

/** 
* 
* @author bob.yang
* @since 2017年12月15日
*
*/

@RestController("cola.logInfoController")
@Transactional(readOnly = true)
@RequestMapping("/api")
public class LogInfoController {

	@Autowired
	private LogInfoService logInfoService;
	
	@RequestMapping(path = "/logInfo/load", method = RequestMethod.GET)
	public Page<LogInfo> load(Pageable pageable, @RequestParam(name = "searchKey", required = false) String searchKey) {
		return logInfoService.load(pageable, searchKey);
	}

}
