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

        try {

            if(AddresserPool.addresserSendCountMap.get(address.entrySet().stream().findFirst()).getAndIncrement() <= AddresserPool.maxSendNum){
                sendOneEmail();
            }else{
                LOGGER.info("address:{},已到达当天最大发送次数，不让发送邮件!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    static Object object = new Object();
    /**  发送邮件的方法 */
    private static void sendOneEmail(){
        try{

            String recevier = (String)ReceiverPool.receiverPool.poll();
            LOGGER.info("开始给:{}发送邮件",recevier);
            // 发送文本邮件
            EmailUtil.sendEmail01(recevier, EmailType.getByCode(emailKey),ReceiverPool.getTextBody(emailKey), address);
            LOGGER.info("给:{}发送邮件结束",recevier);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
