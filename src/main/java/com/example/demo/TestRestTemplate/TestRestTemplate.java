package com.example.demo.TestRestTemplate;

import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RestController
public class TestRestTemplate {
    @RequestMapping(value = "/test")
    public String testRestTemplate(){
        //请求地址
        String url = "http://localhost:8080/testPost";
        //入参
        RequestBean requestBean = new RequestBean();
        requestBean.setTest1("1");
        requestBean.setTest2("2");
        requestBean.setTest3("3");

        RestTemplate restTemplate = new RestTemplate();
        //请求地址、请求参数、HTTP响应转换被转换成的对象类型。
        ResponseBean responseBean = restTemplate.postForObject(url, requestBean, ResponseBean.class);
//        ResponseBean responseBean = restTemplate.postForObject(url, requestBean, JSono.class);

        return responseBean.toString();
    }
}
