package com.rookie.send.email.controller;

import com.rookie.send.email.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

@RestController
public class EmailController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailController.class) ;

    @Resource
    private EmailService emailService ;

    final String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    final Pattern regex = Pattern.compile(check);

//    @RequestMapping("/sendEmail")
//    public String sendEmail (@RequestParam("file") MultipartFile file, HttpServletRequest request){
//
//        SendEmailModel model = new SendEmailModel() ;
//        model.setReceiver("1126480696@qq.com");
//        emailService.sendEmail(EmailType.EMAIL_TEXT_KEY.getCode(),model);
//        LOGGER.info("执行结束====>>");
//        return "success" ;
//    }

    @RequestMapping("/sendEmail")
    public String sendEmail (@RequestParam("file") MultipartFile file, HttpServletRequest request){

        /* 读取数据 */
        try {

            ArrayBlockingQueue recevierQueue = getRecevierQueue(file);

            LOGGER.info("starting send email,email`s num is:{}",recevierQueue.size());
            emailService.sendEmailByThread(recevierQueue);

        } catch (Exception e) {
            LOGGER.error("read errors :" + e);
        }

        return "success" ;
    }

    public ArrayBlockingQueue getRecevierQueue(MultipartFile file){

        ArrayBlockingQueue recevierQueue = new ArrayBlockingQueue<String>(200000);
        try {
            AtomicInteger rightEmail = new AtomicInteger();
            AtomicInteger errorEmail = new AtomicInteger();

            InputStream inputStream = file.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
            String lineTxt = null;
            while ((lineTxt = br.readLine()) != null) {
                String email = lineTxt.trim();
                if(regex.matcher(email).matches()){
                    rightEmail.incrementAndGet();
                    recevierQueue.offer(email);
                }else{
                    errorEmail.incrementAndGet();
                    LOGGER.info("不合格的邮箱账号:{}",email);
                }
            }
            br.close();
            LOGGER.info("read receiver txt info done,right email num:{},error email num:{}",rightEmail.intValue(),errorEmail.intValue());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return recevierQueue;
    }
}
