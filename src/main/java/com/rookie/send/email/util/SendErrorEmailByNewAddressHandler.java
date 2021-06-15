package com.rookie.send.email.util;

import com.alibaba.fastjson.JSON;
import com.rookie.send.email.entity.Email;
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
 * @Date 2021-06-14 17:12
 * @Description
 **/
public class SendErrorEmailByNewAddressHandler implements Runnable{
    private static final Logger LOGGER = LoggerFactory.getLogger(SendErrorEmailByNewAddressHandler.class) ;

    private static String emailKey;

    private EmailErrorService emailErrorService;

    private EmailService emailService;

    public SendErrorEmailByNewAddressHandler(Map<String, EmailParam> addressByApi, EmailErrorService emailErrorService, EmailService emailService, String emailKey) {
        this.emailKey = emailKey;
        this.emailErrorService = emailErrorService;
        this.emailService = emailService;

    }

    @Override
    public void run() {

        try {


            Map<String, EmailParam> addressByApi = AddresserPool.getAddressByApi();
            String addresser = getAddresser(addressByApi);
            int i = 0;
            while(ReceiverPool.errorReceiverPool.size()>0){

                if(i < 10){
                    sendOneEmail(addresser,addressByApi);
                    i++;
                }else{
                    i = 0;
                    addressByApi = AddresserPool.getAddressByApi();
                    addresser = getAddresser(addressByApi);
                    sendOneEmail(addresser,addressByApi);
                }

//                Map<String, EmailParam> addressByApi = AddresserPool.getAddressByApi();
//                String addresser = getAddresser(addressByApi);
//                if(AddresserPool.addresserSendCountMap.get(addresser).intValue() <= AddresserPool.maxSendNum){
////                    LOGGER.info("{}准备发送邮件，今日已发送次数:{}",addresser,AddresserPool.addresserSendCountMap.get(addresser).intValue());
////                    AddresserPool.addresserSendCountMap.get(addresser).incrementAndGet();
//                    sendOneEmail(addresser,addressByApi);
//                }else{
//                    LOGGER.info("{},已到达当天最大发送次数，不让发送邮件!",addresser);
//                    break;
//                }
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
        Email recevier = (Email)ReceiverPool.errorReceiverPool.poll();


        // 发送文本邮件
        String receiverEmail = recevier.getEmail();String title = EmailTiltlePool.getRandomTitle(emailKey);
        String body = ReceiverPool.getTextBody(emailKey);
        String sendInfo = String.format("%sSTARTING TO SEND EMAIL,RECEIVER:%s,TITLE:%s,BODY:%s,address:%s",addresser, receiverEmail,title, body, JSON.toJSONString(address));

        recevier.setSendInfo(sendInfo);
        recevier.setPassword(address.get(addresser).getPassword());
        try {
            EmailUtil emailUtil = new EmailUtil();
            emailUtil.sendEmail01(addresser,recevier.getEmail(),title ,body, address);

            recevier.setStatus(1); //发送成功
            recevier.setMsg("send success!");
            recevier.setSender(addresser);

            emailService.updateById(recevier);
            LOGGER.info("{}发送完一封邮件，需要休息60*4s再次发送",addresser);
            TimeUnit.SECONDS.sleep(60*4);
        } catch (Exception e) {
//            AddresserPool.addresserSendCountMap.get(addresser).decrementAndGet();
            if(recevier !=null){
                //暂时不重新放入队列里面,将出现异常的收件账号存入发送异常的表中
                //                ReceiverPool.receiverPool.offer(recevier);
                recevier.setStatus(2); //发送异常
                recevier.setSender(addresser);
                recevier.setErrorSendNum(1);
                recevier.setMsg(e.getMessage());
                emailService.updateById(recevier);
            }
//            e.printStackTrace();
            LOGGER.error("sendOneEmail出现异常，{},准备60*4s之后继续发送邮件",e.getMessage());
            TimeUnit.SECONDS.sleep(60*4);
        }
    }
}
