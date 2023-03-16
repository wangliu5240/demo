package com.example.demo.TestConnectionHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * <p>
 * @Decription 自定义证书信任管理器类
 * </p>
 * 
 * <p>
 * All rights reserved.
 * </p>
 *
 * <p>
 * Company:Zsoft
 * </p>
 * <p>
 * CreateDate:2017年1月22日
 * </p> 
 *
 */
public class MyX509TrustManager implements X509TrustManager{
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	/**
	 * @Description：这里可以进行证书的初始化操作  
	 */
	public MyX509TrustManager() {
		//logger.debug("初始化X.509·····");
	}
	
	/**
	 * 该方法检查客户端的证书，若不信任该证书则抛出异常。由于我们不需要对客户端进行认证，
	 * 因此我们只需要执行默认的信任管理器的这个方法。
	 */
	public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString)
			throws CertificateException {
		//接收任意客户端证书
		logger.debug("接口任意客户端证书······");
		for(int i = 0 ; i < paramArrayOfX509Certificate.length ; i++){
			logger.debug("paramArrayOfX509Certificate[" + i + "] = "  + paramArrayOfX509Certificate[i]);
		}
		logger.debug("paramString = " + paramString);
		
	}
    
	/**
	 * 该方法检查服务器的证书，若不信任证书同样抛出异常。通过自己实现该方法，可以使之信任我们指定的
	 * 任何证书。在实现该方法时，也可以简单的不做任何处理，即一个空函数体，由于不会抛出异常，
	 * 他就会信任任何证书。
	 */
	public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString)
			throws CertificateException {
		//接收任意服务端证书
		//logger.debug("接收任意服务端证书······");
		for(int i = 0 ; i < paramArrayOfX509Certificate.length ; i++){
			//logger.debug("paramArrayOfX509Certificate[" + i + "] = "  + paramArrayOfX509Certificate[i]);
		}
		//logger.debug("算法 为 " + paramString);
	}
	
	/**
	 * 返回受信任的X509证书数组
	 */
	public X509Certificate[] getAcceptedIssuers() {
		// 返回接收的发行商数组
		//logger.debug("返回接收的发行商数组·····");
		return null;
	}
	
}
