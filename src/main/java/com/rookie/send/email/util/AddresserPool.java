package com.rookie.send.email.util;

import com.rookie.send.email.model.SendEmailModel;
import com.rookie.send.email.param.EmailParam;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @Author rookie
 * @Date 2021/5/8 10:10
 * @Description 发件邮箱账号池
 **/
@Component
public class AddresserPool implements Addresser {

    // 未使用的账号队列
    public static ArrayBlockingQueue unUsedAddresserQueue = null;
    public static ArrayBlockingQueue usedAddresserQueue = new ArrayBlockingQueue<Map<String, EmailParam>>(10000);

    //当天未使用的发送者邮箱
    public static List<Map<String, EmailParam>> unUsedAddresserPoolList = new ArrayList<>();

    //当天已使用的发送者邮箱
    public static List<Map<String, EmailParam>> usedAddresserPoolMap = new ArrayList<>();

    static {
        initAddresser();
    }

    /**
     * 初始化发送账号池
     */
    private static void initAddresser(){
        ArrayBlockingQueue<Map<String, EmailParam>> queue = FileUtil.readSendersInfo();
        unUsedAddresserQueue = queue;
    }

    @Override
    public Map<String, EmailParam> getAnSendEmailParam() {

        Map<String, EmailParam> poll = null;
        try {
             poll = (Map<String, EmailParam>)unUsedAddresserQueue.poll(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return poll;
    }

    public static void main(String[] args) {
//        initAddresser();
//        for(Map<String, EmailParam> map:unUsedAddresserPoolList){
//            unUsedAddresserQueue.offer(map);
//        }
//
//        System.out.println(unUsedAddresserQueue.size());
//        System.out.println(unUsedAddresserQueue.peek());
//        System.out.println(unUsedAddresserQueue.size());

        ArrayBlockingQueue queue = new ArrayBlockingQueue<String>(10000);
        queue.offer("rookie1");queue.offer("rookie2");queue.offer("rookie3");queue.offer("rookie4");

        System.out.println("取之前长度:"+queue.size());
        System.out.println(queue.poll());
        System.out.println(queue.size());
        System.out.println(queue.poll());
        System.out.println(queue.size());
        System.out.println(queue.poll());
        System.out.println(queue.size());
        System.out.println(queue.poll());
        System.out.println(queue.size());

    }

}
