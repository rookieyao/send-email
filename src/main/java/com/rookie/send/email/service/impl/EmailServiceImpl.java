package com.rookie.send.email.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.rookie.send.email.entity.Email;
import com.rookie.send.email.mapper.EmailErrorMapper;
import com.rookie.send.email.mapper.EmailMapper;
import com.rookie.send.email.model.SendEmailModel;
import com.rookie.send.email.param.BodyType;
import com.rookie.send.email.param.EmailParam;
import com.rookie.send.email.param.EmailType;
import com.rookie.send.email.service.EmailErrorService;
import com.rookie.send.email.service.EmailService;
import com.rookie.send.email.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

@Component
@Service
public class EmailServiceImpl extends ServiceImpl<EmailMapper, Email> implements EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class) ;

    @Autowired
    EmailMapper emailMapper;

    @Autowired
    EmailService emailService;

    @Autowired
    EmailErrorService emailErrorService;

    @Autowired
    EmailErrorMapper emailErrorMapper;

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

    private static ExecutorService sendEmailPool = new ThreadPoolExecutor(500, 500,
            3L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(1024), new ThreadFactoryBuilder()
            .setNameFormat("send-pool-%d").build(), new ThreadPoolExecutor.AbortPolicy());

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
                    sendEmailPool.execute(new SendEmailHandler(emailErrorService, emailService, address,EmailType.EMAIL_TEXT_KEY.getCode()));
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

    static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);

    public String getBatchNum(){
        String s = UUID.randomUUID().toString().replaceAll("-", "");
        return s;

    }

    @Override
    public List<Email> getDbRecevierList(String batchNum) {


        Map<String,Object> params = new HashMap<>();
        params.put("status",0);
        params.put("batch_num",batchNum);
        return emailMapper.selectByMap(params);
//        return emailMapper.getDbRecevierList(batchNum);
    }

    @Override
    public void sendEmailByDb(List<Email> recevierList) {


        for(Email email: recevierList){
            ReceiverPool.receiverPool.offer(email);
        }
        try {

            // 2. 开启线程发送邮件
            double threadNum = Math.ceil(Double.valueOf(ReceiverPool.receiverPool.size())/Double.valueOf(AddresserPool.maxSendNum));

            LOGGER.info("使用线程数:{}",threadNum);
            for(int i =0; i<threadNum; i++){
                Map<String, EmailParam> address = AddresserPool.getAddressByApi();

                sendEmailPool.execute(new SendEmailByNewAddressHandler(address, emailErrorService, emailService,EmailType.EMAIL_TEXT_KEY.getCode()));
                TimeUnit.SECONDS.sleep(15);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendEmailByApi(List<String> emailList) {

        String batchNum = getBatchNum();
        // 1.将要发送的email存入数据库，备份
        try {
            emailService.saveBatch(getEntityList(emailList,batchNum));
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("saveBatch occur an exception!!!");
        }

        List<Email> unSendEmailList = findUnSendEmailListByBatchNum(batchNum);
        for(Email email: unSendEmailList){
            ReceiverPool.receiverPool.offer(email);
        }
//        ReceiverPool.receiverPool = recevierQueue;
        try {

//            BigDecimal threadNum = new BigDecimal(recevierQueue.size()/2);
//            threadNum.setScale(0,BigDecimal.ROUND_UP);

//            int threadNum = 1;
            // 2. 开启线程发送邮件
            double threadNum = Math.ceil(Double.valueOf(ReceiverPool.receiverPool.size())/Double.valueOf(AddresserPool.maxSendNum));

            LOGGER.info("使用线程数:{}",threadNum);
            for(int i =0; i<threadNum; i++){
                Map<String, EmailParam> address = AddresserPool.getAddressByApi();
//                sendEmailPool.execute(new SendEmailHandler(emailErrorService, emailService,address,EmailType.EMAIL_TEXT_KEY.getCode()));
                sendEmailPool.execute(new SendEmailByNewAddressHandler(address, emailErrorService, emailService,EmailType.EMAIL_TEXT_KEY.getCode()));
                TimeUnit.SECONDS.sleep(15);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
//            //3.使用定时线程池定时去获取db中发送失败的邮件
//            scheduledExecutorService.schedule(new Runnable() {
//                @Override
//                public void run() {
//                    Map<String,Object> params = new HashMap<>();
//                    params.put("status",2);
//                    List<Email> emails = emailMapper.selectByMap(params);
//                    if(emails !=null && emails.size() >0){
//                        emailErrorService.sendEmail(emails);
//                    }else{
//                        LOGGER.info("there is no error email！！！");
//                    }
//                }
//            }, 3,TimeUnit.MINUTES);
        }
    }

    private List<Email> findUnSendEmailListByBatchNum(String batchNum) {
        Map<String,Object> params = new HashMap<>();
        params.put("status",0);
        params.put("batch_num",batchNum);
        return emailMapper.selectByMap(params);
    }

    private Collection<Email> getEntityList(List<String> emailList, String batchNum) {

        List<Email> entityList = new ArrayList<>();
        for (String receiver: emailList){
            Email email = new Email();
            email.setEmail(receiver);
            email.setStatus(0);
            email.setBatchNum(batchNum);
            entityList.add(email);
        }
        return entityList;
    }

}
