package com.rookie.send.email.service.impl;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.rookie.send.email.model.SendEmailModel;
import com.rookie.send.email.param.BodyType;
import com.rookie.send.email.param.EmailParam;
import com.rookie.send.email.param.EmailType;
import com.rookie.send.email.service.EmailService;
import com.rookie.send.email.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;

@Component
@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class) ;

    @Async("taskExecutor")
    @Override
    public void sendEmail(String emailKey, SendEmailModel model) {
        try{
            // 异步执行
            Thread.sleep(1000);
            String textBody = EmailUtil.convertTextModel(BodyType.getByCode(emailKey),"rookie","测试邮件");
            // 发送文本邮件
//            EmailUtil.sendEmail01(model.getReceiver(), EmailType.getByCode(emailKey),textBody);

            // 发送复杂邮件:文本+图片+附件
            // String body = "自定义图片：<img src='cid:gzh.jpg'/>,网络图片：<img src='http://pic37.nipic.com/20140113/8800276_184927469000_2.png'/>";
            // EmailUtil.sendEmail02(model.getReceiver(),"文本+图片+附件",body);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    static final int nThreads = Runtime.getRuntime().availableProcessors();

    private static ExecutorService sendEmailPool = new ThreadPoolExecutor(100, 100,
            3L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(1024), new ThreadFactoryBuilder()
            .setNameFormat("demo-pool-%d").build(), new ThreadPoolExecutor.AbortPolicy());

//    public void sendEmailByThread1(ArrayBlockingQueue recevierQueue) {
//        ReceiverPool.receiverPool = recevierQueue;
//            while (recevierQueue.size() >0){
//                try {
//
//                    if(!AddresserPool.unUsedAddresserQueue.isEmpty()){
//                        Map<String, EmailParam> address = (Map<String, EmailParam>)AddresserPool.unUsedAddresserQueue.poll();
//
//                        sendEmailPool.execute(new SendEmailHandler(address,EmailType.EMAIL_TEXT_KEY.getCode()));
//                        AddresserPool.usedAddresserQueue.offer(address);
//                    }else{
//
//                        if(!AddresserPool.usedAddresserQueue.isEmpty()){
//                            Map<String, EmailParam> address = (Map<String, EmailParam>)AddresserPool.usedAddresserQueue.poll();
//                            sendEmailPool.execute(new SendEmailHandler(address,EmailType.EMAIL_TEXT_KEY.getCode()));
//                            TimeUnit.SECONDS.sleep(2);
//                        }else{
//                            LOGGER.info("there is an error!");
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//    }

    @Override
    public void sendEmailByThread(ArrayBlockingQueue recevierQueue) {

        if(DateUtil.belongCalendar(DateUtil.getNowTime(),DateUtil.getBeginTime(),DateUtil.getEndTime())){
            LOGGER.info("it is can not to send email at now time:{}",DateUtil.getNowTime());
            return;
        }

        ReceiverPool.receiverPool = recevierQueue;
//        while (AddresserPool.unUsedAddresserPoolList.size() >0){
            try {


                for(Map<String, EmailParam> address:AddresserPool.unUsedAddresserPoolList){
                    sendEmailPool.execute(new SendEmailHandler(address,EmailType.EMAIL_TEXT_KEY.getCode()));
                    // todo 这里之前没有休眠，导致的后果就是SendEmailHandler 执行的时候，发送者的账号会错乱。
                    //  本来应该一个账号一个线程发送一次之后休息指定时间，实际上是一个账号连续发送几次邮件
                    //  感觉这里应该有其他做法的
                    TimeUnit.SECONDS.sleep(3);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
//        }
    }

    @Override
    public void sendEmailByApi(ArrayBlockingQueue recevierQueue) {

        ReceiverPool.receiverPool = recevierQueue;
        try {

            double threadNum = Math.ceil(Double.valueOf(recevierQueue.size()/2));

            LOGGER.info("使用线程数:{}",threadNum);
            for(int i =0; i<threadNum; i++){
                Map<String, EmailParam> address = AddresserPool.getAddressByApi();
                sendEmailPool.execute(new SendEmailHandler(address,EmailType.EMAIL_TEXT_KEY.getCode()));
                TimeUnit.SECONDS.sleep(60);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        ArrayBlockingQueue usedAddresserQueue = new ArrayBlockingQueue<String>(10000);
        System.out.println("empty test:"+usedAddresserQueue.isEmpty());
        System.out.println("size test:"+usedAddresserQueue.size());
        usedAddresserQueue.offer("rookie");
        System.out.println("empty1 test:"+usedAddresserQueue.isEmpty());
        System.out.println("size1 test:"+usedAddresserQueue.size());
        System.out.println(usedAddresserQueue.poll());
        System.out.println("empty2 test:"+usedAddresserQueue.isEmpty());
        System.out.println("size2 test:"+usedAddresserQueue.size());
    }

}
