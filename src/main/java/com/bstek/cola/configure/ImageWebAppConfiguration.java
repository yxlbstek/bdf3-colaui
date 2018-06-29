package com.bstek.cola.configure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/** 
* 
* @author bob.yang
* @since 2018年6月29日
*
*/

@Configuration
public class ImageWebAppConfiguration extends WebMvcConfigurerAdapter {
	
	@Value("${bdf3.upload.fileStoreDir:D:/upload}")
	private String fileStoreDir;
	
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) { 
		registry.addResourceHandler("/path/**").addResourceLocations("file:/" + fileStoreDir + "/");
        super.addResourceHandlers(registry);
	}
	
}
