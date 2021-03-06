package com.rookie.send.email.util;

import com.rookie.send.email.entity.Email;
import com.rookie.send.email.param.BodyType;
import com.rookie.send.email.param.EmailParam;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @Author rookie
 * @Date 2021/5/8 15:42
 * @Description
 **/
@Component
public class ReceiverPool {
    // 接收者账号队列
    public static volatile ArrayBlockingQueue receiverPool = new ArrayBlockingQueue<Email>(200000);

    // 异常接收者账号队列
    public static volatile ArrayBlockingQueue errorReceiverPool = new ArrayBlockingQueue<Email>(200000);

    public static String getTextBody(String emailKey){

//        return EmailUtil.convertTextModel(BodyType.getByCode(emailKey),"二零二一","三叁二九久叁柒零幺");
//        return EmailUtil.convertTextModel(AddresserPool.getRandomContent(),"二零二一","三叁二九久叁柒零幺");
        return AddresserPool.getRandomContent();
    }
}
