package com.bstek.cola.security.service.impl;


import java.util.UUID;

import org.malagu.linq.JpaUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bstek.cola.orm.Dictionary;
import com.bstek.cola.orm.DictionaryItem;
import com.bstek.cola.security.service.DictionaryService;


/** 
* 
* @author bob.yang
* @since 2017年12月15日
*
*/
@Service("cola.dictionaryService")
public class DictionaryServiceImpl implements DictionaryService {

	/*字典目录*/
	@Override
	public Page<Dictionary> loadDictionaries(Pageable pageable, String searchKey) {
		return JpaUtil
			.linq(Dictionary.class)
			.addIf(searchKey)
				.or()
					.like("code", "%" + searchKey + "%")
					.like("name", "%" + searchKey + "%")
				.end()
			.endIf()
			.desc("order")
			.paging(pageable);
	}

	@Override
	public void removeDictionary(String id) {
		JpaUtil.lind(Dictionary.class).equal("id", id).delete();		
	}

	@Override
	public void addDictionary(Dictionary dictionary) {
		dictionary.setId(UUID.randomUUID().toString());
		JpaUtil.persist(dictionary);
	}

	@Override
	public void modifyDictionary(Dictionary dictionary) {
		JpaUtil.merge(dictionary);		
	}

	@Override
	public boolean isExistCode(String code) {
		boolean result = JpaUtil.linq(Dictionary.class).equal("code", code).exists();
		return result;
	}
	
	
	/*字典项*/
	@Override
	public Page<DictionaryItem> loadDictionaryItems(Pageable pageable, String dictionaryId, String searchKey) {
		return JpaUtil
				.linq(DictionaryItem.class)
				.equal("dictionaryId", dictionaryId)
				.addIf(searchKey)
					.or()
						.like("code", "%" + searchKey + "%")
						.like("name", "%" + searchKey + "%")
					.end()
				.endIf()
				.desc("order")
				.paging(pageable);
	}

	@Override
	public void removeDictionaryItem(String id) {
		JpaUtil.lind(DictionaryItem.class).equal("id", id).delete();		
	}

	@Override
	public void addDictionaryItem(DictionaryItem dictionaryItem) {
		dictionaryItem.setId(UUID.randomUUID().toString());		
	}

	@Override
	public void modifyDictionaryItem(DictionaryItem dictionaryItem) {
		JpaUtil.merge(dictionaryItem);		
	}

	@Override
	public boolean isExistKey(String key) {
		boolean result = JpaUtil.linq(DictionaryItem.class).equal("key", key).exists();
		return result;
	}

}
