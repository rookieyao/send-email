package com.rookie.send.email.util;

import com.rookie.send.email.param.EmailParam;
import com.rookie.send.email.param.EmailType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author rookie
 * @Date 2021/5/8 15:22
 * @Description
 **/
public class SendEmailHandler implements Runnable{

    private static final Logger LOGGER = LoggerFactory.getLogger(SendEmailHandler.class) ;

    private static Map<String, EmailParam> address;
    private static String emailKey;

    public SendEmailHandler(Map<String, EmailParam> address, String emailKey) {
        this.address = address;
        this.emailKey = emailKey;
    }

    @Override
    public void run() {

        AtomicInteger sendNums = new AtomicInteger(50);//每个账号发送的邮件数

        int i = sendNums.getAndDecrement();
        try {
            if(i >0){
                try {
                    // todo  日志打印
                    sendOneEmail();
                    sendNums.decrementAndGet();
                    TimeUnit.MINUTES.sleep(4);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } finally {

        }
        LOGGER.info("当前线程当天邮件发送完毕!");

    }

    /**  发送邮件的方法 */
    private static void sendOneEmail(){
        try{

            String recevier = (String)ReceiverPool.receiverPool.poll();
            LOGGER.info("收件人账号:{}",recevier);
            // 发送文本邮件
            EmailUtil.sendEmail01(recevier, EmailType.getByCode(emailKey),ReceiverPool.getTextBody(emailKey), address);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
