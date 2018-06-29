package com.bstek.cola.security.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bstek.cola.orm.FileInfo;

/** 
* 
* @author bob.yang
* @since 2017年12月18日
*
*/
public interface FileinfoService {

	Page<FileInfo> load(Pageable pageable, String searchKey);

	void remove(String id);


}