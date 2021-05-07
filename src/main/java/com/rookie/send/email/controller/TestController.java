package com.rookie.send.email.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author rookie
 * @Date 2021/5/7 22:41
 * @Description
 **/
@RestController
@RequestMapping("/test")
public class TestController {


    @GetMapping("/testSend")
    public String testSend(){

        return "发送成功!";
    }
}
