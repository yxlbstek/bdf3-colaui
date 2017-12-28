package com.bstek.cola.log;

import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Date;
import java.util.Enumeration;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.malagu.linq.JpaUtil;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.bstek.bdf3.security.ContextUtils;
import com.bstek.bdf3.security.orm.User;
import com.bstek.cola.log.annotation.Log;
import com.bstek.cola.orm.LogInfo;


/** 
* 
* @author bob.yang
* @since 2017年12月28日
*
*/

@Aspect
@Component
public class LogAspect {

	@Pointcut("@annotation(com.bstek.cola.log.annotation.Log)")
	public void logPointCut() {
		
	}

	@Around("logPointCut()")
	public Object around(ProceedingJoinPoint point) throws Throwable {
		Object result = point.proceed();
		saveLog(point);
		return result;
	}
	
	@Transactional
	private void saveLog(ProceedingJoinPoint joinPoint) {
		User user = ContextUtils.getLoginUser();
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		LogInfo logInfo = new LogInfo();
		
		Log log = method.getAnnotation(Log.class);
		if (log != null) {
			logInfo.setCategory(log.category());
			logInfo.setModule(log.module());
			logInfo.setOperation(log.operation());
			logInfo.setSource(getPrefix() + log.source());
		}
		logInfo.setId(UUID.randomUUID().toString());
		logInfo.setIp(getIp());
		logInfo.setOperationUser(user.getUsername());
		logInfo.setOperationUserNickname(user.getNickname());
		logInfo.setOperationDate(new Date());
		JpaUtil.persist(logInfo);
	}

	private String getPrefix() {
		RequestAttributes ra = RequestContextHolder.getRequestAttributes();
	    ServletRequestAttributes sra = (ServletRequestAttributes) ra;
	    HttpServletRequest request = sra.getRequest();
	    StringBuffer url = request.getRequestURL();
	    String uri = request.getRequestURI();
	    int index = url.indexOf(uri);
	    return url.substring(0, index);
	}

	@SuppressWarnings("rawtypes")
	private String getIp() {
		Enumeration allNetInterfaces;
		String ipAddress = "";
        try {
            allNetInterfaces = NetworkInterface.getNetworkInterfaces();
	        InetAddress ip = null;
	        while (allNetInterfaces.hasMoreElements()) {
	        	NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
	        	System.out.println(netInterface.getName());
	        	Enumeration addresses = netInterface.getInetAddresses();
	        	while (addresses.hasMoreElements()) {
	        		ip = (InetAddress) addresses.nextElement();
	        		if (ip != null && ip instanceof Inet4Address) {
	        			ipAddress = ip.getHostAddress();
	        		}
	        	}
	        }	        
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ipAddress;
	}
	
}
