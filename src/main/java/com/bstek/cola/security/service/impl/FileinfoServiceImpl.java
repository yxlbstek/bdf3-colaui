package com.bstek.cola.security.service.impl;

import java.io.File;

import org.malagu.linq.JpaUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bstek.cola.orm.FileInfo;
import com.bstek.cola.security.service.FileinfoService;

/** 
* 
* @author bob.yang
* @since 2017年12月18日
*
*/
@Service("cola.fileinfoService")
public class FileinfoServiceImpl implements FileinfoService {

	@Override
	public Page<FileInfo> load(Pageable pageable, String searchKey) {
		return JpaUtil
				.linq(FileInfo.class)
				.addIf(searchKey)
					.or()
						.like("type", "%" + searchKey + "%")
						.like("name", "%" + searchKey + "%")
				.endIf()
				.desc("createDate")
				.paging(pageable);
	}

	@Override
	public void remove(String id) {
		FileInfo fileinfo = JpaUtil.getOne(FileInfo.class, id);
		JpaUtil.remove(fileinfo);
		
		File file = new File(fileinfo.getPath());
		if (file.exists()) {
			file.delete();
		}
	}

	
}
