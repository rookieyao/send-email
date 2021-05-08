package com.rookie.send.email.util;

import com.rookie.send.email.param.BodyType;
import com.rookie.send.email.param.EmailParam;
import org.springframework.stereotype.Component;

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
    public static ArrayBlockingQueue receiverPool = null;

    public static String getTextBody(String emailKey){
        String textBody = EmailUtil.convertTextModel(BodyType.getByCode(emailKey),"rookie","测试邮件");
        return null;
    }
}
