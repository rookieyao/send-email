package com.rookie.send.email.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.rookie.send.email.entity.Email;
import com.rookie.send.email.entity.EmailError;
import com.rookie.send.email.mapper.EmailErrorMapper;
import com.rookie.send.email.mapper.EmailMapper;
import com.rookie.send.email.param.EmailParam;
import com.rookie.send.email.param.EmailType;
import com.rookie.send.email.service.EmailErrorService;
import com.rookie.send.email.service.EmailService;
import com.rookie.send.email.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@Service
public class EmailErrorServiceImpl extends ServiceImpl<EmailErrorMapper, EmailError> implements EmailErrorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailErrorServiceImpl.class) ;
    @Autowired
    EmailErrorMapper emailErrorMapper;

    @Autowired
    EmailMapper emailMapper;

    @Autowired
    EmailErrorService emailErrorService;

    @Autowired
    EmailService emailService;

    private static ExecutorService sendErrorEmailPool = new ThreadPoolExecutor(100, 100,
            3L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(1024), new ThreadFactoryBuilder()
            .setNameFormat("error-pool-%d").build(), new ThreadPoolExecutor.AbortPolicy());


    @Override
    public void sendEmail(List<Email> emails) {

        try {
            if(ReceiverPool.errorReceiverPool.size() == 0){
                for(Email email: emails){
                    ReceiverPool.errorReceiverPool.offer(email);
                }
            }else{
                LOGGER.info("??????????????????????????????????????????!");
//                return;
            }

            Map<String, EmailParam> address = AddresserPool.getAddressByApi();
            int i = 0;
            while (ReceiverPool.errorReceiverPool.size() >0){
                if(i >5){
                    address = AddresserPool.getAddressByApi();
                    i = 0;
                }
                String addresser = getAddresser(address);
                try {
                    sendOneEmail(addresser,address);
                    LOGGER.info("{}????????????????????????????????????10s????????????",addresser);
                } catch (Exception e) {
//                    e.printStackTrace();
                    LOGGER.error("sendOneEmail???????????????{},??????10s????????????????????????",e.getMessage());
                }finally {
                    i++;
                    TimeUnit.SECONDS.sleep(10);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


//        double threadNum = Math.ceil(Double.valueOf(emails.size())/Double.valueOf(AddresserPool.maxSendNum));
//
//        LOGGER.info("????????????{}?????????????????????",threadNum);
//        for(int i=0; i<threadNum; i++){
//            Map<String, EmailParam> address = AddresserPool.getAddressByApi();
//            sendErrorEmailPool.execute(new SendErrorEmailHandler(address, emailErrorService, emailService, EmailType.EMAIL_TEXT_KEY.getCode()));
//        }
    }


    @Override
    public void sendUnSendEmail(List<Email> emails) {

        LOGGER.info("????????????????????????????????????????????????====??????======!");
        Map<String, EmailParam> address = AddresserPool.getAddressByApi();
        int i = 0;
        while (true){

            for(Email email :emails){
                if(i >5){
                    address = AddresserPool.getAddressByApi();
                    i = 0;
                }
                String addresser = getAddresser(address);
                sendUSendEmail(addresser, address, email);
                i++;
            }

            Map<String,Object> params = new HashMap<>();
            params.put("status",0);
            List<Email> afterSend = emailMapper.selectByMap(params);

            if(afterSend ==null || afterSend.size() ==0){
                break;
            }
        }
        LOGGER.info("????????????????????????????????????????????????====??????======!");
    }

    private void sendOneEmail(String addresser, Map<String, EmailParam> address)  throws Exception{

        if(ReceiverPool.errorReceiverPool.size() <=0){
            LOGGER.info("ReceiverPool.errorReceiverPool is null!!!");
            return;
        }

        Email recevier = (Email)ReceiverPool.errorReceiverPool.poll();

        // ??????????????????
        String receiverEmail = recevier.getEmail();String title = EmailTiltlePool.getRandomTitle(null);
        String body = ReceiverPool.getTextBody(null);
        String sendInfo = String.format("%sSTARTING TO SEND EMAIL,RECEIVER:%s,TITLE:%s,BODY:%s,address:%s",addresser, receiverEmail,title, body, JSON.toJSONString(address));

        recevier.setSendInfo(sendInfo);
        recevier.setPassword(address.get(addresser).getPassword());
        try {
//            recevier = (Email)ReceiverPool.receiverPool.poll();
//
//            // ??????????????????
//            String receiverEmail = recevier.getEmail();String title = EmailTiltlePool.getRandomTitle(emailKey);
//            String body = ReceiverPool.getTextBody(emailKey);
            EmailUtil emailUtil = new EmailUtil();
            emailUtil.sendEmail01(addresser,recevier.getEmail(),title ,body, address);

            recevier.setStatus(1); //????????????
            recevier.setSender(addresser);
            recevier.setMsg("send success!");
            emailService.updateById(recevier);
//            LOGGER.info("{}????????????????????????????????????15s????????????",addresser);
//            TimeUnit.SECONDS.sleep(15);
        } catch (Exception e) {
//            try {
//                AddresserPool.addresserSendCountMap.get(addresser).decrementAndGet();
                if(recevier !=null){
                    //?????????????????????????????????,?????????????????????????????????????????????????????????
                    //                ReceiverPool.receiverPool.offer(recevier);
                    recevier.setStatus(2); //????????????
                    recevier.setSender(addresser);
                    Integer errorSendNum = recevier.getErrorSendNum();
                    if(errorSendNum <=10){
                        errorSendNum = errorSendNum+1;
                    }else{
                        recevier.setStatus(3);// ????????????????????????10???
                    }
                    recevier.setErrorSendNum(errorSendNum);
                    recevier.setMsg(e.getMessage());
                    emailService.updateById(recevier);
                }
                throw e;
//                e.printStackTrace();

//                TimeUnit.SECONDS.sleep(15);
//            } catch (InterruptedException ex) {
//                ex.printStackTrace();
//            }
        }
    }

    public void sendUSendEmail(String addresser, Map<String, EmailParam> address, Email email){

        if(ReceiverPool.receiverPool.size() >0){
            LOGGER.info("ReceiverPool.errorReceiverPool is not null!!!");
            return;
        }

        // ??????????????????
        String receiverEmail = email.getEmail();String title = EmailTiltlePool.getRandomTitle(null);
        String body = ReceiverPool.getTextBody(null);
        String sendInfo = String.format("%sSTARTING TO SEND EMAIL,RECEIVER:%s,TITLE:%s,BODY:%s,address:%s",addresser, receiverEmail,title, body, JSON.toJSONString(address));

        email.setSendInfo(sendInfo);
        email.setPassword(address.get(addresser).getPassword());
        try {

            EmailUtil emailUtil = new EmailUtil();
            emailUtil.sendEmail01(addresser,email.getEmail(),title ,body, address);

            email.setStatus(1); //????????????
            email.setSender(addresser);
            email.setMsg("send success!");
            emailService.updateById(email);
            LOGGER.info("{}????????????????????????????????????30s????????????",addresser);
            TimeUnit.SECONDS.sleep(30);
        } catch (Exception e) {
            try {

                email.setStatus(2); //????????????
                email.setErrorSendNum(1);
                email.setSender(addresser);
                email.setMsg(e.getMessage());
                emailService.updateById(email);
                LOGGER.error("sendUnSendEmail ???????????????{},??????30s????????????????????????",e.getMessage());
                TimeUnit.SECONDS.sleep(30);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void updateErrorEmail(Email email, String msg) throws Exception{
        email.setStatus(2); //????????????
        email.setMsg(msg);
        emailService.updateById(email);
    }

    public String getAddresser(Map<String, EmailParam> address){
        Set<String> addresser = address.keySet();
        for(String key:addresser){
            if (key !=null){
                return key;
            }
        }
        return null;
    }
}
