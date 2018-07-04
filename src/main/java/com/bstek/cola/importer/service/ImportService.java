package com.bstek.cola.importer.service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


/** 
* 
* @author bob.yang
* @since 2018年7月4日
*
*/
@RestController("cola.importController")
@RequestMapping("/importExcel")
public class ImportService {

	@RequestMapping("/import")
	@ResponseBody
	@Transactional
	public String importExcel(@RequestParam(value = "file") MultipartFile file, @RequestParam(value = "importerSolutionId") String importerSolutionId, @RequestParam(value = "startRow") String startRow) throws Exception {
		
		return null;
	}

	
}
