package com.example.demo.TestConnectionHelper;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TestConnectionHelper {

    @RequestMapping(value = "/testConnectionHelper")
    public String test(){
        //请求地址
        String url = "http://19.50.65.3:8083/aplanmis-front/rest/form/project/GDPlatform/getEnterpriseInfoByCreditCode";
        Map<String,String> params = new HashMap<>();
        Map<String,String> headMap = new HashMap<>();
        params.put("unifiedSocialCreditCode","914419002818310966");
        params.put("unitInfoId","085e0e54-8113-475a-9112-27cea82d6fae");
        params.put("unitType","11");

        headMap.put("Cookie","JSESSIONID=BC9607EE6063AA7FD3C3E9B6915841AA");
        String s = ConnectionHelper.doGet(url, params, null, headMap);
        return s;
    }
}
