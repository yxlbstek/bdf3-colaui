package com.bstek.cola.security.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bstek.cola.orm.Dictionary;
import com.bstek.cola.orm.DictionaryItem;

/** 
* 
* @author bob.yang
* @since 2017年12月15日
*
*/
public interface DictionaryService {

	Page<Dictionary> loadDictionaries(Pageable pageable, String searchKey);

	void removeDictionary(String id);

	void addDictionary(Dictionary dictionary);

	void modifyDictionary(Dictionary dictionary);

	boolean isExistCode(String code);

	Page<DictionaryItem> loadDictionaryItems(Pageable pageable, String dictionaryId, String searchKey);

	void removeDictionaryItem(String id);

	void addDictionaryItem(DictionaryItem dictionaryItem);

	void modifyDictionaryItem(DictionaryItem dictionaryItem);

	boolean isExistKey(String key);


}