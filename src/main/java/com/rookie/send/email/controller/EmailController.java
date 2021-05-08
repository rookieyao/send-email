package com.rookie.send.email.controller;

import com.rookie.send.email.model.SendEmailModel;
import com.rookie.send.email.param.EmailType;
import com.rookie.send.email.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
public class EmailController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailController.class) ;

    @Resource
    private EmailService emailService ;

    final String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    final Pattern regex = Pattern.compile(check);

    @RequestMapping("/sendEmail")
    public String sendEmail (@RequestParam("file") MultipartFile file, HttpServletRequest request){

        /* 读取数据 */
        try {
            InputStream inputStream = file.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
            String lineTxt = null;
            while ((lineTxt = br.readLine()) != null) {

                if(regex.matcher(lineTxt.trim()).matches()){
                    LOGGER.info(lineTxt.trim());
                }else{
                    LOGGER.info("不合格的邮箱账号:{}",lineTxt);
                }

            }
            br.close();
        } catch (Exception e) {
            System.err.println("read errors :" + e);
        }

//        SendEmailModel model = new SendEmailModel() ;
//        model.setReceiver("1126480696@qq.com");
//        emailService.sendEmail(EmailType.EMAIL_TEXT_KEY.getCode(),model);
//        LOGGER.info("执行结束====>>");
        return "success" ;
    }
}
