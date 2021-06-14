package com.rookie.send.email.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rookie.send.email.entity.Email;
import com.rookie.send.email.model.SendEmailModel;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public interface EmailService extends IService<Email> {
    void sendEmail (String emailKey, SendEmailModel model) ;

    void sendEmailByThread(ArrayBlockingQueue recevierQueue);

    void sendEmailByApi(List<String> emailList);

    List<Email> getDbRecevierList(String batchNum);

    void sendEmailByDb(List<Email> recevierList);
}
