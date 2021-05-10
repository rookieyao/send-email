package com.rookie.send.email.util;

import com.rookie.send.email.param.EmailParam;
import com.rookie.send.email.param.EmailType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
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
    private CountDownLatch countDownLatch;

    public SendEmailHandler(Map<String, EmailParam> address, String emailKey, CountDownLatch countDownLatch) {
        this.address = address;
        this.emailKey = emailKey;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {

        try {

            String addresser = getAddresser(address);
            if(AddresserPool.addresserSendCountMap.get(addresser).getAndIncrement() <= AddresserPool.maxSendNum){

                sendOneEmail(addresser);
                LOGGER.info("{}今日发送次数:{}",addresser,AddresserPool.addresserSendCountMap.get(addresser).intValue());
            }else{
                LOGGER.info("address:{},已到达当天最大发送次数，不让发送邮件!",addresser);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            countDownLatch.countDown();
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
    private static void sendOneEmail(String addresser){
        try{

            String recevier = (String)ReceiverPool.receiverPool.poll();
            LOGGER.info("{}开始给:{}发送邮件",addresser, recevier);
            // 发送文本邮件
            EmailUtil.sendEmail01(recevier, EmailType.getByCode(emailKey),ReceiverPool.getTextBody(emailKey), address);
            LOGGER.info("{}给:{}发送邮件结束",addresser, recevier);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
