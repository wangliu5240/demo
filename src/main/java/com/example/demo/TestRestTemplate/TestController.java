package com.example.demo.TestRestTemplate;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController
{
    @RequestMapping(value = "/testPost")
    public ResponseBean testPost(@RequestBody RequestBean requestBean)
    {
        System.out.println(" 接收过来的请求参数： " + requestBean.toString());
        ResponseBean responseBean = new ResponseBean();
        responseBean.setRetCode("0000");
        responseBean.setRetMsg("succ");
        return responseBean;
    }

    @RequestMapping("/sayWord")
    public String helloworld()
    {
        return "Hello Word！";
    }
}
