package com.example.demo.TestConnectionHelper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectionHelper {

    private static Logger logger = LoggerFactory.getLogger(ConnectionHelper.class);

    private static final String DEFAULT_ENCODING = "UTF-8";


    /**
     * 请求参数字符串转化为map
     *
     * @param queryString
     * @return
     */
    public static Map<String, String> queryStrToMap(String queryString) {
        String[] subQueryStrs = null;
        if (StringUtils.isNotBlank(queryString)) {
            subQueryStrs = queryString.split("&");
        }
        Map<String, String> params = new HashMap<String, String>(subQueryStrs.length);
        for (String item : subQueryStrs) {
            if (StringUtils.isNotBlank(item)) {
                String[] keyValue = item.split("=");
                if (keyValue.length == 2) {
                    params.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return params;
    }


    /**
     * 请求参数map转化为字符串，并且按照编码转换
     *
     * @param params
     * @param encoding
     */
    public static String mapToQueryStr(Map<String, String> params, String encoding) {
        if (params != null && !params.isEmpty()) {
            List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String value = String.valueOf(entry.getValue());
                String key = entry.getKey();
                if (value != null && !value.isEmpty() && key != null && !key.isEmpty()) {
                    pairs.add(new BasicNameValuePair(key, value));
                }
            }
            try {
                return EntityUtils.toString(new UrlEncodedFormEntity(pairs,
                                                                     StringUtils.isNotBlank(
                                                                             encoding) ? encoding : DEFAULT_ENCODING));
            } catch (Exception e) {
                e.printStackTrace();
                //logger.error("map to QueryStr fail!", e.getMessage());
            }
        }
        return null;
    }

    private static HttpURLConnection initConnect(String url, String method, String data,
                                                 Map<String, String> headMap) throws Exception {
        //创建一个URL对象
        URL httpurl = new URL(url);
        HttpURLConnection hc = (HttpURLConnection) httpurl.openConnection();
        //设置连接超时(毫秒)
        hc.setConnectTimeout(6 * 1000);
        String Method = method.toUpperCase();
        if ("GET".equals(Method)) {
            logger.debug("get request url: " + url);
            logger.debug("get request data: " + data);
        }
        logger.debug("Method: " + Method);

        if ("POST".equals(Method)) {
            //post请求一定要容许输出
            hc.setDoInput(true);
            hc.setDoOutput(true);
            if (data != null) {
                //设置文件的总长度
                hc.setRequestProperty("Content-Length", String.valueOf(data.length()));
            }
        }
        //设置提交方式get或者post
        hc.setRequestMethod(Method);
        //URL 连接可用于输入和/或输出。将 doInput 标志设置为 true，指示应用程序要从 URL 连接读取数据。
        hc.setDoInput(true);
        //设置缓存
        hc.setUseCaches(false);
        //设置请求头
        if (headMap != null) {
            for (Map.Entry<String, String> entry : headMap.entrySet()) {
                hc.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        //设置文件类型
        if ("".equals(hc.getRequestProperty("Content-Type")) || hc.getRequestProperty("Content-Type") == null) {
            hc.setRequestProperty("Content-Type", "application/json");
        }
        //设置字符集
        if ("".equals(hc.getRequestProperty("Charset")) || hc.getRequestProperty("Charset") == null) {
            hc.setRequestProperty("Charset", "utf-8");
        }
        return hc;
    }

    /**
     * 请求文件流，返回字节流
     *
     * @param url
     * @param params
     * @param encoding
     * @param headMap
     * @return
     */
    public static byte[] sendAndDownload(String url, Map<String, String> params, String encoding, Map<String, String> headMap) {
        try {
            if (params != null && params.size() > 0) {
                url += (url.indexOf("?") == -1 ? "?" : "&") + mapToQueryStr(params, encoding);
            }
            return sendAndDownload(url, "GET", headMap);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }

        return null;
    }

    private static byte[] sendAndDownload(String url, String method, Map<String, String> headMap) throws Exception {
        String Method = method.toUpperCase();
        HttpURLConnection hc = initConnect(url, Method, null, headMap);
        long s = System.currentTimeMillis();
        hc.connect();
        //请求响应代码
        logger.info("Http Status Code:" + hc.getResponseCode());
        //请求头文件
        logger.info("request headers : " + hc.getHeaderFields());
        int code = hc.getResponseCode();

        byte[] res = null;
        if (code == 200) {
            InputStream ins = hc.getInputStream();
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            //每次读取的字符串长度，如果为-1，代表全部读取完毕
            int len = 0;
            while ((len = ins.read(buffer)) != -1) {
                //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
                outStream.write(buffer, 0, len);
            }
            res = outStream.toByteArray();

        } else {
            logger.error("连接发生错误，错误代码：" + code);
        }
        //关闭请求
        hc.disconnect();
        long e = System.currentTimeMillis();
        long time = e - s;
        logger.info("耗时: " + time + "毫秒");
        return res;
    }

    /**
     * 模拟Http协议,发送请求,将网页以流的形式读回来
     *
     * @param url
     * @param method
     * @param data
     */
    public static String HttpConnect(String url, String method, String data, Map<String, String> headMap) {
        StringBuilder result = null;
        String Method = method.toUpperCase();
        try {
            long s = System.currentTimeMillis();
            HttpURLConnection hc = initConnect(url, Method, data, headMap);
            hc.setConnectTimeout(300000);
            //连接
            hc.connect();
            if ("POST".equals(Method)) {
                OutputStream ops = hc.getOutputStream();
                byte[] buff;
                if (data != null) {
                    //设置请求参数为二进制流为UTF-8编码格式
                    buff = data.getBytes(DEFAULT_ENCODING);
                    ops.write(buff);
                }
                ops.flush();//刷空输出流，并输出所有被缓存的字节
                ops.close();//流操作完毕后关闭
            }
            //请求响应代码
            logger.info("Http Status Code:" + hc.getResponseCode());

            //请求头文件
            logger.info("request headers : " + hc.getHeaderFields());
            int code = hc.getResponseCode();
            if (code == 200) {
                result = new StringBuilder();
                InputStream ins = hc.getInputStream();
                //将返回的字节流转换为字符流
                InputStreamReader isr = new InputStreamReader(ins, DEFAULT_ENCODING);
                convert2String(isr, result);
                ins.close();

            } else {
                logger.error("连接发生错误，错误代码：" + code);
                StringBuilder errStr = new StringBuilder();
                InputStream ins = hc.getErrorStream();
                InputStreamReader isr = new InputStreamReader(ins);
                convert2String(isr, errStr);
                logger.debug("error:" + errStr);
                //关闭输出流
                ins.close();
            }

            //关闭请求
            hc.disconnect();
            long e = System.currentTimeMillis();
            long time = e - s;
            logger.info("耗时: " + time + "毫秒");
        } catch (Exception e) {
            logger.debug("", e.fillInStackTrace());
            e.printStackTrace();
        }
        return result == null ? null : result.toString();
    }

    private static void convert2String(InputStreamReader isr, StringBuilder result) throws IOException {
        char[] cbuf = new char[1024];
        int i = isr.read(cbuf);
        while (i > 0) {
            result.append(new String(cbuf, 0, i));
            i = isr.read(cbuf);
        }
    }

    /**
     * 模拟https安全协议
     *
     * @param url
     * @param data
     * @param method
     */
    public static String HttpsConnect(String url, String method, String data, Map<String, String> headMap) {
        StringBuilder result = new StringBuilder();
		/*if(url.matches(regx)){
			String urllogout = url.substring(url.indexOf("security/") + 9, url.indexOf("?"));
			logger.info("正在执行 [{}] 接口···", urllogout.toUpperCase());
		} else if(url.matches(regx1)) {
			String urllogin = url.substring(url.indexOf("security/") + 9 );
			logger.info("正在执行 [{}] 接口···", urllogin.toUpperCase());
		} else if(url.matches(regx2)) {
			String urllicense = url.substring(url.indexOf("license/") + 8, url.indexOf("?"));
			String urllicense1 = urllicense.substring(urllicense.indexOf("/") + 1);
			logger.info("正在执行 [{}] 接口···", urllicense1.toUpperCase());
		} else if(url.matches(regx3)) {
			String urllicense2 = url.substring(url.indexOf("license/") + 8, url.indexOf("?"));
			logger.info("正在执行 [{}] 接口···", urllicense2.toUpperCase());
		}*/
        try {
            //创建一个请求对象
            URL httpsurl = new URL(url);
            long s = System.currentTimeMillis();
            HttpsURLConnection hsc = (HttpsURLConnection) httpsurl.openConnection();
            X509TrustManager xtm = new MyX509TrustManager();
            X509TrustManager[] mytm = new X509TrustManager[]{xtm};

            SSLContext sct = SSLContext.getInstance("SSL");
            sct.init(null, mytm, null);
            MyHostnameVerifier hostnameVerifier = new MyHostnameVerifier();
            hsc.setHostnameVerifier(hostnameVerifier);
            hsc.setSSLSocketFactory(sct.getSocketFactory());

            String Method = method.toUpperCase();
            if ("GET".equals(Method)) {
                logger.debug("get request url:" + url);
                logger.debug("get request data:" + url);
            }
            logger.debug("Method:" + Method);
            hsc.setRequestMethod(Method);
            hsc.setDoInput(true);

            if ("POST".equals(Method)) {
                hsc.setDoInput(true);
                hsc.setDoOutput(true);
                if (data != null) {
                    hsc.setRequestProperty("Content-Length",
                                           String.valueOf(data.length()));
                }
            }
            //设置请求头
            if (headMap != null) {
                for (Map.Entry<String, String> entry : headMap.entrySet()) {
                    hsc.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            //设置文件类型
            if ("".equals(hsc.getRequestProperty("Content-Type")) || hsc.getRequestProperty("Content-Type") == null) {
                hsc.setRequestProperty("Content-Type", "application/json");
            }
            //设置字符集
            if ("".equals(hsc.getRequestProperty("Charset")) || hsc.getRequestProperty("Charset") == null) {
                hsc.setRequestProperty("Charset", "utf-8");
            }

            hsc.connect();
            if ("POST".equals(Method)) {
                OutputStream ops = hsc.getOutputStream();
                byte[] buff;
                if (data != null) {
                    //请求参数为UTF-8格式
                    buff = data.getBytes(DEFAULT_ENCODING);
                    ops.write(buff);
                }
                ops.flush();
                ops.close();
            }
            //请求响应代码
            logger.info("Https Status Code :" + hsc.getResponseCode());
            //请求头文件
            logger.info("request headers : " + hsc.getHeaderFields());
            int code = hsc.getResponseCode();
            if (code == 200) {
                InputStream ins = hsc.getInputStream();
                if (ins.available() < 0) {
                    logger.error("没有响应数据，可能是对方已经断开连接···");
                } else {
                    InputStreamReader isr = new InputStreamReader(ins, DEFAULT_ENCODING);
                    char[] cbuf = new char[1024];
                    int i = isr.read(cbuf);
                    while (i > 0) {
                        result.append(new String(cbuf, 0, i));
                        i = isr.read(cbuf);
                    }
                }
                ins.close();
            } else {
                logger.error("连接发生错误，错误代码：" + code);
                InputStream ins;
                ins = hsc.getErrorStream();
                InputStreamReader isr = new InputStreamReader(ins, DEFAULT_ENCODING);
                char[] cbuf = new char[1024];
                int i = isr.read(cbuf);
                while (i > 0) {
                    result.append(new String(cbuf, 0, i));
                    i = isr.read(cbuf);
                }
                logger.debug("error:" + result);
                ins.close();
            }
            hsc.disconnect();
            long e = System.currentTimeMillis();
            long time = e - s;
            logger.debug("耗时:" + time + "毫秒");
        } catch (Exception e) {
            logger.debug("", e.fillInStackTrace());
            e.printStackTrace();
        }
        return result.toString();

    }

    /**
     * 定义区分https和http协议的请求
     *
     * @param url
     * @param method
     * @param data
     */
    public static String Connect(String url, String method, String data, Map<String, String> headMap) {
        try {
            if (StringUtils.isNoneBlank(url) && StringUtils.isNoneBlank(method)) {
                if (url.toLowerCase().startsWith("http:")) {
                    return HttpConnect(url, method, data, headMap);
                } else if (url.toLowerCase().startsWith("https:")) {
                    return HttpsConnect(url, method, data, headMap);
                }
            } else {
                throw new Exception("url or method parameter error···");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    public static String doGet(String url, String queryString, Map<String, String> headMap) {
        return doGet(url, queryStrToMap(queryString), null, headMap);
    }

    /**
     * 模拟get请求
     *
     * @param url      请求url
     * @param params   请求参数map类型
     * @param encoding 编码类型
     */
    public static String doGet(String url, Map<String, String> params, String encoding, Map<String, String> headMap) {
        if (StringUtils.isBlank(url)) {
            logger.debug("url is null！");
            return null;
        }

        try {
            if (params != null && params.size() > 0) {
                url += (url.indexOf("?") == -1 ? "?" : "&") + mapToQueryStr(params, encoding);
            }
            return Connect(url, "GET", null, headMap);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }

        return null;
    }

    /**
     * 模拟POST请求 ，仅支持 application/json 请求类型
     *
     * @param url
     * @param data
     */
    public static String doPost(String url, String data, Map<String, String> headMap) {
        logger.debug(" post request url : " + url);
        logger.debug("post request data : " + data);
        if (StringUtils.isNotBlank(data)) {
            return Connect(url, "POST", data, headMap);
        } else {
            return Connect(url, "POST", "{}", headMap);
        }
    }

    /**
     * 发送post请求，仅支持 application/json 请求类型
     *
     * @param url    请求url
     * @param params url请求参数
     * @param data   application/json 格式参数的json字符串
     * @return
     */
    public static String doPostStr(String url, Map<String, String> params, String data, Map<String, String> headMap) {
        if (StringUtils.isBlank(url)) {
            logger.debug("url is null！");
            return null;
        }
        try {
            if (params != null && params.size() > 0) {
                url += (url.indexOf("?") == -1 ? "?" : "&") + mapToQueryStr(params, DEFAULT_ENCODING);
            }
            return Connect(url, "POST", StringUtils.isNotBlank(data) ? data : "{}", headMap);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return null;
    }

    /**
     * 发送post请求，仅支持 application/json 请求类型
     *
     * @param url    请求url
     * @param params url请求参数
     * @param data   application/json 格式参数的json数据对象
     * @return
     */
    public static String doPost(String url, Map<String, String> params, Object data, Map<String, String> headMap) {
        String jsonStr = null;
        if (data != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                jsonStr = mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY).writeValueAsString(data);
            } catch (JsonProcessingException e) {
                logger.error(e.getMessage());
            }
        }
        return ConnectionHelper.doPostStr(url, params, jsonStr, headMap);

    }

    /**
     * 发送post请求，仅支持 application/json 请求类型
     *
     * @param url    请求url
     * @param params url请求参数
     * @param data   application/json 格式参数的json数据对象
     * @return
     */
    public static String ztbDoPost(String url, Map<String, String> params, Object data, Map<String, String> headMap) {
        String jsonStr = null;
        if (data != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                jsonStr = JSONObject.toJSONString(data);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return ConnectionHelper.doPostStr(url, params, jsonStr, headMap);

    }

    public static Map<String, String> initHeaders(MediaType mediaType, String passId, String passtoken) throws Exception {
        Long localTimestamp = System.currentTimeMillis() / 1000;
        String timestamp = localTimestamp.toString();
        String nonce = timestamp + Integer.toString(1000000 + (int) (Math.random() * 1000000)).substring(1);
        String signature = calcRequestSign(timestamp, passtoken, nonce);
        //头部表明参数传递格式
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", mediaType == null ? MediaType.APPLICATION_JSON.toString() : mediaType.toString());
        headers.put("x-tif-paasid", passId);
        headers.put("x-tif-timestamp", localTimestamp.toString());
        headers.put("x-tif-signature", signature);
        headers.put("x-tif-nonce", nonce);
        return headers;
    }

    /**
     * 计算signature
     *
     * @param timestamp
     * @param paasToken
     * @param nonce
     * @return
     * @throws Exception
     */
    public static String calcRequestSign(String timestamp, String paasToken, String nonce) throws Exception {

        return toSHA256(String.format("%s%s%s%s", timestamp, paasToken, nonce, timestamp));
    }


    /**
     * SHA-256加密
     *
     * @param str 加密字符串
     * @return
     */
    public static String toSHA256(String str) throws Exception {
        try {
            MessageDigest messageDigest;
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            return byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw e;
        } catch (UnsupportedEncodingException e) {
            throw e;
        }
    }

    /**
     * byte转换成16进制
     *
     * @param bytes
     * @return
     */
    public static String byte2Hex(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer();
        String temp;
        for (int i = 0; i < bytes.length; i++) {
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length() == 1) {
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }

}
