package com.bstek.cola.security.service;

import java.util.List;

import com.bstek.bdf3.security.orm.Url;

/** 
* 
* @author bob.yang
* @since 2017年12月16日
*
*/
public interface UrlService {

	List<Url> loadTree();

	List<Url> load();

	void remove(String id);

	String add(Url url);

	void modify(Url url);

}