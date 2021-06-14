package com.rookie.send.email.util;

import com.rookie.send.email.entity.Email;
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
    public static final int maxSendNum = 10;

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
        String str1 = "这是为您推荐的考试资料，年度卫生资格考试未通过可以加内部老师 332993701/包拿证/";
        String str2 = "哈喽，你好。年度卫生资格考试未通过可以加内部老师 332993701/包拿证/";
        String str3 = "Hi , \n" +
                "Glad to hear that you’re on the market for furniture, we specialize in this field for 14 years, with the strength of ERU&amp;amp;USA ANTIQUE FURNITURE, with good quality and pretty competitive price. \n" +
                "Also we have our own professional designers to meet any of your requirements. \n" +
                "Should you have any questions,call me,let’s talk details. \n" +
                "Best regards! \n" +
                "%MYNAME%\n" +
                "U n s u b s c r i b e";
        String str4 = "内部审核：您2021年度卫生资格考试未通过/内部复核查询成绩/修改成绩/百分百一次合格/加内部老师扣+332993701/包拿证/";
        String str5 = "您好，这是一封通知邮件，2021年度卫生资格考试未通过/内部复核查询成绩/修改成绩/百分百一次合格/加内部老师扣+332993701/包拿证/";
        String str6 = "抱歉，打扰了，这是一封通知邮件，年度卫生资格考试未通过可以加内部老师 332993701/包拿证/";
        String str7 = "尊敬的客户，年度卫生资格考试未通过可以加内部老师 332993701/包拿证/";
        String str8 = "Notice：年度卫生资格考试未通过可以加内部老师 332993701/包拿证/";
        String str9 = "Dear purchasing manager, \n" +
                "Hello, this %MYNAME% from xxx company, our company is a professional xx manufacturer with years's experience. So we want to avail ourselves of opportunity establishing business relation with you. \n" +
                "Please link our company web site:………….if you want to know more about our product.By the way, f-r-e-e sample are available. \n" +
                "Thank you in advance! \n" +
                "Best regards！";
        String str10 = "通知:您2021年度卫生资格考试未通过/内部复核查询成绩/修改成绩/百分百一次合格/加内部老师扣+332993701/包拿证/";
        String str11 = "注意，这是一封通知邮件，2021年度卫生资格考试未通过/内部复核查询成绩/修改成绩/百分百一次合格/加内部老师扣+332993701/包拿证/";
        String str12 = "客户你好，发送此邮件是为了通知你，年度卫生资格考试未通过可以加内部老师 332993701/包拿证/";
        String str13 = "还在为考试发愁么?年度卫生资格考试未通过可以加内部老师 332993701/包拿证/";
        String str14 = "客户你好，打扰了，给你发邮件是想通知你。年度卫生资格考试未通过可以加内部老师 332993701/包拿证/";
        String str15= "努力学习，努力考试，年度卫生资格考试未通过可以加内部老师 332993701/包拿证/";
        String str16 = "你好，没通过考试？不要紧！年度卫生资格考试未通过可以加内部老师 332993701/包拿证/ ";
        String str17= "Hey guy,XYZ trading here, exporting LANTERNS with good quality and low price in US.Call me,let’s talk details.Rgds" +
                        "Contact 332993701";
        String str18 = "你好，给你发邮件是想告诉你，你与卫生资格考试通过只差一个我，可以加内部老师 332993701/包拿证/";
        String str19= "用户你好，卫生考试没通过，可以联系内部老师，332993701/包拿证/";
        String str20 = "2021年度卫生资格考试未通过/内部复核查询成绩/修改成绩/百分百一次合格/加内部老师扣+332993701/包拿证/";
        contentList.add(str1);contentList.add(str2);contentList.add(str3);
        contentList.add(str4);contentList.add(str5);contentList.add(str6);
        contentList.add(str7);contentList.add(str8);contentList.add(str9);
        contentList.add(str10);contentList.add(str11);contentList.add(str12);
        contentList.add(str13);contentList.add(str14);contentList.add(str15);
        contentList.add(str16);contentList.add(str17);contentList.add(str18);
        contentList.add(str19);contentList.add(str20);
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
        for(int i=1; i<=900; i++){
//            String temptitle = getRandomString(6);
//            String temp ="%s-"+temptitle+i+"-%s";
            titleList.add("卫生通知"+i);
        }
    }


    public static String getRandomString(int length){
        String str="abcdefghijklmnopqrstuvwxyz0123456789";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(36);
            sb.append(str.charAt(number));
        }
        return sb.toString();
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

//    public static Map<String, EmailParam> getAddressByDb(Email email){
//
//        Map<String,EmailParam> sendInfo = new HashMap<>();
//        EmailParam emailParam = new EmailParam();
//        String address = email.getSender();
//        Integer errorSendNum = email.getErrorSendNum();
//        AddresserPool.addresserSendCountMap.put(address,new AtomicInteger(0));
//        emailParam.setEmailSender(address);emailParam.setEmailHost("smtp.office365.com");
//        emailParam.setEmailProtocol("smtp");emailParam.setPassword(lineContent[1].trim());
//        emailParam.setEmailNick(address);
//        sendInfo.put(lineContent[0].trim(),emailParam);
//        return sendInfo;
//    }
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

//    public static void main(String[] args) {
////        initTitle();
//        System.out.println(String.format("%s-0e11az900-%s", "hy", "notice"));
////        initAddresser();
////        for(Map<String, EmailParam> map:unUsedAddresserPoolList){
////            unUsedAddresserQueue.offer(map);
////        }
////
////        System.out.println(unUsedAddresserQueue.size());
////        System.out.println(unUsedAddresserQueue.peek());
////        System.out.println(unUsedAddresserQueue.size());
//
////        ArrayBlockingQueue queue = new ArrayBlockingQueue<String>(10000);
////        queue.offer("rookie1");queue.offer("rookie2");queue.offer("rookie3");queue.offer("rookie4");
////
////        System.out.println("取之前长度:"+queue.size());
////        System.out.println(queue.poll());
////        System.out.println(queue.size());
////        System.out.println(queue.poll());
////        System.out.println(queue.size());
////        System.out.println(queue.poll());
////        System.out.println(queue.size());
////        System.out.println(queue.poll());
////        System.out.println(queue.size());
//
//    }

}
