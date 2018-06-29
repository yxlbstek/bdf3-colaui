package com.bstek.cola;

import javax.servlet.MultipartConfigElement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableCaching
public class ColaApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(ColaApplication.class, args);
	}
	
	@Bean  
    public MultipartConfigElement multipartConfigElement() {  
        MultipartConfigFactory factory = new MultipartConfigFactory();  
        factory.setMaxFileSize("102400KB"); //单个文件最大  
        factory.setMaxRequestSize("102400KB"); //总上传数据大小  
        return factory.createMultipartConfig();  
    }
	
}
