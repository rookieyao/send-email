package com.rookie.send.email.util;

import com.rookie.send.email.model.SendEmailModel;
import com.rookie.send.email.param.EmailParam;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author rookie
 * @Date 2021/5/8 10:10
 * @Description 发件邮箱账号池
 **/
@Component
public class AddresserPool implements Addresser {

    // 未使用的账号队列
    public static volatile ArrayBlockingQueue unUsedAddresserQueue = null;
    public static volatile ArrayBlockingQueue usedAddresserQueue = new ArrayBlockingQueue<Map<String, EmailParam>>(10000);

    /** 统计当天账号发送次数 */
    public static volatile Map<String, AtomicInteger> addresserSendCountMap = new ConcurrentHashMap<>();

    /** 账号每天最大发送邮件数*/
    public static final int maxSendNum = 3;

    //当天未使用的发送者邮箱
    public static List<Map<String, EmailParam>> unUsedAddresserPoolList = new ArrayList<>();

    //当天已使用的发送者邮箱
    public static List<Map<String, EmailParam>> usedAddresserPoolMap = new ArrayList<>();

    static {
        initAddresser();
        initContent();
    }

    public static List<String> contentList = null;
    private static void initContent(){
        contentList =  new ArrayList<>();
        String str1 = "Dear XXX Jewelry,\n" +
                "\n" +
                "Glad to know you’re co-branding partner with Swarovski, and have a large quantity of stock!\n" +
                "\n" +
                "%s,As the LARGEST BUYER of Swarovski crystals (Dongguan 2014), we produce fashion jewelries since 1999, with competitive price.\n" +
                "\n" +
                "a) TWO SWAROVSKI LICENCES.\n" +
                "b) Jewelleries made with Swarovski crystals, sterling silver 925, zirconia and pearl.\n" +
                "c) More than 120 new arrivals monthly, as well as 15,000 available designs.\n" +
                "d) NO nickle, lead and cadmium (%s)\n" +
                "\n" +
                "REPLY to get E-catalog and FOC samples!";
        String str2 = "Dear \n" +
                "Glad to contact you.Hope you are doing good\n" +
                "Here is Xue update exam answer for 2 years,are you also in this business\n" +
                "Anyway,your earliest reply would be highly appreciated.\n" +
                "Have a nice day\n" +
                "\n" +
                "Thanks & best regards,\n" +
                "\n" +
                "Xue";
        String str3 = "您好：您未痛过%s年度卫生资格考释/乘机/可查/可修改/包拯和个/家内部老师+%s/包拿正/";
        contentList.add(str1);contentList.add(str2);contentList.add(str3);
    }

    public static String getRandomContent(){
        if(contentList == null){
            initContent();
        }
        int size = contentList.size();
        Random rn = new Random();
        int answer = rn.nextInt(size) + 1;
        return contentList.get(answer-1);
    }
    public static List<String> titleList = null;
    private static void initTitle(){
        titleList =  new ArrayList<>();
        String str1 = "%s-%sProposal: Bright Ideas Imports--Zhejiang Textile's Partnership Opportunity";
        String str2 = "%sIntroduction%s: Our Product Offerings for Bright Ideas Imports";
        String str3 = "Advice: Your Product Is Great%s%s";
        titleList.add(str1);titleList.add(str2);titleList.add(str3);
    }

    public static String getRandomTitle(){
        if(titleList == null){
            initTitle();
        }
        int size = titleList.size();
        Random rn = new Random();
        int answer = rn.nextInt(size) + 1;
        return titleList.get(answer-1);
    }


    /**
     * 初始化发送账号池
     */
    private static void initAddresser(){
//        ArrayBlockingQueue<Map<String, EmailParam>> queue = FileUtil.readSendersInfo();
//
//        while (queue.size() >0){
//            unUsedAddresserPoolList.add(queue.poll());
//        }

//        /** 通过api初始化发送者账号 */
//        EmailUtil.initSenderEmailsByApi();

    }


    public static Map<String, EmailParam> getAddressByApi(){

        return EmailUtil.getOneSenderEmailsByApi();
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
        Random rn = new Random();

        int answer = rn.nextInt(1) + 1;
        System.out.println(answer);
//        initAddresser();
//        for(Map<String, EmailParam> map:unUsedAddresserPoolList){
//            unUsedAddresserQueue.offer(map);
//        }
//
//        System.out.println(unUsedAddresserQueue.size());
//        System.out.println(unUsedAddresserQueue.peek());
//        System.out.println(unUsedAddresserQueue.size());

//        ArrayBlockingQueue queue = new ArrayBlockingQueue<String>(10000);
//        queue.offer("rookie1");queue.offer("rookie2");queue.offer("rookie3");queue.offer("rookie4");
//
//        System.out.println("取之前长度:"+queue.size());
//        System.out.println(queue.poll());
//        System.out.println(queue.size());
//        System.out.println(queue.poll());
//        System.out.println(queue.size());
//        System.out.println(queue.poll());
//        System.out.println(queue.size());
//        System.out.println(queue.poll());
//        System.out.println(queue.size());

    }

}
