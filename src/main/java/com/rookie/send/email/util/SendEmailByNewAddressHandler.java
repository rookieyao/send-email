package com.rookie.send.email.util;

import com.alibaba.fastjson.JSON;
import com.rookie.send.email.entity.Email;
import com.rookie.send.email.entity.EmailError;
import com.rookie.send.email.param.EmailParam;
import com.rookie.send.email.service.EmailErrorService;
import com.rookie.send.email.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Author rookie
 * @Date 2021/5/8 15:22
 * @Description
 **/

public class SendEmailByNewAddressHandler implements Runnable{

    private static final Logger LOGGER = LoggerFactory.getLogger(SendEmailByNewAddressHandler.class) ;

//    private static String recevier;
    private static String emailKey;
//    private CountDownLatch countDownLatch;

    private static Map<String, EmailParam> addressByApi;

    private EmailErrorService emailErrorService;

    private EmailService emailService;

    public SendEmailByNewAddressHandler(Map<String, EmailParam> addressByApi, EmailErrorService emailErrorService, EmailService emailService, String emailKey) {
//        LOGGER.info("{}对应的当前线程id:{}",getAddresser(address),Thread.currentThread().getId());
//        this.recevier = recevier;
        this.emailKey = emailKey;
//        this.countDownLatch = countDownLatch;
        this.emailErrorService = emailErrorService;
        this.emailService = emailService;
        this.addressByApi = addressByApi;

    }

    @Override
    public void run() {

        try {

//            ThreadLocalUtil.set(String.valueOf(Thread.currentThread().getName()),address);
//            Map<String, EmailParam> address = (Map<String, EmailParam>)ThreadLocalUtil.getThreadLocal().get(String.valueOf(Thread.currentThread().getId()));

            String addresser = getAddresser(addressByApi);
            while(ReceiverPool.receiverPool.size()>0){
//                Map<String, EmailParam> addressByApi = AddresserPool.getAddressByApi();
//                String addresser = getAddresser(addressByApi);
                if(AddresserPool.addresserSendCountMap.get(addresser).intValue() <= AddresserPool.maxSendNum){
                    LOGGER.info("{}准备发送邮件，今日已发送次数:{}",addresser,AddresserPool.addresserSendCountMap.get(addresser).intValue());
                    AddresserPool.addresserSendCountMap.get(addresser).incrementAndGet();
                    sendOneEmail(addresser,addressByApi);
                }else{
                    LOGGER.info("{},已到达当天最大发送次数，不让发送邮件!",addresser);
                    break;
                }
            }
            LOGGER.info("收件人队列为空，发送线程{}结束",Thread.currentThread().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
//            countDownLatch.countDown();
        }

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

    static Object object = new Object();
    /**  发送邮件的方法 */
    private void sendOneEmail(String addresser, Map<String, EmailParam> address) throws Exception{
        Email recevier = (Email)ReceiverPool.receiverPool.poll();

        // 发送文本邮件
        String receiverEmail = recevier.getEmail();String title = EmailTiltlePool.getRandomTitle(emailKey);
        String body = ReceiverPool.getTextBody(emailKey);
        String sendInfo = String.format("%sSTARTING TO SEND EMAIL,RECEIVER:%s,TITLE:%s,BODY:%s,address:%s",addresser, receiverEmail,title, body, JSON.toJSONString(address));

        recevier.setSendInfo(sendInfo);
        try {
//            recevier = (Email)ReceiverPool.receiverPool.poll();
//
//            // 发送文本邮件
//            String receiverEmail = recevier.getEmail();String title = EmailTiltlePool.getRandomTitle(emailKey);
//            String body = ReceiverPool.getTextBody(emailKey);
            EmailUtil.sendEmail01(addresser,recevier.getEmail(),title ,body, address);

            recevier.setStatus(1); //发送成功
            recevier.setMsg("send success!");
            recevier.setSender(addresser);
            emailService.updateById(recevier);
            LOGGER.info("{}发送完一封邮件，需要休息30s再次发送",addresser);
            TimeUnit.SECONDS.sleep(30);
        } catch (Exception e) {
            AddresserPool.addresserSendCountMap.get(addresser).decrementAndGet();
            if(recevier !=null){
                //暂时不重新放入队列里面,将出现异常的收件账号存入发送异常的表中
                //                ReceiverPool.receiverPool.offer(recevier);
                recevier.setStatus(2); //发送异常
                recevier.setSender(addresser);
                recevier.setErrorSendNum(1);
                recevier.setMsg(e.getMessage());
                emailService.updateById(recevier);
            }
            e.printStackTrace();
            LOGGER.error("sendOneEmail出现异常，{},准备30s之后继续发送邮件",e.getMessage());
            TimeUnit.SECONDS.sleep(30);
        }
    }

    private EmailError getErrorEmail(String recevier, String message) {
        EmailError emailError = new EmailError();
        emailError.setEmail(recevier);
        emailError.setMsg(message);
        return emailError;
    }

}
