package com.bstek.cola.security.controller;


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

import com.bstek.cola.log.annotation.Log;
import com.bstek.cola.orm.Dictionary;
import com.bstek.cola.orm.DictionaryItem;
import com.bstek.cola.security.service.DictionaryService;

/** 
* 
* @author bob.yang
* @since 2017年12月15日
*
*/

@RestController("cola.dictionaryController")
@Transactional(readOnly = true)
@RequestMapping("/api")
public class DictionaryController {

	@Autowired
	private DictionaryService dictionaryService;
	
	/*字典目录*/
	@RequestMapping(path = "/dictionary/load", method = RequestMethod.GET)
	public Page<Dictionary> loadDictionaries(Pageable pageable, @RequestParam(name = "searchKey", required = false) String searchKey) {
		return dictionaryService.loadDictionaries(pageable, searchKey);
	}
	
	@RequestMapping(path = "/dictionary/remove", method = RequestMethod.POST)
	@Log(module = "字典管理", category = "系统日志", operation = "删除字典目录", source = "/dictionary")
	@Transactional
	public void removeDictionary(@RequestParam String id) {
		dictionaryService.removeDictionary(id);
	}
	
	@RequestMapping(path = "/dictionary/add", method = RequestMethod.POST)
	@Log(module = "字典管理", category = "系统日志", operation = "新增字典目录", source = "/dictionary")
	@Transactional
	public void addDictionary(@RequestBody Dictionary dictionary) throws Exception {
		dictionaryService.addDictionary(dictionary);
	}

	@RequestMapping(path = "/dictionary/modify", method = RequestMethod.PUT)
	@Log(module = "字典管理", category = "系统日志", operation = "修改字典目录", source = "/dictionary")
	@Transactional
	public void modifyDictionary(@RequestBody Dictionary dictionary) throws Exception {
		dictionaryService.modifyDictionary(dictionary);
	}
	
	@RequestMapping(path = "/dictionary/exist", method = RequestMethod.GET)
	public boolean validateCode(@RequestParam String code) {
		return !dictionaryService.isExistCode(code);
	}
	
	/*字典项*/
	@RequestMapping(path = "/dictionaryItem/load/{dictionaryId}", method = RequestMethod.GET)
	public Page<DictionaryItem> loadDictionaryItems(Pageable pageable, @RequestParam(name = "searchKey", required = false) String searchKey, @PathVariable(name = "dictionaryId", required = false) String dictionaryId) {
		return dictionaryService.loadDictionaryItems(pageable, dictionaryId, searchKey);
	}
	
	@RequestMapping(path = "/dictionaryItem/remove", method = RequestMethod.POST)
	@Log(module = "字典管理", category = "系统日志", operation = "删除字典项", source = "/dictionary")
	@Transactional
	public void removeDictionaryItem(@RequestParam String id) {
		dictionaryService.removeDictionaryItem(id);
	}
	
	@RequestMapping(path = "/dictionaryItem/add", method = RequestMethod.POST)
	@Log(module = "字典管理", category = "系统日志", operation = "新增字典项", source = "/dictionary")
	@Transactional
	public void addDictionaryItem(@RequestBody DictionaryItem dictionaryItem) throws Exception {
		dictionaryService.addDictionaryItem(dictionaryItem);
	}

	@RequestMapping(path = "/dictionaryItem/modify", method = RequestMethod.PUT)
	@Log(module = "字典管理", category = "系统日志", operation = "修改字典项", source = "/dictionary")
	@Transactional
	public void modifyDictionaryItem(@RequestBody DictionaryItem dictionaryItem) throws Exception {
		dictionaryService.modifyDictionaryItem(dictionaryItem);
	}
	
	@RequestMapping(path = "/dictionaryItem/exist", method = RequestMethod.GET)
	public boolean validateKey(@RequestParam String key) {
		return !dictionaryService.isExistKey(key);
	}

}
