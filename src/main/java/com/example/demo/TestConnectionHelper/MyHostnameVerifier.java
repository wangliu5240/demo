package com.example.demo.TestConnectionHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * 自定义验证服务证书
 */
public class MyHostnameVerifier implements HostnameVerifier{
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	public MyHostnameVerifier(){
		//logger.debug("初始化Verifier···");
	}
	
	/**
	 * 忽视验证证书Hostname，默认已接受
	 * 验证主机名和服务器验证方案的匹配是可接受的
	 * param：hostname -- 主机名
	 *        session --到主机的连接上使用的SSLsession
	 * return :如果主机名是可接受的，则返回true 
	 */
	public boolean verify(String hostname , SSLSession session) {
	
		return true;
	}
}
