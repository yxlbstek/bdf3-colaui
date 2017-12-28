package com.bstek.cola.security.service.impl;

import org.malagu.linq.JpaUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bstek.cola.orm.LogInfo;
import com.bstek.cola.security.service.LogInfoService;

/** 
* 
* @author bob.yang
* @since 2017年12月15日
*
*/
@Service("cola.logInfoService")
public class LogInfoServiceImpl implements LogInfoService {

	@Override
	public Page<LogInfo> load(Pageable pageable, String searchKey) {
		return JpaUtil
			.linq(LogInfo.class)
			.addIf(searchKey)
				.or()
					.like("module", "%" + searchKey + "%")
					.like("operationUser", "%" + searchKey + "%")
				.end()
			.endIf()
			.desc("operationDate")
			.paging(pageable);
	}

}
