package com.bstek.cola.security.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.malagu.linq.JpaUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bstek.bdf3.security.ContextUtils;
import com.bstek.bdf3.security.orm.User;
import com.bstek.cola.orm.FileInfo;


/** 
* 
* @author bob.yang
* @since 2017年12月15日
*
*/

@RestController("cola.uploadController")
@RequestMapping("/fileUpload")
public class UploadService {
	
	@Value("${bdf3.upload.fileStoreDir:D:/upload}")
	private String fileStoreDir;

    @RequestMapping("/upload")
    @ResponseBody
    @Transactional
    public void fileUpload(@RequestParam(value = "files") MultipartFile[] files, HttpServletRequest request) throws Exception {
    	User user = ContextUtils.getLoginUser();
		String suffix = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
		String realPath = fileStoreDir + File.separator + suffix;
		File dir = new File(realPath);
		if(!dir.exists() || !dir.isDirectory()){
			dir.mkdirs();
		}
    	if (files.length > 0) {
    		for (MultipartFile file : files) {
    			FileInfo fileInfo = new FileInfo();
    			fileInfo.setId(UUID.randomUUID().toString());
    			fileInfo.setCreator(user.getUsername());
    			fileInfo.setCreateDate(new Date());
    			fileInfo.setSize(file.getSize()/1024);
    			fileInfo.setName(file.getOriginalFilename());
    			fileInfo.setType(getType(file.getOriginalFilename()));
    			File dest = new File(dir,  fileInfo.getId() + "@" + fileInfo.getName());
    			fileInfo.setPath(dest.getAbsolutePath());
    			JpaUtil.persist(fileInfo);
                file.transferTo(dest);
    		}
    	}
    }
    
    @RequestMapping("/download")
    public void download(String fileId, HttpServletRequest request, HttpServletResponse response) throws Exception { 	
    	FileInfo fileInfo = JpaUtil
    		.linq(FileInfo.class)
    		.equal("id", fileId)
    		.findOne();
    	String path = fileInfo.getPath();
    	File file = new File(path);
    	if (file.exists()) {
    		String fileNameStr = fileInfo.getName();
        	String fileName = new String(fileNameStr.getBytes("UTF-8"), "iso-8859-1");// 为了解决中文名称乱码问题
        	response.setContentType("application/x-msdownload");  
        	response.setCharacterEncoding("UTF-8");
        	response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        	FileCopyUtils.copy(getBytes(path), response.getOutputStream());
    	} else {
    		response.sendRedirect("http://localhost:8080/bdf3-colaui/exists");
    	}
    }
    
    private String getType(String originalFilename) {
    	int index = originalFilename.indexOf(".");
    	if (index != -1) {
    		return originalFilename.substring(index + 1, originalFilename.length());
    	}
		return "其他";
	}
    
    public static byte[] getBytes(String filePath){  
        byte[] buffer = null;  
        try {  
            File file = new File(filePath);  
            FileInputStream fis = new FileInputStream(file);  
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);  
            byte[] b = new byte[1000];  
            int n;  
            while ((n = fis.read(b)) != -1) {  
                bos.write(b, 0, n);  
            }  
            fis.close();  
            bos.close();  
            buffer = bos.toByteArray();  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return buffer;  
    }
	
}
