package com.rookie.send.email.util;

import com.rookie.send.email.entity.Email;
import com.rookie.send.email.mapper.EmailMapper;
import com.rookie.send.email.service.EmailErrorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author rookie
 * @Date 2021-06-06 10:50
 * @Description
 **/
@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class ScheduleSendErrorEmailTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleSendErrorEmailTask.class) ;

    @Autowired
    EmailMapper emailMapper;

    @Autowired
    EmailErrorService emailErrorService;

    /** 每三分钟执行一次 */
    @Scheduled(cron = "0 */3 * * * ?")
    private void configureTasks() {
        Map<String,Object> params = new HashMap<>();
        params.put("status",2);
        List<Email> emails = emailMapper.selectByMap(params);
        if(emails !=null && emails.size() >0){
            emailErrorService.sendEmail(emails);
        }else{
            LOGGER.info("there is no error email！！！");
        }
    }

    /**
     * @描述  每个整点执行一次
     *          执行条件:状态为1(未发送)的，发送队列为空的
     * @参数
     * @返回值
     * @创建人 rookie
     * @创建时间 2021-06-06
     */
    @Scheduled(cron = "0 0 0/1 * * ?")
    private void checkUnSendEmailTasks() {
        Map<String,Object> params = new HashMap<>();
        params.put("status",0);
        List<Email> emails = emailMapper.selectByMap(params);
        if(emails !=null && emails.size() >0 && ReceiverPool.receiverPool.size() == 0){
            emailErrorService.sendUnSendEmail(emails);
        }else{
            LOGGER.info("there is no unsend email！！！");
        }
    }
}
