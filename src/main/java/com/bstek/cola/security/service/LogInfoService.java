package com.bstek.cola.security.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bstek.cola.orm.LogInfo;

/** 
* 
* @author bob.yang
* @since 2017年12月15日
*
*/
public interface LogInfoService {

	Page<LogInfo> load(Pageable pageable, String searchKey);
}