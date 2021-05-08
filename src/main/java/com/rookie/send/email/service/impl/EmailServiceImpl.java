package com.rookie.send.email.service.impl;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.rookie.send.email.model.SendEmailModel;
import com.rookie.send.email.param.BodyType;
import com.rookie.send.email.param.EmailParam;
import com.rookie.send.email.param.EmailType;
import com.rookie.send.email.service.EmailService;
import com.rookie.send.email.util.AddresserPool;
import com.rookie.send.email.util.EmailUtil;
import com.rookie.send.email.util.ReceiverPool;
import com.rookie.send.email.util.SendEmailHandler;
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

    private static ExecutorService sendEmailPool = new ThreadPoolExecutor(nThreads, nThreads,
            3L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(1024), new ThreadFactoryBuilder()
            .setNameFormat("demo-pool-%d").build(), new ThreadPoolExecutor.AbortPolicy());

    @Override
    public void sendEmailByThread(ArrayBlockingQueue recevierQueue) {
        ReceiverPool.receiverPool = recevierQueue;
        while (!recevierQueue.isEmpty()){
            Map<String, EmailParam> address = (Map<String, EmailParam>)AddresserPool.unUsedAddresserQueue.poll();
            sendEmailPool.execute(new SendEmailHandler(address,EmailType.EMAIL_TEXT_KEY.getCode()));
        }
    }

}
