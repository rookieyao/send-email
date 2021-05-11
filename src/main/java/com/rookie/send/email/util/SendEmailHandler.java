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
//    private static String recevier;
    private static String emailKey;
//    private CountDownLatch countDownLatch;

    public SendEmailHandler(Map<String, EmailParam> address, String emailKey) {
//        LOGGER.info("{}对应的当前线程id:{}",getAddresser(address),Thread.currentThread().getId());
        this.address = address;
//        this.recevier = recevier;
        this.emailKey = emailKey;
//        this.countDownLatch = countDownLatch;

    }

    @Override
    public void run() {

        try {

//            ThreadLocalUtil.set(String.valueOf(Thread.currentThread().getName()),address);
//            Map<String, EmailParam> address = (Map<String, EmailParam>)ThreadLocalUtil.getThreadLocal().get(String.valueOf(Thread.currentThread().getId()));


            String addresser = getAddresser(address);
            while(AddresserPool.addresserSendCountMap.get(addresser).intValue() <= AddresserPool.maxSendNum){
                LOGGER.info("{}准备发送邮件，今日已发送次数:{}",addresser,AddresserPool.addresserSendCountMap.get(addresser).intValue());
                AddresserPool.addresserSendCountMap.get(addresser).incrementAndGet();
                sendOneEmail(addresser,address);
                LOGGER.info("{}发送完一封邮件，需要休息10s再次发送",addresser);
                TimeUnit.SECONDS.sleep(10);
            }
            LOGGER.info("{},已到达当天最大发送次数，不让发送邮件!",addresser);
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
    private static void sendOneEmail(String addresser, Map<String, EmailParam> address){
        try{

            String recevier = (String)ReceiverPool.receiverPool.poll();;

            // 发送文本邮件
            EmailUtil.sendEmail01(addresser,recevier, EmailType.getByCode(emailKey),ReceiverPool.getTextBody(emailKey), address);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
